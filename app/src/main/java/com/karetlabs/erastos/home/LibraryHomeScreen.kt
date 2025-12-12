package com.karetlabs.erastos.home

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.karetlabs.erastos.R
import com.karetlabs.erastos.camera.IsbnScanCameraScreen
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.home.presentation.BottomSheetType.ADD_BOOK
import com.karetlabs.erastos.home.presentation.BottomSheetType.ERROR
import com.karetlabs.erastos.home.presentation.BottomSheetType.NONE
import com.karetlabs.erastos.home.presentation.BottomSheetType.SCAN_ISBN
import com.karetlabs.erastos.home.presentation.BottomSheetType.SEARCHING
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookShelfSelectEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DismissBottomSheetEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScanIsbnClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScreenCreatedEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenIntent.BookShelfSelectIntent
import com.karetlabs.erastos.home.presentation.LibraryHomeViewModel
import com.karetlabs.erastos.library.presentation.doOnScreenCreate
import com.karetlabs.erastos.library.presentation.mvi.observableViewState
import com.karetlabs.erastos.library.presentation.mvi.subscribeForIntents
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.CategoryChip
import com.karetlabs.erastos.library.ui.components.ErastosBottomSheet
import com.karetlabs.erastos.library.ui.components.ErastosSearchField
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
        onAddBookClick = { viewModel.onEvent(AddBookClickEvent)}
    )

    ErastosBottomSheet(
        visible = viewState.shouldOpenBottomSheet,
        onDismissRequest = {viewModel.onEvent(DismissBottomSheetEvent)}
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
                    onIsbnDetected = {
                        Log.d("jerrydev", "LibraryHomeScreen: ISBN $it")
                        viewModel.onEvent(DismissBottomSheetEvent)
                    }
                )
            }
            SEARCHING -> {
                AddBookBottomSheetContent(
                    onDoneClick = { viewModel.onEvent(DismissBottomSheetEvent) },
                    onScanClick = { viewModel.onEvent(ScanIsbnClickEvent) },
                    onSearchBookClick = {},
                    onAddBookClick = { _, _, _, _ -> }
                )
            }
            NONE -> {}

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
                        horizontal = dimension.spaceBase,
                        vertical = dimension.spaceSmall
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimension.spaceSmall)
                ) {
                    Icon(
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(dimension.iconMedium),
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )

                    Text(
                        text = "Add Book",
                        style = typography.bodyMedium,
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
                        horizontal = dimension.spaceBase,
                        vertical = dimension.spaceSmall
                    ),
                horizontalArrangement = Arrangement.spacedBy(dimension.spaceSmall),
                contentPadding = PaddingValues(vertical = 2.dp)
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
                Text("No books yet. Click '+' to add one!")
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
            horizontal = dimension.spaceBase,
            vertical = dimension.spaceSmall
        ),
        verticalArrangement = Arrangement.spacedBy(dimension.spaceBase),
        horizontalArrangement = Arrangement.spacedBy(dimension.spaceBase),
    ) {

        items(books, key = { it.title + it.author }) { book ->
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
            .padding(horizontal = dimension.spaceBase)
            .padding(top = dimension.spaceBase, bottom = dimension.spaceSmall),
    ) {

        Text(
            text = stringResource(R.string.app_name_erastos),
            style = typography.headlineLarge,
            color = colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_book),
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )

            Text(
                text = "$bookCount books in your collection",
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(dimension.spaceSmall))


        ErastosSearchField(
            state = rememberTextFieldState(""),
            placeholderText = stringResource(R.string.search_your_library),
            onFocusEvent = {},
            onTextChanged = {}
        )

        Spacer(Modifier.height(dimension.spaceSmall))
    }
}

@Composable
private fun previewBooks(): List<Book> = listOf(
    Book(
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        coverUrl = "http://books.google.com/books/content?id=WB5_8THW0dEC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 1
    ),
    Book(
        title = "Sapiens",
        author = "Yuval Noah Harari",
        coverUrl = "http://books.google.com/books/content?id=QoeDDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 3
    ),
    Book(
        title = "Atomic Habits",
        author = "James Clear",
        coverUrl = "http://books.google.com/books/content?id=o7kYEQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 5
    ),
    Book(
        title = "Steve Jobs",
        author = "Walter Isaacson",
        coverUrl = "http://books.google.com/books/content?id=tzWkDQAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        bookshelfId = 2
    ),
)


@Preview(showBackground = true)
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
            books = previewBooks(),
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
