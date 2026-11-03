package com.karetlabs.erastos.repository

import com.karetlabs.erastos.data.db.daos.BookDao
import com.karetlabs.erastos.data.db.daos.BookshelfDao
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf
import com.karetlabs.erastos.data.source.remote.BookRemoteDataSource
import com.karetlabs.erastos.data.source.remote.CloudBook
import com.karetlabs.erastos.data.source.remote.CloudBookshelf
import com.karetlabs.erastos.data.source.remote.SupabaseLibraryRemoteDataSource
import com.karetlabs.erastos.network.data.VolumesResponse
import com.karetlabs.erastos.network.util.DataResult
import com.karetlabs.erastos.network.util.RemoteDataSourceException
import com.karetlabs.erastos.network.util.RemoteErrorKind
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val bookDao: BookDao,
    private val bookshelfDao: BookshelfDao,
    private val remoteDataSource: BookRemoteDataSource,
    private val supabaseRemoteDataSource: SupabaseLibraryRemoteDataSource
) {

    val authSessionStatus get() = supabaseRemoteDataSource.authSessionStatus

    fun currentUserId(): String? = supabaseRemoteDataSource.currentUserId()

    suspend fun signUp(email: String, password: String) = supabaseRemoteDataSource.signUp(email, password)

    suspend fun signIn(email: String, password: String) = supabaseRemoteDataSource.signIn(email, password)

    suspend fun requestPasswordReset(email: String) = supabaseRemoteDataSource.requestPasswordReset(email)

    suspend fun updatePassword(password: String) = supabaseRemoteDataSource.updatePassword(password)

    suspend fun signOut() = supabaseRemoteDataSource.signOut()

    suspend fun getCloudBookshelves(): List<CloudBookshelf> = supabaseRemoteDataSource.getBookshelves()

    suspend fun createCloudBookshelf(name: String): CloudBookshelf = supabaseRemoteDataSource.createBookshelf(name)

    suspend fun updateCloudBookshelf(bookshelf: CloudBookshelf) = supabaseRemoteDataSource.updateBookshelf(bookshelf)

    suspend fun deleteCloudBookshelf(id: String) = supabaseRemoteDataSource.deleteBookshelf(id)

    suspend fun getCloudBooks(): List<CloudBook> = supabaseRemoteDataSource.getBooks()

    suspend fun createCloudBook(book: Book, bookshelfId: String): CloudBook =
        supabaseRemoteDataSource.createBook(book, bookshelfId)

    suspend fun updateCloudBook(book: CloudBook) = supabaseRemoteDataSource.updateBook(book)

    suspend fun deleteCloudBook(id: String) = supabaseRemoteDataSource.deleteBook(id)

    fun getAllBookshelves(): Flow<List<Bookshelf>> = bookshelfDao.getAllBookshelves()

    suspend fun insertBookshelf(bookshelf: Bookshelf): Long = bookshelfDao.insert(bookshelf)

    fun getBookshelfById(id: Int): Flow<Bookshelf> = bookshelfDao.getBookshelfById(id)

    fun getBooksByBookshelf(bookshelfId: Int): Flow<List<Book>> = bookDao.getBooksByBookshelf(bookshelfId)

    suspend fun insertBook(book: Book) {
        bookDao.insert(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.update(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.delete(book)
    }

    fun getBookById(id: Int): Flow<Book> = bookDao.getBookById(id)

    suspend fun searchBooks(query: String): DataResult<VolumesResponse> {
        return try {
            DataResult.Success(remoteDataSource.searchBooks(query))
        } catch (e: RemoteDataSourceException) {
            DataResult.Failure(
                title = when (e.kind) {
                    RemoteErrorKind.Network -> "Network error"
                    RemoteErrorKind.Http -> "Request failed"
                    RemoteErrorKind.Unknown -> "Unexpected error"
                },
                message = e.message,
                code = e.httpCode
            )
        }
    }

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
}
