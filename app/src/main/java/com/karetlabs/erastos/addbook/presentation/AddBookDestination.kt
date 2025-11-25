package com.karetlabs.erastos.addbook.presentation

import com.karetlabs.erastos.library.presentation.PresentationDestination
import kotlinx.serialization.Serializable

@Serializable
data class AddBookDestination(val bookshelfId: Int): PresentationDestination