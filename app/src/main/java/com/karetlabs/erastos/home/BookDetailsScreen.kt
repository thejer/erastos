package com.karetlabs.erastos.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.karetlabs.erastos.R
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.library.getAuthorsString
import com.karetlabs.erastos.library.ui.components.ErastosButton
import com.karetlabs.erastos.library.ui.components.VerticalSpacer
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun BookDetailsScreen(
    book: Book,
    bookshelfName: String?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimension.screenPaddingHorizontal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimension.paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.width(dimension.paddingSmall))
            Text(text = book.title, style = typography.titleLarge, maxLines = 1)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = stringResource(R.string.search_result_item_book_cover),
                placeholder = painterResource(R.drawable.img_book_cover),
                error = painterResource(R.drawable.img_book_cover),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(190.dp)
                    .aspectRatio(0.63f)
                    .clip(RoundedCornerShape(dimension.radiusLarge))
            )
            VerticalSpacer(dimension.componentSpacingLarge)
        }

        Text(
            text = book.title,
            style = typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (book.authors.isNotEmpty()) {
            VerticalSpacer(dimension.componentSpacingSmall)
            Text(
                text = book.getAuthorsString(),
                style = CustomTypography.bookAuthor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        VerticalSpacer(dimension.componentSpacingLarge)
        bookshelfName?.takeIf { it.isNotBlank() && it != "All" }?.let {
            DetailRow("Bookshelf", it)
        }
        book.isbn?.takeIf(String::isNotBlank)?.let { DetailRow("ISBN", it) }
        book.publisher?.takeIf(String::isNotBlank)?.let { DetailRow("Publisher", it) }
        book.publishedDate?.takeIf(String::isNotBlank)?.let { DetailRow("Published", it) }
        book.pageCount?.takeIf { it > 0 }?.let { DetailRow("Pages", it.toString()) }

        book.description?.takeIf(String::isNotBlank)?.let { description ->
            VerticalSpacer(dimension.componentSpacingLarge)
            Text(
                text = "Description",
                style = typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )
            VerticalSpacer(dimension.componentSpacingSmall)
            Text(
                text = description,
                style = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ErastosButton(
            onClick = onEdit,
            modifier = Modifier.padding(vertical = dimension.screenPaddingVertical),
            text = stringResource(R.string.edit_book)
        )
        TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
            Text("Delete book", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(dimension.screenPaddingVertical))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, style = typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        VerticalSpacer(dimension.componentSpacingMedium)
    }
}
