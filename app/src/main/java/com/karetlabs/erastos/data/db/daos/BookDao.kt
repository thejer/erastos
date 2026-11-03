package com.karetlabs.erastos.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.karetlabs.erastos.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE bookshelfId = :bookshelfId ORDER BY title ASC")
    fun getBooksByBookshelf(bookshelfId: Int): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Int): Flow<Book>
}
