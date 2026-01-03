package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.repository.LibraryRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(book: Book) {
        libraryRepository.insertBook(book)
    }
}
