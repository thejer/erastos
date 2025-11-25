package com.karetlabs.erastos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookshelves")
data class Bookshelf(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
