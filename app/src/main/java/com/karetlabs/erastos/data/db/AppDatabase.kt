package com.karetlabs.erastos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.karetlabs.erastos.data.db.daos.BookDao
import com.karetlabs.erastos.data.db.daos.BookshelfDao
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.data.model.Bookshelf

@Database(entities = [Book::class, Bookshelf::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookshelfDao(): BookshelfDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "erastos_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
