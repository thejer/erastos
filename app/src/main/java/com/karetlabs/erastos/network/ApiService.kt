package com.karetlabs.erastos.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): VolumesResponse
}
