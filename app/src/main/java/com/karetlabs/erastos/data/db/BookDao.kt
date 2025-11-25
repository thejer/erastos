package com.karetlabs.erastos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.karetlabs.erastos.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE bookshelfId = :bookshelfId ORDER BY title ASC")
    fun getBooksByBookshelf(bookshelfId: Int): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Int): Flow<Book>
}
