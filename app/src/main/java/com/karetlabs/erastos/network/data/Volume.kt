package com.karetlabs.erastos.network.data

data class VolumesResponse(
    val items: List<Volume>?
)

data class Volume(
    val id: String,
    val volumeInfo: VolumeInfo,
    val searchInfo: SearchInfo?
)

data class VolumeInfo(
    val title: String,
    val subtitle: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val pageCount: Int?,
    val description: String?,
    val industryIdentifiers: List<IndustryIdentifier>?,
    val categories: List<String>?,
    val imageLinks: ImageLinks?,
    val language: String
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

data class SearchInfo(
    val textSnippet: String?
)