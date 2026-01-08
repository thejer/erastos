package com.karetlabs.erastos.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

@Composable
fun IsbnCameraPreview(
    modifier: Modifier,
    onIsbnDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    val lastResultRef = remember { AtomicReference<String?>(null) }
    val lastTsRef = remember { AtomicLong(0L) }

    // Require a short burst of identical reads before accepting to avoid fast misreads.
    val candidateRef = remember { AtomicReference<String?>(null) }
    val candidateCount = remember { AtomicInteger(0) }
    val candidateWindowStartTs = remember { AtomicLong(0L) }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A
            )
            .build()
        BarcodeScanning.getClient(options)
    }

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val hasClosedCamera = remember { AtomicBoolean(false) }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }.also { previewView = it }
        }
    )

    LaunchedEffect(previewView) {
        val cameraPreview = previewView ?: return@LaunchedEffect

        cameraProviderFuture.addListener(
            {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().apply {
                    surfaceProvider = cameraPreview.surfaceProvider
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(
                    analysisExecutor,
                    IsbnAnalyzer(
                        scanner = scanner,
                        onDetected = { raw ->
                            if (hasClosedCamera.get()) return@IsbnAnalyzer

                            val digits = raw.filter(Char::isDigit)
                            val now = System.currentTimeMillis()

                            if (digits.length !in setOf(10, 13)) return@IsbnAnalyzer

                            if (!isValidIsbn(digits)) return@IsbnAnalyzer

                            val previousCandidate = candidateRef.get()
                            if (previousCandidate == digits) {
                                val start = candidateWindowStartTs.get()
                                if (start == 0L || now - start > 700L) {
                                    candidateWindowStartTs.set(now)
                                    candidateCount.set(1)
                                } else {
                                    candidateCount.incrementAndGet()
                                }
                            } else {
                                candidateRef.set(digits)
                                candidateWindowStartTs.set(now)
                                candidateCount.set(1)
                            }

                            if (candidateCount.get() < 2) return@IsbnAnalyzer

                            // Global de-dupe gate (prevents immediate repeats).
                            val last = lastResultRef.get()
                            val lastTs = lastTsRef.get()
                            if (digits == last && now - lastTs <= 1200L) return@IsbnAnalyzer

                            // Accept.
                            lastResultRef.set(digits)
                            lastTsRef.set(now)

                            if (!hasClosedCamera.compareAndSet(false, true)) return@IsbnAnalyzer

                            // Unbind + callback on main thread.
                            mainExecutor.execute {
                                provider.unbindAll()
                                runCatching { analysisExecutor.shutdown() }
                                onIsbnDetected(digits)
                            }
                        }
                    )
                )

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            },
            mainExecutor
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProvider?.unbindAll() }
            runCatching { analysisExecutor.shutdown() }
        }
    }
}

private fun isValidIsbn(isbnDigits: String): Boolean {
    return when (isbnDigits.length) {
        10 -> isValidIsbn10(isbnDigits)
        13 -> isValidIsbn13(isbnDigits)
        else -> false
    }
}

private fun isValidIsbn10(isbn: String): Boolean {
    // ISBN-10: sum(d[i] * (10 - i)) % 11 == 0
    if (isbn.length != 10) return false
    if (!isbn.all(Char::isDigit)) return false

    var sum = 0
    for (i in 0 until 10) {
        val digit = isbn[i] - '0'
        sum += digit * (10 - i)
    }
    return sum % 11 == 0
}

private fun isValidIsbn13(isbn: String): Boolean {
    // ISBN-13 uses EAN-13 checksum (mod 10) with weights 1 and 3.
    if (isbn.length != 13) return false
    if (!isbn.all(Char::isDigit)) return false

    var sum = 0
    for (i in 0 until 12) {
        val digit = isbn[i] - '0'
        sum += if (i % 2 == 0) digit else digit * 3
    }
    val check = (10 - (sum % 10)) % 10
    return check == (isbn[12] - '0')
}