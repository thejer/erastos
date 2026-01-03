package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = CustomTypography.chipLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        shape = RoundedCornerShape(dimension.radiusRounded),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colorScheme.primaryContainer,
            selectedLabelColor = colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = colorScheme.onPrimary,
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.35f),
            labelColor = colorScheme.onSurface,
        ),
        border = null,
        modifier = Modifier.height(ComponentDimensions.chipHeight)
    )
}


@DevicePreviews
@Composable
private fun PreviewCategoryChips() {
    ErastosTheme {
        CategoryChip("Category 1", selected = true, onClick = {})
    }
}