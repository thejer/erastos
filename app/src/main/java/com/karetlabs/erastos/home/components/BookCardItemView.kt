package com.karetlabs.erastos.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.karetlabs.erastos.R
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.library.getAuthorsString
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.dimension

@Composable
fun BookCardItemView(
    book: Book,
    onClick: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(dimension.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = ComponentDimensions.cardElevation),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                placeholder = painterResource(R.drawable.img_book_cover),
                error = painterResource(R.drawable.img_book_cover),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.63f)
                    .clip(RoundedCornerShape(topStart = dimension.radiusLarge, topEnd = dimension.radiusLarge))
            )

            Column(
                modifier = Modifier.padding(dimension.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimension.componentSpacingSmall)
            ) {
                Text(
                    text = book.title,
                    style = CustomTypography.bookTitle,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.getAuthorsString(),
                    style = CustomTypography.bookAuthor,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBookCardItem() {
    val book = Book(
        title = "The Great Gatsby",
        authors = listOf("F. Scott Fitzgerald"),
        coverUrl = "https://picsum.photos/seed/picsum/200/300",
        bookshelfId = 1,
        id = 1,
        description = "The Great Gatsby is a novel written by American author F. Scott Fitzgerald that tells the story of the fabulously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.",
        pageCount = 180,
        publishedDate = "1925-04-10",
        publisher = "Scribner",
        isbn = "9780141439518",
        volumeId = "",
        addMode = "SCAN"
    )
    BookCardItemView(book)
}
