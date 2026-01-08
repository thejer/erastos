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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import com.karetlabs.erastos.R
import com.karetlabs.erastos.library.ui.components.ErastosButton
import com.karetlabs.erastos.library.ui.theme.Border
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
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
                .padding(dimens.paddingMedium)
                .size(dimens.iconXl)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                style = typo.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }

        ScanFrameOverlay(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = dimens.screenPaddingHorizontal),
            accent = MaterialTheme.colorScheme.primary
        )

        InstructionCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = dimens.screenPaddingHorizontal)
                .padding(bottom = dimens.paddingLarge),
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
                .padding(dimension.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.isbn_scan_camera_permission_prompt),
                style = typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )

            Spacer(Modifier.height(dimension.componentSpacingLarge))

            ErastosButton(
                onClick = { requestPermission.launch(Manifest.permission.CAMERA) },
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.isbn_scan_allow_camera),
            )

            Spacer(Modifier.height(dimension.componentSpacingSmall))

            TextButton(onClick = onClose) {
                Text(stringResource(R.string.isbn_scan_camera_close))
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
                horizontal = dimens.paddingMedium,
                vertical = dimens.paddingMedium
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_barcode_scanner),
                contentDescription = null,
                modifier = Modifier.size(dimens.iconLarge),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(dimens.componentSpacingSmall))

            Text(
                text = stringResource(R.string.isbn_scan_align_barcode_within_frame),
                style = titleStyle.copy(color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(Modifier.height(dimens.paddingTiny))

            Text(
                text = stringResource(R.string.isbn_scan_isbn_will_be_detected_automatically),
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
    val frameHeight = ComponentDimensions.bookCoverHeightMedium + dimens.space28
    val corner = dimens.space28
    val stroke = dimens.space4

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
                .height(Border.medium)
                .padding(horizontal = dimens.screenPaddingHorizontal)
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
        fun h(width: Dp) = Modifier
            .width(width)
            .height(stroke)
            .background(color)
        fun v(height: Dp) = Modifier
            .width(stroke)
            .height(height)
            .background(color)

        Box(Modifier
            .align(Alignment.TopStart)
            .then(h(corner)))
        Box(Modifier
            .align(Alignment.TopStart)
            .then(v(corner)))

        Box(Modifier
            .align(Alignment.TopEnd)
            .then(h(corner)))
        Box(Modifier
            .align(Alignment.TopEnd)
            .then(v(corner)))

        Box(Modifier
            .align(Alignment.BottomStart)
            .then(h(corner)))
        Box(Modifier
            .align(Alignment.BottomStart)
            .then(v(corner)))

        Box(Modifier
            .align(Alignment.BottomEnd)
            .then(h(corner)))
        Box(Modifier
            .align(Alignment.BottomEnd)
            .then(v(corner)))
    }
}