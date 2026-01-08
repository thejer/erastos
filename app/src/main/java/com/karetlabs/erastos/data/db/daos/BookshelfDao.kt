package com.karetlabs.erastos.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.karetlabs.erastos.data.model.Bookshelf
import kotlinx.coroutines.flow.Flow

@Dao
interface BookshelfDao {
    @Query("SELECT * FROM bookshelves ORDER BY name ASC")
    fun getAllBookshelves(): Flow<List<Bookshelf>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(bookshelf: Bookshelf): Long

    @Query("SELECT * FROM bookshelves WHERE id = :id")
    fun getBookshelfById(id: Int): Flow<Bookshelf>
}