package com.karetlabs.erastos.home.presentation

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.home.presentation.BottomSheetType.ERROR
import com.karetlabs.erastos.home.presentation.BottomSheetType.NEW_BOOK_SEARCH
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookManually
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.CloseBookDetailsEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.EditBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.CancelBookEditEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookShelfSelectEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DismissBottomSheetEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DeleteBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScanIsbnClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScreenCreatedEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SearchQueryChangeEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SelectSearchBookResult
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.StartNewBookSearchEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ManualBookFieldChangedEvent
import com.karetlabs.erastos.library.presentation.mvi.HasEvents
import com.karetlabs.erastos.library.presentation.mvi.HasIntents
import com.karetlabs.erastos.library.presentation.mvi.HasIntentsImpl
import com.karetlabs.erastos.library.presentation.mvi.HasState
import com.karetlabs.erastos.library.presentation.mvi.HasStateImpl
import com.karetlabs.erastos.network.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBooksAndBookShelvesPairUseCase: GetBooksAndBookShelvesPairUseCase,
    private val searchNewBookUseCase: SearchNewBookUseCase,
    private val addBookshelfUseCase: AddBookshelfUseCase,
    private val addBookUseCase: AddBookUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel(),
    HasEvents<LibraryHomeScreenEvent>,
    HasIntents<LibraryHomeScreenIntent> by HasIntentsImpl(),
    HasState<HomeScreenViewState> by HasStateImpl(HomeScreenViewState()) {

    override fun onEvent(event: LibraryHomeScreenEvent) {
        when (event) {
            ScreenCreatedEvent -> onCreate()
            AddBookClickEvent -> updateState {
                it.copy(
                    selectedBook = null,
                    isBookDetailsVisible = false,
                    manualBookForm = ManualBookFormState.from(null, it.selectedBookshelf.name.takeUnless { name -> name == "All" }.orEmpty()),
                    shouldOpenBottomSheet = true,
                    bottomSheetType = BottomSheetType.ADD_BOOK
                )
            }
            DismissBottomSheetEvent -> updateState {
                it.copy(
                    selectedBook = null,
                    isBookDetailsVisible = false,
                    manualBookForm = ManualBookFormState(),
                    shouldOpenBottomSheet = false,
                    bottomSheetType = BottomSheetType.NONE
                )
            }
            CloseBookDetailsEvent -> updateState {
                it.copy(
                    selectedBook = null,
                    manualBookForm = ManualBookFormState(),
                    isBookDetailsVisible = false,
                    shouldOpenBottomSheet = false,
                    bottomSheetType = BottomSheetType.NONE
                )
            }
            EditBookClickEvent -> updateState {
                if (it.selectedBook == null) it else it.copy(
                    isBookDetailsVisible = false,
                    shouldOpenBottomSheet = true,
                    bottomSheetType = BottomSheetType.ADD_BOOK
                )
            }
            CancelBookEditEvent -> updateState {
                it.copy(
                    shouldOpenBottomSheet = it.selectedBook != null,
                    bottomSheetType = if (it.selectedBook != null) BottomSheetType.BOOK_DETAILS else BottomSheetType.NONE,
                    isBookDetailsVisible = it.selectedBook != null
                )
            }
            ScanIsbnClickEvent -> showHideBottomSheet(true, BottomSheetType.SCAN_ISBN)
            is StartNewBookSearchEvent -> searchBooks(event.searchType, event.query)
            is BookClickEvent -> updateState {
                val shelfName = it.bookshelves.find { shelf -> shelf.id == event.book.bookshelfId }?.name.orEmpty()
                it.copy(
                    selectedBook = event.book,
                    manualBookForm = ManualBookFormState.from(event.book, shelfName),
                    isBookDetailsVisible = true,
                    shouldOpenBottomSheet = true,
                    bottomSheetType = BottomSheetType.BOOK_DETAILS
                )
            }
            is BookShelfSelectEvent -> updateState { it.copy(selectedBookshelf = event.bookshelf) }
            is SearchQueryChangeEvent -> {}
            is SelectSearchBookResult -> addBookToLibrary(event.book)
            is AddBookManually -> manuallyAddBook(event.book, event.bookshelfName)
            is DeleteBookClickEvent -> deleteBook(event.book)
            is ManualBookFieldChangedEvent -> updateManualBookForm(event.field, event.value)
        }
    }

    private fun updateManualBookForm(field: ManualBookField, value: String) {
        updateState { state ->
            val form = state.manualBookForm
            state.copy(
                manualBookForm = when (field) {
                    ManualBookField.Title -> form.copy(title = value)
                    ManualBookField.Author -> form.copy(author = value)
                    ManualBookField.Category -> form.copy(category = value)
                    ManualBookField.ISBN -> form.copy(isbn = value)
                    ManualBookField.CoverUrl -> form.copy(coverUrl = value)
                    ManualBookField.Description -> form.copy(description = value)
                    ManualBookField.PageCount -> form.copy(pageCount = value)
                    ManualBookField.PublishedDate -> form.copy(publishedDate = value)
                    ManualBookField.Publisher -> form.copy(publisher = value)
                }
            )
        }
    }

    private fun manuallyAddBook(book: Book, bookshelfName: String) {
        if (book.title.isBlank()) return
        if (book.id == 0 && !book.isbn.isNullOrBlank() && viewState.books.any { it.isbn == book.isbn }) {
            updateState {
                it.copy(
                    selectedBook = null,
                    manualBookForm = ManualBookFormState(),
                    isBookDetailsVisible = false,
                    shouldOpenBottomSheet = false,
                    bottomSheetType = BottomSheetType.NONE
                )
            }
            return
        }
        viewModelScope.launch {
            val shelfName = bookshelfName.trim()
            val existingBookshelf = viewState.bookshelves.find { it.name.equals(shelfName, ignoreCase = true) }
            val bookshelfId = when {
                existingBookshelf != null -> existingBookshelf.id
                shelfName.isBlank() || shelfName.equals("All", ignoreCase = true) -> 0
                else -> addBookshelfUseCase(Bookshelf(name = shelfName)).toInt()
            }
            val savedBook = book.copy(coverUrl = persistLocalCover(book.coverUrl), bookshelfId = bookshelfId)
            if (book.id == 0) addBookUseCase(savedBook) else updateBookUseCase(savedBook)
            updateState {
                it.copy(
                    selectedBook = null,
                    manualBookForm = ManualBookFormState(),
                    isBookDetailsVisible = false,
                    shouldOpenBottomSheet = false,
                    bottomSheetType = BottomSheetType.NONE
                )
            }
        }
    }

    private fun deleteBook(book: Book) {
        viewModelScope.launch {
            deleteBookUseCase(book)
            updateState {
                it.copy(
                    selectedBook = null,
                    manualBookForm = ManualBookFormState(),
                    shouldOpenBottomSheet = false,
                    bottomSheetType = BottomSheetType.NONE
                )
            }
        }
    }

    private suspend fun persistLocalCover(coverUrl: String?): String? = withContext(Dispatchers.IO) {
        if (coverUrl.isNullOrBlank()) return@withContext coverUrl
        val uri = Uri.parse(coverUrl)
        if (uri.scheme != "content") return@withContext coverUrl
        runCatching {
            val coverDirectory = File(context.filesDir, "book-covers").apply { mkdirs() }
            val destination = File(coverDirectory, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            } ?: return@runCatching coverUrl
            Uri.fromFile(destination).toString()
        }.getOrDefault(coverUrl)
    }

    private fun addBookToLibrary(book: Book) {
        if (!book.isbn.isNullOrBlank() && viewState.books.any { it.isbn == book.isbn }) {
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
