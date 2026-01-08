package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.network.data.Volume
import com.karetlabs.erastos.network.data.VolumesResponse

fun Volume.toBook(addMode: AddMode): Book {
    val info = volumeInfo

    return Book(
        title = info.title,
        authors = info.authors.orEmpty(),
        coverUrl = (info.imageLinks?.thumbnail
            ?: info.imageLinks?.smallThumbnail
            ?: "").replace("http://", "https://"),
        description = info.description.orEmpty(),
        pageCount = info.pageCount ?: 0,
        publishedDate = info.publishedDate.orEmpty(),
        publisher = info.publisher.orEmpty(),
        isbn = info.industryIdentifiers.orEmpty().find { it.type == "ISBN_13" }?.identifier.orEmpty(),
        bookshelfId = 0,
        volumeId = id,
        addMode = addMode.mode
    )
}

fun VolumesResponse.toBooks(addMode: AddMode): List<Book> {
    return items
        .orEmpty()
        .map { it.toBook(addMode) }
}

enum class AddMode(val mode: String) {
    MANUAL("MANUAL"),
    SCAN("SCAN"),
    SEARCH("SEARCH");

    companion object {
        fun from(value: String?): AddMode {
            val normalized = value?.trim()?.uppercase().orEmpty()
            return entries.firstOrNull { it.mode == normalized } ?: MANUAL
        }
    }
}