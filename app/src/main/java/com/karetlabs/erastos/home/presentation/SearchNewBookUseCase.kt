package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.repository.LibraryRepository
import javax.inject.Inject

class SearchNewBookUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(query: String) = libraryRepository.searchBooks(query)
}