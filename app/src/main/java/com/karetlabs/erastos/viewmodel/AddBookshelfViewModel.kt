package com.karetlabs.erastos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookshelfViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    fun addBookshelf(name: String) {
        viewModelScope.launch {
            repository.insertBookshelf(Bookshelf(name = name))
        }
    }
}
