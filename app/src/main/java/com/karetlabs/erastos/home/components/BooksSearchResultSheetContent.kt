package com.karetlabs.erastos.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.karetlabs.erastos.R
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.home.presentation.SearchType
import com.karetlabs.erastos.library.getAuthorsString
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.AbsoluteVerticalSpacer
import com.karetlabs.erastos.library.ui.components.HorizontalSpacer
import com.karetlabs.erastos.library.ui.components.VerticalSpacer
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography


@Composable
fun BooksSearchResultSheetContent(
    query: String,
    searchType: SearchType,
    isSearching: Boolean,
    foundBooks: List<Book>,
    onContinueClicked: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchCopy = when (searchType) {
        SearchType.ISBN -> stringResource(R.string.new_book_search_copy_isbn)
        SearchType.TITLE -> stringResource(R.string.new_book_search_copy_title)
        else -> Unit
    }
    if (isSearching) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(.55f)
                .padding(horizontal = dimension.screenPaddingHorizontal),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(dimension.space64),
                color = colorScheme.primary,
                strokeWidth = 6.dp
            )
            Spacer(Modifier.height(dimension.componentSpacingLarge))
            Text(
                text = stringResource(R.string.book_search_searching),
                style = typography.headlineSmall,
                color = colorScheme.onSurface
            )
            Spacer(Modifier.height(dimension.componentSpacingMedium))
            Text(
                text = stringResource(R.string.book_search_looking_up, searchCopy, query),
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )
        }
    } else if (foundBooks.isNotEmpty()) {
        SearchResultsView(
            modifier = modifier,
            foundBooks = foundBooks,
            onContinueClicked = onContinueClicked
        )
    } else {
        Text("No books found")
    }
}

