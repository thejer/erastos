package com.karetlabs.erastos.repository

import com.karetlabs.erastos.data.db.BookDao
import com.karetlabs.erastos.data.db.BookshelfDao
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.data.source.remote.BookRemoteDataSource
import com.karetlabs.erastos.network.VolumesResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val bookDao: BookDao,
    private val bookshelfDao: BookshelfDao,
    private val remoteDataSource: BookRemoteDataSource
) {

    fun getAllBookshelves(): Flow<List<Bookshelf>> = bookshelfDao.getAllBookshelves()

    suspend fun insertBookshelf(bookshelf: Bookshelf) {
        bookshelfDao.insert(bookshelf)
    }

    fun getBookshelfById(id: Int): Flow<Bookshelf> = bookshelfDao.getBookshelfById(id)

    fun getBooksByBookshelf(bookshelfId: Int): Flow<List<Book>> = bookDao.getBooksByBookshelf(bookshelfId)

    suspend fun insertBook(book: Book) {
        bookDao.insert(book)
    }

    fun getBookById(id: Int): Flow<Book> = bookDao.getBookById(id)

    suspend fun searchBooks(query: String): VolumesResponse {
        return remoteDataSource.searchBooks(query)
    }
}
