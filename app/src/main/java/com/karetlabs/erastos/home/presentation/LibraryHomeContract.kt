package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.library.presentation.mvi.BaseEvent
import com.karetlabs.erastos.library.presentation.mvi.BaseIntent
import com.karetlabs.erastos.library.presentation.mvi.BaseViewState
import com.karetlabs.erastos.network.data.Volume

sealed class LibraryHomeScreenEvent: BaseEvent {
    data object ScreenCreatedEvent: LibraryHomeScreenEvent()
    data class BookShelfSelectEvent(val bookshelf: Bookshelf): LibraryHomeScreenEvent()
    data class SearchQueryChangeEvent(val query: String): LibraryHomeScreenEvent()
    data object AddBookClickEvent: LibraryHomeScreenEvent()
    data object ScanIsbnClickEvent: LibraryHomeScreenEvent()
    data object DismissBottomSheetEvent: LibraryHomeScreenEvent()
    data class StartNewBookSearchEvent(val searchType: SearchType, val query: String): LibraryHomeScreenEvent()
    data class BookClickEvent(val book: Book): LibraryHomeScreenEvent()
    data class SelectSearchBookResult(val book: Book): LibraryHomeScreenEvent()
}

sealed class LibraryHomeScreenIntent: BaseIntent {
    data class BookShelfSelectIntent(val bookshelf: Bookshelf): LibraryHomeScreenIntent()
}

data class HomeScreenViewState(
    val bookshelves: List<Bookshelf> = listOf(Bookshelf(name = "All")),
    val books: List<Book> = emptyList(),
    val newBookSearchResults: List<Book> = emptyList(),
    val rawNewBookSearchResults: List<Volume> = emptyList(),
    val newBookSearchQuery: String = "",
    val newBookSearchType: SearchType = SearchType.NONE,
    val isNewBookSearchLoading: Boolean = false,
    val selectedBookshelf: Bookshelf = Bookshelf(name = "All"),
    val shouldOpenBottomSheet: Boolean = false,
    val bottomSheetType: BottomSheetType = BottomSheetType.NONE,
    val errorMessage: String = ""
): BaseViewState

enum class BottomSheetType {
    ERROR, ADD_BOOK, SCAN_ISBN, NEW_BOOK_SEARCH, NONE
}

enum class SearchType {
    TITLE, ISBN, NONE
}
