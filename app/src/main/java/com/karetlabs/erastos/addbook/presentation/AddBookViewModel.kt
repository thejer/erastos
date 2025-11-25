package com.karetlabs.erastos.addbook.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.network.Volume
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchType {
    TITLE, ISBN
}

@HiltViewModel
class AddBookViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Volume>>(emptyList())
    val searchResults: StateFlow<List<Volume>> = _searchResults

    fun addBook(title: String, author: String, bookshelfId: Int) {
        viewModelScope.launch {
            repository.insertBook(Book(title = title, author = author, bookshelfId = bookshelfId))
        }
    }

    fun searchBooks(type: SearchType, query: String) {
        val formattedQuery = when (type) {
            SearchType.TITLE -> query
            SearchType.ISBN -> "isbn:$query"
        }
        viewModelScope.launch {
            try {
                val result = repository.searchBooks(formattedQuery)
                _searchResults.value = result.items
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
