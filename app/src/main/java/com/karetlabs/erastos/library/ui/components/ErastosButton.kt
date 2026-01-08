package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension

@Composable
fun ErastosButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentDimensions.buttonHeightFilled),
        onClick = onClick,
        shape = RoundedCornerShape(dimension.radiusBase),
        enabled = enabled,
        content = content
    )
}


@Composable
fun ErastosButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
) {
    ErastosButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        content = {
            Text(text = text, style = CustomTypography.buttonText)
        }
    )
}

@DevicePreviews
@Composable
private fun ErastosButtonPreview_Content() {
    ErastosTheme {
        ErastosButton(
            onClick = {},
            content = {
                Text(text = "Button")
            }
        )
    }
}

@DevicePreviews
@Composable
private fun ErastosButtonPreview_Text() {
    ErastosTheme {
        ErastosButton(
            onClick = {},
            text = "Button"
        )
    }
}