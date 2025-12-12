package com.karetlabs.erastos.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.ErastosLabeledTextField
import com.karetlabs.erastos.library.ui.components.TextFieldLength
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
    onSearchBookClick: (isbn: String) -> Unit,
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
    var scanIsbn by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spaceBase, vertical = dimens.spaceBase)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Add Book", style = typo.headlineMedium)
            TextButton(onClick = onDoneClick) {
                Text(
                    text = "Done",
                    style = typo.labelMedium.copy(color = colorScheme.primary)
                )
            }
        }

        Spacer(Modifier.height(dimens.spaceBase))

        AddBookModeSwitcher(
            selected = mode,
            onSelect = { mode = it }
        )

        Spacer(Modifier.height(dimens.spaceXl))

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

            AddBookMode.IsbnScan -> IsbnScanContent(
                isbn = scanIsbn,
                onIsbnChange = { scanIsbn = it },
                textFieldColors = textFieldColors,
                onScanClick = onScanClick,
                onSearchBookClick = { onSearchBookClick(scanIsbn) }
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
                .padding(dimens.spaceXSmall)
                .fillMaxWidth()
        ) {
            ModeChip(
                text = "ISBN Scan",
                isSelected = selected == AddBookMode.IsbnScan,
                onClick = { onSelect(AddBookMode.IsbnScan) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(dimens.spaceXSmall))
            ModeChip(
                text = "Manual Entry",
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
    val typo = typography

    val bg =
        if (isSelected) colorScheme.background
        else colorScheme.surfaceVariant

    val border =
        if (isSelected) BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.3f))
        else null

    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.radiusRounded),
        color = bg,
        border = border
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = typo.labelMedium.copy(
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
            label = "Title",
            value = title,
            onValueChange = onTitleChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.spaceMedium))

        ErastosLabeledTextField(
            label = "Author",
            value = author,
            onValueChange = onAuthorChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.spaceMedium))

        ErastosLabeledTextField(
            label = "Category",
            value = category,
            onValueChange = onCategoryChange,
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.spaceMedium))

        ErastosLabeledTextField(
            label = "ISBN (Optional)",
            value = isbn,
            onValueChange = onIsbnChange,
            fieldLength = TextFieldLength.ISBN,
            colors = textFieldColors,
            keyboardType = KeyboardType.Number,
            placeholder = "Enter ISBN if available"
        )

        Spacer(Modifier.height(dimens.spaceXl))

        Button(
            onClick = onAddBookClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(dimens.radiusRounded)
        ) {
            Text("Add Book")
        }
    }
}

@Composable
private fun IsbnScanContent(
    isbn: String,
    onIsbnChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onScanClick: () -> Unit,
    onSearchBookClick: () -> Unit
) {
    val dimens = dimension

    Column {
        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(dimens.radiusRounded)
        ) {
            Text("🔍  Scan Barcode")
        }

        Spacer(Modifier.height(dimens.spaceLarge))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(Modifier.weight(1f))
            Text(" or enter manually ")
            Divider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(dimens.spaceLarge))

        ErastosLabeledTextField(
            label = "ISBN Number",
            value = isbn,
            onValueChange = onIsbnChange,
            fieldLength = TextFieldLength.ISBN,
            colors = textFieldColors,
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(dimens.spaceXl))

        Button(
            onClick = onSearchBookClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(dimens.radiusRounded)
        ) {
            Text("Search Book")
        }
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