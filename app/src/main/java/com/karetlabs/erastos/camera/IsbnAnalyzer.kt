package com.karetlabs.erastos.camera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

class IsbnAnalyzer(
    private val scanner: BarcodeScanner,
    private val onDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image ?: run {
            image.close()
            return
        }

        val input = InputImage.fromMediaImage(
            mediaImage,
            image.imageInfo.rotationDegrees
        )

        scanner.process(input)
            .addOnSuccessListener { barcodes ->
                val value = barcodes
                    .firstOrNull { !it.rawValue.isNullOrBlank() }
                    ?.rawValue

                if (!value.isNullOrBlank()) {
                    onDetected(value)
                }
            }
            .addOnCompleteListener {
                image.close()
            }
    }
}