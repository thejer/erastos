package com.karetlabs.erastos.addbook.presentation

import androidx.lifecycle.ViewModel
import com.karetlabs.erastos.network.data.Volume
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class AddBookViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Volume>>(emptyList())
    val searchResults: StateFlow<List<Volume>> = _searchResults

}
