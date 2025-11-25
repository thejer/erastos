package com.karetlabs.erastos.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    private val _bookshelves = MutableStateFlow<List<Bookshelf>>(emptyList())
    val bookshelves: StateFlow<List<Bookshelf>> = _bookshelves.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllBookshelves().collect {
                _bookshelves.value = it
            }
        }
    }

    fun addBookshelf(name: String) {
        viewModelScope.launch {
            repository.insertBookshelf(Bookshelf(name = name))
        }
    }
}