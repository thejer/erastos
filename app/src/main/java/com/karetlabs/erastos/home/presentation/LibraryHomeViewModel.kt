package com.karetlabs.erastos.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.home.presentation.BottomSheetType.ERROR
import com.karetlabs.erastos.home.presentation.BottomSheetType.NEW_BOOK_SEARCH
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookManually
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookShelfSelectEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DismissBottomSheetEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScanIsbnClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScreenCreatedEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SearchQueryChangeEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SelectSearchBookResult
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.StartNewBookSearchEvent
import com.karetlabs.erastos.library.presentation.mvi.HasEvents
import com.karetlabs.erastos.library.presentation.mvi.HasIntents
import com.karetlabs.erastos.library.presentation.mvi.HasIntentsImpl
import com.karetlabs.erastos.library.presentation.mvi.HasState
import com.karetlabs.erastos.library.presentation.mvi.HasStateImpl
import com.karetlabs.erastos.network.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val getBooksAndBookShelvesPairUseCase: GetBooksAndBookShelvesPairUseCase,
    private val searchNewBookUseCase: SearchNewBookUseCase,
    private val addBookshelfUseCase: AddBookshelfUseCase,
    private val addBookUseCase: AddBookUseCase
) : ViewModel(),
    HasEvents<LibraryHomeScreenEvent>,
    HasIntents<LibraryHomeScreenIntent> by HasIntentsImpl(),
    HasState<HomeScreenViewState> by HasStateImpl(HomeScreenViewState()) {

    override fun onEvent(event: LibraryHomeScreenEvent) {
        when (event) {
            ScreenCreatedEvent -> onCreate()
            AddBookClickEvent -> showHideBottomSheet(true, BottomSheetType.ADD_BOOK)
            DismissBottomSheetEvent -> showHideBottomSheet(false, BottomSheetType.NONE)
            ScanIsbnClickEvent -> showHideBottomSheet(true, BottomSheetType.SCAN_ISBN)
            is StartNewBookSearchEvent -> searchBooks(event.searchType, event.query)
            is BookClickEvent -> {}
            is BookShelfSelectEvent -> {}
            is SearchQueryChangeEvent -> {}
            is SelectSearchBookResult -> addBookToLibrary(event.book)
            is AddBookManually -> manuallyAddBook(event.book, event.bookshelfName)
        }
    }

    private fun manuallyAddBook(book: Book, bookshelfName: String) {
        val bookshelf = Bookshelf(name = bookshelfName)
        viewModelScope.launch {
            val existingBookshelf = viewState.bookshelves.find { it.name == bookshelf.name }
            val newBookshelfId = existingBookshelf?.id ?: addBookshelfUseCase(bookshelf).toInt()
            addBookUseCase(book.copy(bookshelfId = newBookshelfId))
            showHideBottomSheet(false, BottomSheetType.NONE)
        }
    }

    private fun addBookToLibrary(book: Book) {
        if (viewState.books.find { it.isbn == book.isbn } != null) {
            showHideBottomSheet(false, BottomSheetType.NONE)
            return
        }
        val category = viewState.rawNewBookSearchResults.find { volume ->
            volume.id == book.volumeId
        }?.volumeInfo?.categories?.firstOrNull() ?: "All"
        manuallyAddBook(book, category)
    }

    private fun showHideBottomSheet(shouldShow: Boolean, sheetType: BottomSheetType) {
        updateState { state ->
            state.copy(shouldOpenBottomSheet = shouldShow, bottomSheetType = sheetType)
        }
    }

    private fun onCreate() {
        viewModelScope.launch {
            getBooksAndBookShelvesPairUseCase().collect {
                updateState { state ->
                    val bookshelves = listOf(Bookshelf(name = "All")) + it.second
                    state.copy(
                        books = it.first,
                        bookshelves = bookshelves
                    )
                }
            }
        }
    }

    fun searchBooks(type: SearchType, query: String) {
        val formattedQuery = when (type) {
            SearchType.TITLE -> query
            SearchType.ISBN -> "isbn:$query"
            else -> return
        }
        viewModelScope.launch {
            updateState { current ->
                current.copy(
                    newBookSearchQuery = formattedQuery,
                    newBookSearchType = type,
                    newBookSearchResults = emptyList(),
                    isNewBookSearchLoading = true,
                    shouldOpenBottomSheet = true,
                    bottomSheetType = NEW_BOOK_SEARCH
                )
            }
            when (val result = searchNewBookUseCase(formattedQuery)) {
                is DataResult.Success -> {
                    updateState { current ->
                        val addMode = if (type == SearchType.TITLE) AddMode.SEARCH else AddMode.SCAN
                        current.copy(
                            newBookSearchResults = result.data.toBooks(addMode),
                            rawNewBookSearchResults = result.data.items.orEmpty(),
                            isNewBookSearchLoading = false,
                        )
                    }
                }

                is DataResult.Failure -> {
                    updateState { current ->
                        current.copy(
                            newBookSearchResults = emptyList(),
                            isNewBookSearchLoading = false,
                            shouldOpenBottomSheet = true,
                            bottomSheetType = ERROR,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}