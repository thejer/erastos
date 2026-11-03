package com.karetlabs.erastos.home.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.karetlabs.erastos.R
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.ErastosLabeledTextField
import com.karetlabs.erastos.library.ui.components.HorizontalSpacer
import com.karetlabs.erastos.library.ui.components.TextFieldLength
import com.karetlabs.erastos.library.ui.theme.Border
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.link
import com.karetlabs.erastos.library.ui.theme.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BookCoverUploadCard(
    coverUrl: String,
    onCoverUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var showUrlDialog by remember { mutableStateOf(false) }
    var tempUrl by remember(coverUrl) { mutableStateOf(coverUrl) }

    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedCoverUri = uri
        }
    )

    val fallbackPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedCoverUri = uri
        }
    )

    fun launchCoverPicker() {
        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            fallbackPicker.launch("image/*")
        }
    }

    LaunchedEffect(selectedCoverUri) {
        val uri = selectedCoverUri ?: run {
            selectedBitmap = null
            return@LaunchedEffect
        }

        selectedBitmap = loadBitmapFromUri(context, uri)
        // If the user picked a local image, clear the URL to avoid ambiguity.
        tempUrl = selectedCoverUri?.toString().orEmpty()
        onCoverUrlChange(selectedCoverUri?.toString().orEmpty())
    }

    if (selectedBitmap != null) {
        ImagePreviewView(
            onCancel = {
                selectedCoverUri = null
                onCoverUrlChange("")
            },
            launchCoverPicker = ::launchCoverPicker,
            selectedBitmap = selectedBitmap!!,
        )
    } else {
        UploadImageView(
            modifier = modifier,
            tempUrl = tempUrl,
            launchCoverPicker = ::launchCoverPicker,
            onShowDialog = { showUrlDialog = true }
        )
    }

    if (showUrlDialog) {
        InputUrlDialog(
            onDismiss = {
                onCoverUrlChange(tempUrl)
                selectedCoverUri = null
                showUrlDialog = false
            },
            tempUrl = tempUrl,
            onConfirm = {
                onCoverUrlChange(it.trim())
                selectedCoverUri = null
                tempUrl = it.trim()
                showUrlDialog = false
            }
        )
    }
}

@Composable
fun InputUrlDialog(
    onDismiss: () -> Unit,
    tempUrl: String,
    onConfirm: (String) -> Unit,
) {
    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = colorScheme.surfaceVariant,
        unfocusedContainerColor = colorScheme.surfaceVariant,
        disabledContainerColor = colorScheme.surfaceVariant,
        cursorColor = colorScheme.primary
    )
    val url = remember(tempUrl) { mutableStateOf(tempUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Book cover image URL") },
        text = {
            ErastosLabeledTextField(
                label = "Paste a direct image URL (https://...)",
                value = url.value,
                textStyle = CustomTypography.metadata,
                onValueChange = {
                    url.value = it
                },
                placeholder = "https://...",
                fieldLength = TextFieldLength.DEFAULT,
                colors = textFieldColors
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(url.value)
                    onDismiss()
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UploadImageView(
    modifier: Modifier = Modifier,
    tempUrl: String,
    launchCoverPicker: () -> Unit,
    onShowDialog: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimension.radiusXl))
                .dashedBorder(
                    strokeWidth = Border.thick,
                    color = colorScheme.outlineVariant,
                    cornerRadius = dimension.radiusXl
                ),
            color = colorScheme.surfaceVariant
        ) {
            val hasUrl = tempUrl.isNotBlank()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimension.space20)
                        .clickable {
                            launchCoverPicker()
                        }
                ) {
                    if (tempUrl.startsWith("http", ignoreCase = true)) {
                        AsyncImage(
                            model = tempUrl,
                            contentDescription = "Book cover preview",
                            placeholder = painterResource(R.drawable.img_book_cover),
                            error = painterResource(R.drawable.img_book_cover),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(ComponentDimensions.bookCoverWidthLarge)
                                .aspectRatio(0.63f)
                                .clip(RoundedCornerShape(dimension.radiusBase))
                        )
                        Spacer(Modifier.height(dimension.componentSpacingSmall))
                    } else Icon(
                        painter = painterResource(R.drawable.ic_cloud_upload),
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(ComponentDimensions.iconSizeMedium)
                    )

                    if (!tempUrl.startsWith("http", ignoreCase = true)) Spacer(Modifier.height(dimension.componentSpacingMedium))

                    if (!tempUrl.startsWith("http", ignoreCase = true)) Text(
                        text = "Click to upload",
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(dimension.paddingTiny))

                    if (!tempUrl.startsWith("http", ignoreCase = true)) Text(
                        text = "PNG or JPG",
                        style = CustomTypography.metadata,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }

                Spacer(Modifier.height(dimension.componentSpacingSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimension.space20)
                        .clickable(onClick = onShowDialog),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (hasUrl) {
                        Text(
                            modifier = Modifier.width(ComponentDimensions.bookCoverHeightMedium),
                            text = tempUrl,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = CustomTypography.metadata,
                            color = link.copy(alpha = 0.75f)
                        )
                    } else {
                        Text(
                            text = "Or Add Url",
                            style = CustomTypography.metadata,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )

                        HorizontalSpacer(dimension.componentSpacingSmall)

                        Icon(
                            modifier = Modifier
                                .size(dimension.iconBase),
                            painter = painterResource(R.drawable.ic_add_link),
                            tint = link,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePreviewView(
    onCancel: () -> Unit,
    launchCoverPicker: () -> Unit,
    selectedBitmap: Bitmap,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.wrapContentSize()) {
        Image(
            bitmap = selectedBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .width(ComponentDimensions.bookCoverWidthLarge)
                .aspectRatio(0.63f)
                .clip(RoundedCornerShape(dimension.radiusXl))
        )

        Box(
            modifier = Modifier
                .width(ComponentDimensions.bookCoverWidthLarge)
                .aspectRatio(0.63f)
                .clip(RoundedCornerShape(dimension.radiusXl))
                .background(colorScheme.scrim.copy(alpha = 0.35f))
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .background(
                        colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp),
                    painter = painterResource(R.drawable.ic_cancel),
                    contentDescription = null,
                    tint = colorScheme.inverseOnSurface
                )
            }

            Box(
                modifier = Modifier
                    .clickable { launchCoverPicker() }
                    .background(
                        colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                        shape = CircleShape
                    )
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp),
                    painter = painterResource(R.drawable.ic_edit_image),
                    contentDescription = null,
                    tint = colorScheme.inverseOnSurface
                )
            }
        }
    }

}

@DevicePreviews
@Composable
private fun BookCoverUploadCardPreview() {
    ErastosTheme {
        Surface {
            BookCoverUploadCard(
                coverUrl = "https://example.com/cover.jpg",
                onCoverUrlChange = {}
            )
        }
    }
}

@Composable
private fun Modifier.dashedBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
    dashLength: Dp = dimension.space8,
    gapLength: Dp = dimension.space4
): Modifier = this.drawWithContent {
    drawContent()
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f
        )
    )
    drawRoundRect(
        color = color,
        topLeft = Offset.Zero,
        size = Size(size.width, size.height),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = stroke
    )
}

private suspend fun loadBitmapFromUri(
    context: android.content.Context,
    uri: Uri
): Bitmap? = withContext(Dispatchers.IO) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (_: Throwable) {
        null
    }
}
