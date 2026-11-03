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
    data object CloseBookDetailsEvent: LibraryHomeScreenEvent()
    data object EditBookClickEvent: LibraryHomeScreenEvent()
    data object CancelBookEditEvent: LibraryHomeScreenEvent()
    data class SelectSearchBookResult(val book: Book): LibraryHomeScreenEvent()
    data class AddBookManually(val book: Book, val bookshelfName: String): LibraryHomeScreenEvent()
    data class DeleteBookClickEvent(val book: Book): LibraryHomeScreenEvent()
    data class ManualBookFieldChangedEvent(val field: ManualBookField, val value: String): LibraryHomeScreenEvent()
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
    val selectedBook: Book? = null,
    val isBookDetailsVisible: Boolean = false,
    val manualBookForm: ManualBookFormState = ManualBookFormState(),
    val shouldOpenBottomSheet: Boolean = false,
    val bottomSheetType: BottomSheetType = BottomSheetType.NONE,
    val errorMessage: String = ""
): BaseViewState

enum class BottomSheetType {
    ERROR, ADD_BOOK, BOOK_DETAILS, SCAN_ISBN, NEW_BOOK_SEARCH, NONE
}

enum class SearchType {
    TITLE, ISBN, NONE
}

enum class ManualBookField {
    Title, Author, Category, ISBN, CoverUrl, Description, PageCount, PublishedDate, Publisher
}

data class ManualBookFormState(
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val isbn: String = "",
    val coverUrl: String = "",
    val description: String = "",
    val pageCount: String = "",
    val publishedDate: String = "",
    val publisher: String = ""
) {
    companion object {
        fun from(book: Book?, bookshelfName: String = "") = ManualBookFormState(
            title = book?.title.orEmpty(),
            author = book?.authors?.joinToString(", ").orEmpty(),
            category = bookshelfName,
            isbn = book?.isbn.orEmpty(),
            coverUrl = book?.coverUrl.orEmpty(),
            description = book?.description.orEmpty(),
            pageCount = book?.pageCount?.toString().orEmpty(),
            publishedDate = book?.publishedDate.orEmpty(),
            publisher = book?.publisher.orEmpty()
        )
    }
}
