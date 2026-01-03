package com.karetlabs.erastos.library

import com.karetlabs.erastos.data.model.Book

fun Book.getAuthorsString(): String = authors.joinToString(", ")