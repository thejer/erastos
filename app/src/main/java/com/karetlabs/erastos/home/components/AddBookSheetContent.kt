package com.karetlabs.erastos.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.R
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.home.presentation.AddMode
import com.karetlabs.erastos.home.presentation.ManualBookField
import com.karetlabs.erastos.home.presentation.ManualBookFormState
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.CategoryChip
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
    initialMode: AddBookMode = AddBookMode.ManualEntry,
    bookshelves: List<Bookshelf> = emptyList(),
    onDoneClick: () -> Unit,
    onScanClick: () -> Unit,
    onSearchBookClick: (String) -> Unit,
    form: ManualBookFormState,
    onManualBookFieldChange: (ManualBookField, String) -> Unit,
    book: Book? = null,
    onAddBookClick: (book: Book, bookShelfName: String) -> Unit,
    onDeleteBookClick: (Book) -> Unit = {}
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

    var mode by remember(book, initialMode) { mutableStateOf(initialMode) }

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
            Text(
                text = stringResource(if (book == null) R.string.add_book else R.string.edit_book),
                style = typo.headlineSmall
            )
            TextButton(onClick = onDoneClick) {
                Text(
                    text = stringResource(R.string.add_book_done),
                    style = CustomTypography.buttonText.copy(color = colorScheme.primary)
                )
            }
        }

        Spacer(Modifier.height(dimens.componentSpacingLarge))

        if (book == null) {
            AddBookModeSwitcher(
                selected = mode,
                onSelect = { mode = it }
            )
            Spacer(Modifier.height(dimens.paddingLarge))
        }

        when (mode) {
            AddBookMode.ManualEntry -> ManualEntryContent(
                title = form.title,
                author = form.author,
                category = form.category,
                isbn = form.isbn,
                coverUrl = form.coverUrl,
                description = form.description,
                pageCount = form.pageCount,
                publishedDate = form.publishedDate,
                publisher = form.publisher,
                textFieldColors = textFieldColors,
                bookshelves = bookshelves,
                selectedBookshelf = bookshelves.find { it.name.equals(form.category, ignoreCase = true) },
                canAddBook = form.title.isNotBlank(),
                submitLabel = stringResource(if (book == null) R.string.add_book else R.string.save_book_changes),
                isEditing = book != null,
                onDeleteBookClick = { book?.let(onDeleteBookClick) },
                onFieldChange = onManualBookFieldChange,
                onAddBookClick = {
                    onAddBookClick(
                        Book(
                            title = form.title,
                            authors = form.author
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() },
                            isbn = form.isbn.ifBlank { null },
                            coverUrl = form.coverUrl.ifBlank { null },
                            description = form.description.ifBlank { null },
                            pageCount = form.pageCount.toIntOrNull(),
                            publishedDate = form.publishedDate.ifBlank { null },
                            publisher = form.publisher.ifBlank { null },
                            addMode = book?.addMode ?: AddMode.MANUAL.mode,
                            volumeId = book?.volumeId.orEmpty(),
                            id = book?.id ?: 0,
                            bookshelfId = bookshelves.find { it.name == form.category }?.id ?: 0
                        ),
                        form.category
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
        if (isSelected) BorderStroke(
            dimens.dividerThickness,
            colorScheme.outline.copy(alpha = 0.3f)
        )
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
    author: String,
    category: String,
    isbn: String,
    coverUrl: String,
    description: String,
    pageCount: String,
    publishedDate: String,
    onFieldChange: (ManualBookField, String) -> Unit,
    publisher: String,
    textFieldColors: TextFieldColors,
    onAddBookClick: () -> Unit,
    bookshelves: List<Bookshelf> = emptyList(),
    selectedBookshelf: Bookshelf? = null,
    canAddBook: Boolean,
    submitLabel: String,
    isEditing: Boolean,
    onDeleteBookClick: () -> Unit
) {
    val dimens = dimension
    var showDeleteConfirmation by remember(isEditing) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_title),
            placeholder = stringResource(R.string.add_book_title_label),
            value = title,
            onValueChange = {
                onFieldChange(ManualBookField.Title, it)
            },
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_authors),
            placeholder = stringResource(R.string.add_book_authors_label),
            value = author,
            onValueChange = {
                onFieldChange(ManualBookField.Author, it)
            },
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_bookshelf),
            value = category,
            onValueChange = {
                onFieldChange(ManualBookField.Category, it)
            },
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimension.componentSpacingSmall),
            contentPadding = PaddingValues(vertical = dimension.paddingTiny)
        ) {
            items(bookshelves) { bookshelf ->
                CategoryChip(
                    text = bookshelf.name,
                    selected = bookshelf == selectedBookshelf,
                    textStyle = CustomTypography.fieldLabel,
                    height = ComponentDimensions.fieldChipHeight,
                    onClick = { onFieldChange(ManualBookField.Category, bookshelf.name) }
                )
            }
        }

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = stringResource(R.string.add_book_isbn_optional),
            value = isbn,
            onValueChange = {
                onFieldChange(ManualBookField.ISBN, it)
            },
            fieldLength = TextFieldLength.ISBN,
            colors = textFieldColors,
            keyboardType = KeyboardType.Number,
            placeholder = stringResource(R.string.add_book_enter_isbn_if_available)
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))
        Text(
            text = "Book Cover",
            style = CustomTypography.fieldLabel,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(dimens.componentSpacingSmall))

        BookCoverUploadCard(
            coverUrl = coverUrl,
            onCoverUrlChange = {
                onFieldChange(ManualBookField.CoverUrl, it)
            }
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))


        ErastosLabeledTextField(
            label = "Description",
            value = description,
            onValueChange = {
                onFieldChange(ManualBookField.Description, it)
            },
            fieldLength = TextFieldLength.LONG_TEXT,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            singleLine = false,
            height = 160.dp
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = "Page Count",
            value = pageCount,
            onValueChange = {
                onFieldChange(ManualBookField.PageCount, it)
            },
            fieldLength = TextFieldLength.DEFAULT,
            colors = textFieldColors,
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = "Published Date",
            value = publishedDate,
            onValueChange = {
                onFieldChange(ManualBookField.PublishedDate, it)
            },
            fieldLength = TextFieldLength.DEFAULT,
            colors = textFieldColors
        )

        Spacer(Modifier.height(dimens.componentSpacingMedium))

        ErastosLabeledTextField(
            label = "Publisher",
            value = publisher,
            onValueChange = {
                onFieldChange(ManualBookField.Publisher, it)
            },
            fieldLength = TextFieldLength.NAME,
            colors = textFieldColors,
            keyboardCapitalization = KeyboardCapitalization.Words
        )

        Spacer(Modifier.height(dimens.paddingLarge))

        ErastosButton(
            onClick = onAddBookClick,
            enabled = canAddBook,
            modifier = Modifier
                .fillMaxWidth(),
            text = submitLabel,
        )

        if (isEditing) {
            TextButton(onClick = { showDeleteConfirmation = true }) {
                Text(stringResource(R.string.delete_book))
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(R.string.delete_book)) },
                text = { Text(stringResource(R.string.delete_book_confirmation, title)) },
                confirmButton = {
                    TextButton(onClick = onDeleteBookClick) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
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
            enabled = query.isNotBlank(),
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
                initialMode = AddBookMode.ManualEntry,
                bookshelves = listOf(
                    Bookshelf(name = "All"),
                    Bookshelf(name = "Fiction"),
                    Bookshelf(name = "Romance"),
                    Bookshelf(name = "Fantasy"),
                    Bookshelf(name = "Biography"),
                    Bookshelf(name = "History"),
                ),
                onDoneClick = {},
                onScanClick = {},
                onSearchBookClick = {},
                form = ManualBookFormState(),
                onManualBookFieldChange = { _, _ -> },
                onAddBookClick = { _, _-> }
            )
        }
    }
}
