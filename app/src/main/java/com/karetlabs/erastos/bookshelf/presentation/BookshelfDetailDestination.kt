package com.karetlabs.erastos.bookshelf.presentation

import com.karetlabs.erastos.library.presentation.PresentationDestination
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfDetailDestination(val bookshelfId: Int) : PresentationDestination