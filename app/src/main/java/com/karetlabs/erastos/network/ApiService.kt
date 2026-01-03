package com.karetlabs.erastos.network

import com.karetlabs.erastos.network.data.VolumesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): VolumesResponse
}
