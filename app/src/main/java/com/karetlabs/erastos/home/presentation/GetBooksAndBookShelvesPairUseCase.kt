package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBooksAndBookShelvesPairUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    operator fun invoke(): Flow<Pair<List<Book>, List<Bookshelf>>> {
        val booksFlow = libraryRepository.getAllBooks()
        val shelvesFlow = libraryRepository.getAllBookshelves()
        return booksFlow.combine(shelvesFlow) { books, shelves ->
            Pair(books, shelves)
        }
    }
}