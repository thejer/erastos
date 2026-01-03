package com.karetlabs.erastos.home.presentation

import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.network.data.Volume
import com.karetlabs.erastos.network.data.VolumesResponse

fun Volume.toBook(): Book {
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
        bookshelfId = 0
    )
}

fun VolumesResponse.toBooks(): List<Book> {
    return items
        .orEmpty()
        .map { it.toBook() }
}