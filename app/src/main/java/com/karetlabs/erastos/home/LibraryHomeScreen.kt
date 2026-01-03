package com.karetlabs.erastos.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.karetlabs.erastos.R
import com.karetlabs.erastos.camera.IsbnScanCameraScreen
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.home.components.AddBookBottomSheetContent
import com.karetlabs.erastos.home.components.BookCardItemView
import com.karetlabs.erastos.home.components.BooksSearchResultSheetContent
import com.karetlabs.erastos.home.presentation.BottomSheetType.ADD_BOOK
import com.karetlabs.erastos.home.presentation.BottomSheetType.ERROR
import com.karetlabs.erastos.home.presentation.BottomSheetType.NEW_BOOK_SEARCH
import com.karetlabs.erastos.home.presentation.BottomSheetType.NONE
import com.karetlabs.erastos.home.presentation.BottomSheetType.SCAN_ISBN
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookShelfSelectEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DismissBottomSheetEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScanIsbnClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScreenCreatedEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SelectSearchBookResult
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.StartNewBookSearchEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenIntent.BookShelfSelectIntent
import com.karetlabs.erastos.home.presentation.LibraryHomeViewModel
import com.karetlabs.erastos.home.presentation.SearchType
import com.karetlabs.erastos.library.getAuthorsString
import com.karetlabs.erastos.library.presentation.doOnScreenCreate
import com.karetlabs.erastos.library.presentation.mvi.observableViewState
import com.karetlabs.erastos.library.presentation.mvi.subscribeForIntents
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.CategoryChip
import com.karetlabs.erastos.library.ui.components.ErastosBottomSheet
import com.karetlabs.erastos.library.ui.components.ErastosSearchField
import com.karetlabs.erastos.library.ui.components.VerticalSpacer
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun LibraryHomeScreen(
    viewModel: LibraryHomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.observableViewState()
    doOnScreenCreate {
        viewModel.onEvent(ScreenCreatedEvent)
    }
    viewModel.subscribeForIntents {
        when (it) {
            is BookShelfSelectIntent -> {}
        }
    }
    HomeView(
        bookshelves = viewState.bookshelves,
        books = viewState.books,
        selectedBookshelf = viewState.selectedBookshelf,
        onBookshelfSelected = {
            viewModel.onEvent(BookShelfSelectEvent(it))
        },
        onAddBookClick = { viewModel.onEvent(AddBookClickEvent) }
    )

    ErastosBottomSheet(
        visible = viewState.shouldOpenBottomSheet,
        onDismissRequest = { viewModel.onEvent(DismissBottomSheetEvent) }
    ) {
        when (viewState.bottomSheetType) {
            ERROR -> {}
            ADD_BOOK -> {
                AddBookBottomSheetContent(
                    onDoneClick = { viewModel.onEvent(DismissBottomSheetEvent) },
                    onScanClick = { viewModel.onEvent(ScanIsbnClickEvent) },
                    onSearchBookClick = {},
                    onAddBookClick = { _, _, _, _ -> }
                )
            }

            SCAN_ISBN -> {
                IsbnScanCameraScreen(
                    onClose = { viewModel.onEvent(DismissBottomSheetEvent) },
                    onIsbnDetected = { isbn ->
                        viewModel.onEvent(StartNewBookSearchEvent(searchType = SearchType.ISBN, query = isbn))
                    }
                )
            }

            NEW_BOOK_SEARCH -> {
                BooksSearchResultSheetContent(
                    query = viewState.newBookSearchQuery,
                    searchType = viewState.newBookSearchType,
                    isSearching = viewState.isNewBookSearchLoading,
                    foundBooks = viewState.newBookSearchResults,
                    onContinueClicked = { viewModel.onEvent(SelectSearchBookResult(it)) }
                )
            }

            NONE -> Unit
        }
    }
}

