package com.karetlabs.erastos.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.karetlabs.erastos.R
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.ErastosButton
import com.karetlabs.erastos.library.ui.components.ErastosLabeledTextField
import com.karetlabs.erastos.library.ui.components.HorizontalSpacer
import com.karetlabs.erastos.library.ui.components.TextFieldLength
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

enum class AddBookMode {
    IsbnScan, ManualEntry
}

@Composable
fun AddBookBottomSheetContent(
    modifier: Modifier = Modifier,
    initialMode: AddBookMode = AddBookMode.IsbnScan,
    onDoneClick: () -> Unit,
    onScanClick: () -> Unit,
    onSearchBookClick: (String) -> Unit,
    onAddBookClick: (
        title: String,
        author: String,
        category: String,
        isbn: String?
    ) -> Unit,
) {
    val dimens = dimension
    val typo = typography

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = colorScheme.surfaceVariant,
        unfocusedContainerColor = colorScheme.surfaceVariant,
        disabledContainerColor = colorScheme.surfaceVariant,
        cursorColor = colorScheme.primary
    )

    var mode by remember { mutableStateOf(initialMode) }

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var manualIsbn by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimens.screenPaddingHorizontal,
                vertical = dimens.screenPaddingVertical
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.add_book), style = typo.headlineSmall)
            TextButton(onClick = onDoneClick) {
                Text(
                    text = stringResource(R.string.add_book_done),
                    style = CustomTypography.buttonText.copy(color = colorScheme.primary)
                )
            }
        }

        Spacer(Modifier.height(dimens.componentSpacingLarge))

        AddBookModeSwitcher(
            selected = mode,
            onSelect = { mode = it }
        )

        Spacer(Modifier.height(dimens.paddingLarge))

        when (mode) {
            AddBookMode.ManualEntry -> ManualEntryContent(
                title = title,
                onTitleChange = { title = it },
                author = author,
                onAuthorChange = { author = it },
                category = category,
                onCategoryChange = { category = it },
                isbn = manualIsbn,
                onIsbnChange = { manualIsbn = it },
                textFieldColors = textFieldColors,
                onAddBookClick = {
                    onAddBookClick(
                        title,
                        author,
                        category,
                        manualIsbn.ifBlank { null }
                    )
                }
            )

            AddBookMode.IsbnScan -> SearchOrScanNewBookContent(
                textFieldColors = textFieldColors,
                onScanClick = onScanClick,
                onSearchBookClick = onSearchBookClick
            )
        }
    }
}

@Composable
private fun AddBookModeSwitcher(
    selected: AddBookMode,
    onSelect: (AddBookMode) -> Unit
) {
    val dimens = dimension

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusRounded),
        color = colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .padding(dimens.paddingTiny)
                .fillMaxWidth()
        ) {
            ModeChip(
                text = stringResource(R.string.add_book_mode_search),
                isSelected = selected == AddBookMode.IsbnScan,
                onClick = { onSelect(AddBookMode.IsbnScan) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(dimens.paddingTiny))
            ModeChip(
                text = stringResource(R.string.add_book_mode_manual_entry),
                isSelected = selected == AddBookMode.ManualEntry,
                onClick = { onSelect(AddBookMode.ManualEntry) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = dimension

    val bg =
        if (isSelected) colorScheme.background
        else colorScheme.surfaceVariant

    val border =
        if (isSelected) BorderStroke(dimens.dividerThickness, colorScheme.outline.copy(alpha = 0.3f))
        else null

    Surface(
        modifier = modifier
            .height(ComponentDimensions.buttonHeightText)
            .clip(RoundedCornerShape(dimens.radiusRounded))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.radiusRounded),
        color = bg,
        border = border
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = CustomTypography.chipLabel.copy(
                    color = if (isSelected)
                        colorScheme.primary
                    else
                        colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ManualEntryContent(
    title: String,
    onTitleChange: (String) -> Unit,
    author: String,
    onAuthorChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    isbn: String,
    onIsbnChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onAddBookClick: () -> Unit
) {
    val dimens = dimension

    Column {
        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_title),
            value = title,
            onValueChange = onTitleChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_author),
            value = author,
            onValueChange = onAuthorChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_category),
            value = category,
            onValueChange = onCategoryChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_isbn_optional),
            value = isbn,
            onValueChange = onIsbnChange,
            fieldLength = TextFieldLength.ISBN,
            colors = textFieldColors,
            keyboardType = KeyboardType.Number,
            placeholder = stringResource(R.string.add_book_enter_isbn_if_available)
        )

        Spacer(Modifier.height(dimens.paddingLarge))

        ErastosButton(
            onClick = onAddBookClick,
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.add_book),
        )
    }
}

@Composable
private fun SearchOrScanNewBookContent(
    textFieldColors: TextFieldColors,
    onScanClick: () -> Unit,
    onSearchBookClick: (String) -> Unit
) {
    val dimens = dimension
    var query by remember { mutableStateOf("") }

    Column {
        ErastosButton(
            onClick = onScanClick,
            modifier = Modifier
                .fillMaxWidth(),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_barcode_scanner),
                        contentDescription = null,
                        Modifier.size(dimension.iconBase)
                    )
                    HorizontalSpacer(dimension.componentSpacingSmall)
                    Text(
                        text = "Scan Barcode",
                        style = CustomTypography.buttonText
                    )
                }
            }
        )

        Spacer(Modifier.height(dimens.componentSpacingLarge))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.add_book_or_enter_manually),
                style = CustomTypography.metadata,
                color = colorScheme.onSurfaceVariant
            )
            HorizontalDivider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(dimens.componentSpacingLarge))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_search_anything_title_author_isbn),
            value = query,
            onValueChange = { query = it },
            fieldLength = TextFieldLength.DEFAULT,
            colors = textFieldColors,
            keyboardType = KeyboardType.Text
        )

        Spacer(Modifier.height(dimens.paddingLarge))

        ErastosButton(
            onClick = {
                onSearchBookClick(query)
            },
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.add_book_search_book),
        )
    }
}

@DevicePreviews
@Composable
private fun AddBookBottomSheetContentPreview() {
    MaterialTheme {
        Surface {
            AddBookBottomSheetContent(
                onDoneClick = {},
                onScanClick = {},
                onSearchBookClick = {},
                onAddBookClick = { _, _, _, _ -> }
            )
        }
    }
}