package com.karetlabs.erastos.data.source.remote

import com.karetlabs.erastos.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientProvider @Inject constructor() {
    private val client: SupabaseClient by lazy {
        val url = BuildConfig.SUPABASE_URL
        val publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        check(url.isNotBlank() && publishableKey.isNotBlank()) {
            "Configure SUPABASE_URL and SUPABASE_PUBLISHABLE_KEY in local.properties before using cloud features."
        }

        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = publishableKey
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    fun get(): SupabaseClient = client
}
