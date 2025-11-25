package com.karetlabs.erastos.viewmodel

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

@HiltViewModel
class AddBookViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Volume>>(emptyList())
    val searchResults: StateFlow<List<Volume>> = _searchResults

    fun addBook(title: String, author: String, bookshelfId: Int) {
        viewModelScope.launch {
            repository.insertBook(Book(title = title, author = author, bookshelfId = bookshelfId))
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchBooks(query)
                _searchResults.value = result.items
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
