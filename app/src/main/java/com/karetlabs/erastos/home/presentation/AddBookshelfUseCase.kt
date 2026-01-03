package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.repository.LibraryRepository
import javax.inject.Inject

class AddBookshelfUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(bookshelf: Bookshelf): Long = libraryRepository.insertBookshelf(bookshelf)
}