@Composable
private fun SearchResultsView(
    modifier: Modifier,
    foundBooks: List<Book>,
    onContinueClicked: (Book) -> Unit
) {
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = dimension.screenPaddingVertical)) {
        Text(
            text = stringResource(
                R.string.search_results_count_books_found,
                foundBooks.size
            ),
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = dimension.screenPaddingHorizontal)
        )

        VerticalSpacer(dimension.componentSpacingSmall)

        HorizontalDivider(color = colorScheme.outlineVariant)

        LazyColumn(
            modifier = modifier.padding(dimension.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimension.componentSpacingSmall)
        ) {
            items(foundBooks) { book ->
                BookSearchResultItem(
                    book = book,
                    isSelected = book == selectedBook,
                    onClick = {
                        selectedBook = if (selectedBook == book) null else book
                    }
                )
            }
        }
        VerticalSpacer(dimension.componentSpacingLarge)
        selectedBook?.let { book ->
            Button(onClick = { onContinueClicked(book) }) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun BookSearchResultItem(
    book: Book,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val coverShape = RoundedCornerShape(dimension.radiusBase)
    val containerShape = RoundedCornerShape(dimension.radiusLarge)

    val containerColor = if (isSelected) {
        colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        colorScheme.surface
    }

    val border = if (isSelected)
        BorderStroke(width = dimension.dividerThickness, color = colorScheme.primary.copy(alpha = 0.25f))
    else null

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(containerShape)
            .clickable(onClick = onClick),
        shape = containerShape,
        color = containerColor,
        border = border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
                .padding(dimension.paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val coverUrl = book.coverUrl.trim()
                AsyncImage(
                    model = coverUrl,
                    error = painterResource(R.drawable.img_book_cover),
                    placeholder = painterResource(R.drawable.img_book_cover),
                    contentDescription = stringResource(R.string.search_result_item_book_cover),
                    modifier = Modifier
                        .width(ComponentDimensions.avatarSizeLarge)
                        .aspectRatio(0.63f)
                        .clip(coverShape),
                    contentScale = ContentScale.Crop
                )

            HorizontalSpacer(dimension.componentSpacingMedium)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimension.paddingMedium),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = book.title,
                    style = CustomTypography.bookTitle,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                VerticalSpacer(height = dimension.paddingTiny)
                val authors = book.getAuthorsString()
                if (authors.isNotBlank()) {
                    Text(
                        text = authors,
                        style = CustomTypography.bookAuthor,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                AbsoluteVerticalSpacer(1f)

                val publisher = book.publisher.trim()
                val year = book.publishedDate.trim()
                val publisherLine = listOf(publisher, year)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                if (publisherLine.isNotBlank()) {
                    Text(
                        text = publisherLine,
                        style = CustomTypography.metadata,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                VerticalSpacer(dimension.paddingTiny)

                if (book.isbn.isNotBlank()) {
                    Text(
                        text = book.isbn,
                        style = CustomTypography.metadata,
                        color = colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.ic_rounded_check),
                    contentDescription = stringResource(R.string.search_result_item_selected),
                    tint = colorScheme.primary,
                    modifier = Modifier.size(dimension.iconBase)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_caret_right),
                    contentDescription = stringResource(R.string.search_result_item_unselected),
                    tint = colorScheme.outline,
                    modifier = Modifier.size(dimension.iconMedium)
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun BookSearchResultItemPreview() {
    val book = Book(
        title = "The Great Gatsby",
        authors = listOf("F. Scott Fitzgerald"),
        coverUrl = "sdkgh3qw2r",
        bookshelfId = 1,
        isbn = "9780743273565",
        description = "The Great Gatsby is a 1925 novel written by American author F. Scott Fitzgerald that follows a cast of characters living in the fictional towns of West",
        pageCount = 180,
        publishedDate = "1925",
        publisher = "Scribner"
    )
    ErastosTheme {
        BookSearchResultItem(book)
    }
}


@Composable
private fun previewBooks(): List<Book> = listOf(
    Book(
        title = "The Great Gatsby",
        authors = listOf("F. Scott Fitzgerald"),
        coverUrl = "http://books.google.com/books/content?id=WB5_8THW0dEC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 1,
        isbn = "9780743273565",
        description = "The Great Gatsby is a 1925 novel written by American author F. Scott Fitzgerald that follows a cast of characters living in the fictional towns of West",
        pageCount = 180,
        publishedDate = "1925",
        publisher = "Scribner"
    ),
    Book(
        title = "Sapiens",
        authors = listOf("Yuval Noah Harari"),
        coverUrl = "http://books.google.com/books/content?id=QoeDDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 3,
        isbn = "9780062316097",
        description = "Sapiens: A Brief History of Humankind is a 201",
        pageCount = 443,
        publishedDate = "2011",
        publisher = "Dutton Books"
    ),
    Book(
        title = "Atomic Habits",
        authors = listOf("James Clear"),
        coverUrl = "http://books.google.com/books/content?id=o7kYEQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 5,
        isbn = "9780735211359",
        description = "Atomic Habits: An Easy & Proven Way to Build Good Habits & Break Bad Ones is a 2018",
        pageCount = 319,
        publishedDate = "2018",
        publisher = "Avery"
    ),
    Book(
        title = "Steve Jobs",
        authors = listOf("Walter Isaacson"),
        coverUrl = "http://books.google.com/books/content?id=tzWkDQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 2,
        isbn = "9780062316097",
        description = "Steve Jobs: The Exclusive Biography of Steve Jobs is a 2019",
        pageCount = 504,
        publishedDate = "2019",
        publisher = "Dutton Books"
    ),
)

@DevicePreviews
@Composable
private fun BooksSearchResultSheetContentPreview() {
    ErastosTheme {
        BooksSearchResultSheetContent(
            query = "9780743273565",
            searchType = SearchType.ISBN,
            isSearching = false,
            foundBooks = previewBooks(),
            onContinueClicked = {}
        )
    }
}


@Composable
private fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(dimension.componentSpacingSmall))
        Text(
            text = value,
            style = typography.bodyLarge,
            color = colorScheme.onSurface
        )
    }
}