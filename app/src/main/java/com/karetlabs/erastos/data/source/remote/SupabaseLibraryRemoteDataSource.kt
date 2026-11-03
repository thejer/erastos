package com.karetlabs.erastos.data.source.remote

import com.karetlabs.erastos.data.model.Book
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class CloudBookshelf(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CloudBook(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("bookshelf_id") val bookshelfId: String,
    val title: String,
    val authors: List<String>,
    @SerialName("cover_url") val coverUrl: String? = null,
    val description: String? = null,
    @SerialName("page_count") val pageCount: Int? = null,
    @SerialName("published_date") val publishedDate: String? = null,
    val publisher: String? = null,
    val isbn: String? = null,
    @SerialName("add_mode") val addMode: String,
    @SerialName("volume_id") val volumeId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Singleton
class SupabaseLibraryRemoteDataSource @Inject constructor(
    private val clientProvider: SupabaseClientProvider
) {
    private val client get() = clientProvider.get()

    val authSessionStatus get() = client.auth.sessionStatus

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    suspend fun getBookshelves(): List<CloudBookshelf> {
        val ownerId = requireCurrentUserId()
        return client.from("bookshelves").select {
            filter { eq("owner_id", ownerId) }
        }.decodeList<CloudBookshelf>()
    }

    suspend fun createBookshelf(name: String): CloudBookshelf {
        val bookshelf = CloudBookshelf(
            id = UUID.randomUUID().toString(),
            ownerId = requireCurrentUserId(),
            name = name
        )
        client.from("bookshelves").insert(bookshelf)
        return bookshelf
    }

    suspend fun updateBookshelf(bookshelf: CloudBookshelf) {
        val ownerId = requireCurrentUserId()
        client.from("bookshelves").update(bookshelf) {
            filter {
                eq("id", bookshelf.id)
                eq("owner_id", ownerId)
            }
        }
    }

    suspend fun deleteBookshelf(id: String) {
        val ownerId = requireCurrentUserId()
        client.from("bookshelves").delete {
            filter {
                eq("id", id)
                eq("owner_id", ownerId)
            }
        }
    }

    suspend fun getBooks(): List<CloudBook> {
        val ownerId = requireCurrentUserId()
        return client.from("books").select {
            filter { eq("owner_id", ownerId) }
        }.decodeList<CloudBook>()
    }

    suspend fun createBook(book: Book, bookshelfId: String): CloudBook {
        val cloudBook = book.toCloudBook(
            id = UUID.randomUUID().toString(),
            ownerId = requireCurrentUserId(),
            bookshelfId = bookshelfId
        )
        client.from("books").insert(cloudBook)
        return cloudBook
    }

    suspend fun updateBook(book: CloudBook) {
        val ownerId = requireCurrentUserId()
        client.from("books").update(book) {
            filter {
                eq("id", book.id)
                eq("owner_id", ownerId)
            }
        }
    }

    suspend fun deleteBook(id: String) {
        val ownerId = requireCurrentUserId()
        client.from("books").delete {
            filter {
                eq("id", id)
                eq("owner_id", ownerId)
            }
        }
    }

    private fun requireCurrentUserId(): String = currentUserId()
        ?: throw IllegalStateException("Sign in before accessing your cloud library.")

    private fun Book.toCloudBook(id: String, ownerId: String, bookshelfId: String) = CloudBook(
        id = id,
        ownerId = ownerId,
        bookshelfId = bookshelfId,
        title = title,
        authors = authors,
        coverUrl = coverUrl,
        description = description,
        pageCount = pageCount,
        publishedDate = publishedDate,
        publisher = publisher,
        isbn = isbn,
        addMode = addMode,
        volumeId = volumeId
    )
}
