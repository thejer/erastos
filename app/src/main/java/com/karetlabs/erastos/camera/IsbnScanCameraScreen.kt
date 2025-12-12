package com.karetlabs.erastos.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun IsbnScanCameraScreen(
    onClose: () -> Unit,
    onIsbnDetected: (isbn: String) -> Unit
) {
    val dimens = dimension
    val typo = typography
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) requestPermission.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        NoCameraPermissionView(requestPermission, onClose)
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IsbnCameraPreview(
            modifier = Modifier.fillMaxSize(),
            onIsbnDetected = onIsbnDetected
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
        )

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(dimens.spaceBase)
                .size(dimens.iconXl)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                style = typo.large.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }

        ScanFrameOverlay(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = dimens.spaceXl),
            accent = MaterialTheme.colorScheme.primary
        )

        InstructionCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = dimens.spaceXl)
                .padding(bottom = dimens.spaceXl),
            titleStyle = typo.titleMedium,
            bodyStyle = typo.bodyMedium
        )
    }
}

@Composable
private fun NoCameraPermissionView(
    requestPermission: ManagedActivityResultLauncher<String, Boolean>,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimension.spaceXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Camera permission is required to scan ISBN barcodes.",
                style = typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )

            Spacer(Modifier.height(dimension.spaceBase))

            Button(onClick = { requestPermission.launch(Manifest.permission.CAMERA) }) {
                Text("Allow camera")
            }

            Spacer(Modifier.height(dimension.spaceSmall))

            TextButton(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun InstructionCard(
    modifier: Modifier,
    titleStyle: TextStyle,
    bodyStyle: TextStyle
) {
    val dimens = dimension

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusXl),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimens.spaceLarge,
                vertical = dimens.spaceBase
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "▮▮▮",
                style = bodyStyle.copy(color = MaterialTheme.colorScheme.primary)
            )

            Spacer(Modifier.height(dimens.spaceSmall))

            Text(
                text = "Align barcode within frame",
                style = titleStyle.copy(color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(Modifier.height(dimens.spaceXSmall))

            Text(
                text = "ISBN will be detected automatically",
                style = bodyStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Composable
private fun ScanFrameOverlay(
    modifier: Modifier,
    accent: Color
) {
    val dimens = dimension
    val frameHeight = 220.dp
    val corner = 28.dp
    val stroke = 4.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(frameHeight),
        contentAlignment = Alignment.Center
    ) {
        Brackets(
            modifier = Modifier.fillMaxSize(),
            color = accent,
            stroke = stroke,
            corner = corner
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .padding(horizontal = dimens.spaceXl)
                .background(accent)
        )
    }
}

@Composable
private fun Brackets(
    modifier: Modifier,
    color: Color,
    stroke: Dp,
    corner: Dp
) {
    Box(modifier = modifier) {
        fun h(width: Dp) = Modifier.width(width).height(stroke).background(color)
        fun v(height: Dp) = Modifier.width(stroke).height(height).background(color)

        Box(Modifier.align(Alignment.TopStart).then(h(corner)))
        Box(Modifier.align(Alignment.TopStart).then(v(corner)))

        Box(Modifier.align(Alignment.TopEnd).then(h(corner)))
        Box(Modifier.align(Alignment.TopEnd).then(v(corner)))

        Box(Modifier.align(Alignment.BottomStart).then(h(corner)))
        Box(Modifier.align(Alignment.BottomStart).then(v(corner)))

        Box(Modifier.align(Alignment.BottomEnd).then(h(corner)))
        Box(Modifier.align(Alignment.BottomEnd).then(v(corner)))
    }
}