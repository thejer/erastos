package com.karetlabs.erastos.data.source.remote

import com.karetlabs.erastos.network.ApiService
import javax.inject.Inject

class BookRemoteDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun searchBooks(query: String) = apiService.searchBooks(query)
}
