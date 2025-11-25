package com.karetlabs.erastos.network

import com.google.gson.annotations.SerializedName

data class Volume(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    @SerializedName("smallThumbnail")
    val smallThumbnail: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?
)

data class VolumesResponse(
    val items: List<Volume>
)
