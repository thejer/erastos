package com.karetlabs.erastos.data.source.remote

import com.karetlabs.erastos.BuildConfig
import com.karetlabs.erastos.network.ApiService
import com.karetlabs.erastos.network.OpenLibraryApiService
import com.karetlabs.erastos.network.data.ImageLinks
import com.karetlabs.erastos.network.data.IndustryIdentifier
import com.karetlabs.erastos.network.data.Volume
import com.karetlabs.erastos.network.data.VolumeInfo
import com.karetlabs.erastos.network.data.VolumesResponse
import com.karetlabs.erastos.network.util.RemoteDataSourceException
import com.karetlabs.erastos.network.util.RemoteErrorKind
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BookRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val openLibraryApiService: OpenLibraryApiService
) {
    @Throws(RemoteDataSourceException::class)
    suspend fun searchBooks(query: String): VolumesResponse {
        val isbn = query.substringAfter("isbn:", missingDelimiterValue = "")
            .trim()
            .takeIf { query.startsWith("isbn:", ignoreCase = true) && it.isNotBlank() }

        var googleException: Exception? = null
        try {
            val googleResult = apiService.searchBooks(query, BuildConfig.GOOGLE_BOOKS_API_KEY)
            if (!googleResult.items.isNullOrEmpty() || isbn == null) return googleResult
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            googleException = e
        }

        if (isbn == null) throw googleException?.toRemoteException()
            ?: RemoteDataSourceException("No books found.", RemoteErrorKind.Unknown)

        try {
            val key = "ISBN:$isbn"
            val openLibraryBook = openLibraryApiService.getBookByIsbn(key)[key]
            val title = openLibraryBook?.title?.takeIf(String::isNotBlank)
            if (openLibraryBook == null || title == null) {
                if (googleException != null) throw googleException.toRemoteException()
                return VolumesResponse(items = emptyList())
            }

            val isbnIdentifier = IndustryIdentifier("ISBN_13", isbn)
            val coverUrl = openLibraryBook.cover?.medium
                ?: openLibraryBook.cover?.large
                ?: openLibraryBook.cover?.small
                ?: "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg?default=false"
            return VolumesResponse(
                items = listOf(
                    Volume(
                        id = "openlibrary:$isbn",
                        volumeInfo = VolumeInfo(
                            title = title,
                            subtitle = null,
                            authors = openLibraryBook.authors.orEmpty().mapNotNull { it.name },
                            publisher = openLibraryBook.publishers?.firstNotNullOfOrNull { it.name },
                            publishedDate = openLibraryBook.publishDate,
                            pageCount = openLibraryBook.numberOfPages,
                            description = null,
                            industryIdentifiers = listOf(isbnIdentifier),
                            categories = openLibraryBook.subjects.orEmpty().mapNotNull { it.name },
                            imageLinks = coverUrl?.let { ImageLinks(it, it) },
                            language = ""
                        ),
                        searchInfo = null
                    )
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: RemoteDataSourceException) {
            throw e
        } catch (e: Exception) {
            throw e.toRemoteException()
        }
    }

    private fun Exception.toRemoteException() = when (this) {
        is HttpException -> RemoteDataSourceException(
            message = "Request failed (${code()}).",
            kind = RemoteErrorKind.Http,
            httpCode = code(),
            cause = this
        )
        is IOException -> RemoteDataSourceException(
            message = "Network error. Please check your connection and try again.",
            kind = RemoteErrorKind.Network,
            cause = this
        )
        else -> RemoteDataSourceException(
            message = "Unexpected error while searching books.",
            kind = RemoteErrorKind.Unknown,
            cause = this
        )
    }
}

