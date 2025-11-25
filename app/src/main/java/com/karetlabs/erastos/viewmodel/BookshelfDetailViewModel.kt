package com.karetlabs.erastos.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfDetailViewModel @Inject constructor(
    private val repository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookshelfId: Int = checkNotNull(savedStateHandle["bookshelfId"])

    private val _bookshelf = MutableStateFlow<Bookshelf?>(null)
    val bookshelf: StateFlow<Bookshelf?> = _bookshelf.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getBookshelfById(bookshelfId).collect {
                _bookshelf.value = it
            }
        }
        viewModelScope.launch {
            repository.getBooksByBookshelf(bookshelfId).collect {
                _books.value = it
            }
        }
    }

    fun addBook(title: String, author: String) {
        viewModelScope.launch {
            repository.insertBook(Book(title = title, author = author, bookshelfId = bookshelfId))
        }
    }
}
