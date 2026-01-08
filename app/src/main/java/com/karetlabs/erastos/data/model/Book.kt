package com.karetlabs.erastos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val authors: List<String>,
    val coverUrl: String,
    val description: String,
    val pageCount: Int,
    val publishedDate: String,
    val publisher: String,
    val isbn: String,
    /**
     * How this book was added to the library.
     * Expected values: "MANUAL", "SCAN", "SEARCH".
     */
    val addMode: String,
    val volumeId: String,
    val bookshelfId: Int // Foreign key to Bookshelf
)
