package com.karetlabs.erastos.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApiService {
    @GET("api/books")
    suspend fun getBookByIsbn(
        @Query("bibkeys") bibkeys: String,
        @Query("format") format: String = "json",
        @Query("jscmd") command: String = "data"
    ): Map<String, OpenLibraryBook>
}

data class OpenLibraryBook(
    val title: String? = null,
    val authors: List<OpenLibraryNamedValue>? = null,
    val publishers: List<OpenLibraryNamedValue>? = null,
    @SerializedName("publish_date") val publishDate: String? = null,
    @SerializedName("number_of_pages") val numberOfPages: Int? = null,
    val cover: OpenLibraryCover? = null,
    val subjects: List<OpenLibraryNamedValue>? = null
)

data class OpenLibraryNamedValue(val name: String? = null)

data class OpenLibraryCover(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null
)
