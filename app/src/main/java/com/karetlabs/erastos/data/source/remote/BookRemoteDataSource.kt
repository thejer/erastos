package com.karetlabs.erastos.data.source.remote

import com.karetlabs.erastos.network.ApiService
import com.karetlabs.erastos.network.data.VolumesResponse
import com.karetlabs.erastos.network.util.RemoteDataSourceException
import com.karetlabs.erastos.network.util.RemoteErrorKind
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BookRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    @Throws(RemoteDataSourceException::class)
    suspend fun searchBooks(query: String): VolumesResponse {
        return try {
            apiService.searchBooks(query)
        } catch (e: CancellationException) {
            // Never swallow coroutine cancellation.
            throw e
        } catch (e: HttpException) {
            throw RemoteDataSourceException(
                message = "Request failed (${e.code()}).",
                kind = RemoteErrorKind.Http,
                httpCode = e.code(),
                cause = e
            )
        } catch (e: IOException) {
            // Timeouts, no internet, DNS issues, etc.
            throw RemoteDataSourceException(
                message = "Network error. Please check your connection and try again.",
                kind = RemoteErrorKind.Network,
                cause = e
            )
        } catch (e: Exception) {
            throw RemoteDataSourceException(
                message = "Unexpected error while searching books.",
                kind = RemoteErrorKind.Unknown,
                cause = e
            )
        }
    }
}