@Composable
private fun HomeView(
    selectedBookshelf: Bookshelf,
    bookshelves: List<Bookshelf>,
    books: List<Book>,
    onBookshelfSelected: (Bookshelf) -> Unit,
    onAddBookClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            HomeTopBar(20)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = dimension.paddingMedium,
                        vertical = dimension.paddingSmall
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimension.componentSpacingSmall)
                ) {
                    Icon(
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(dimension.iconMedium),
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(R.string.home_add_book_button),
                        style = CustomTypography.buttonText,
                        color = colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalDivider(color = colorScheme.outline)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimension.screenPaddingHorizontal,
                        vertical = dimension.paddingSmall
                    ),
                horizontalArrangement = Arrangement.spacedBy(dimension.componentSpacingSmall),
                contentPadding = PaddingValues(vertical = dimension.paddingTiny)
            ) {
                items(bookshelves) { chip ->
                    CategoryChip(
                        text = chip.name,
                        selected = chip == selectedBookshelf,
                        onClick = { onBookshelfSelected(chip) }
                    )
                }
            }

            HorizontalDivider(color = colorScheme.outline)
            if (books.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_empty_book_results),
                        contentDescription = null,
                        tint = colorScheme.outlineVariant,
                        modifier = Modifier.size(108.dp),
                    )

                    VerticalSpacer(dimension.componentSpacingLarge)

                    Text(
                        text = stringResource(R.string.empty_title_no_books),
                        style = typography.headlineSmall,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = dimension.screenPaddingHorizontal)
                    )

                    VerticalSpacer(dimension.componentSpacingMedium)

                    Text(
                        text = stringResource(R.string.empty_library_text),
                        style = CustomTypography.description,
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = dimension.screenPaddingHorizontal)
                    )
                }
            } else {
                BooksGrid(books)
            }
        }
    }
}

@Composable
fun BooksGrid(
    books: List<Book>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimension.screenPaddingHorizontal,
            vertical = dimension.screenPaddingVertical
        ),
        verticalArrangement = Arrangement.spacedBy(dimension.componentSpacingLarge),
        horizontalArrangement = Arrangement.spacedBy(dimension.componentSpacingLarge),
    ) {

        items(books, key = { it.title + it.getAuthorsString() }) { book ->
            BookCardItemView(book = book)
        }
    }
}

@Composable
private fun HomeTopBar(bookCount: Int) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = dimension.screenPaddingHorizontal)
            .padding(top = dimension.screenPaddingVertical, bottom = dimension.paddingSmall),
    ) {

        Text(
            text = stringResource(R.string.app_name_erastos),
            style = typography.headlineLarge,
            color = colorScheme.onBackground
        )

        Spacer(Modifier.height(dimension.componentSpacingSmall))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimension.componentSpacingMedium)
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_book),
                contentDescription = null,
                tint = colorScheme.outline
            )

            Text(
                text = stringResource(R.string.home_page_books_in_your_collection, bookCount),
                style = typography.bodyMedium,
                color = colorScheme.outline
            )
        }

        Spacer(Modifier.height(dimension.componentSpacingSmall))


        ErastosSearchField(
            state = rememberTextFieldState(""),
            placeholderText = stringResource(R.string.search_your_library),
            onFocusEvent = {},
            onTextChanged = {}
        )

        Spacer(Modifier.height(dimension.componentSpacingSmall))
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
fun HomePreview() {
    ErastosTheme {
        HomeView(
            bookshelves = listOf(
                Bookshelf(name = "All"),
                Bookshelf(name = "Fiction"),
            ),
            onBookshelfSelected = {},
            selectedBookshelf = Bookshelf(name = "All"),
            books = emptyList(),
            onAddBookClick = {}
        )
    }
}


@DevicePreviews
@Composable
private fun BooksGridPreview() {
    val books = previewBooks()
    ErastosTheme {
        BooksGrid(books)
    }
}

@DevicePreviews
@Composable
private fun HomeTopBarPreview() {
    ErastosTheme {
        HomeTopBar(10)
    }
}
