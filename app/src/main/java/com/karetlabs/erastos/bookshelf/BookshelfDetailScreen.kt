package com.karetlabs.erastos.bookshelf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.karetlabs.erastos.addbook.presentation.AddBookDestination
import com.karetlabs.erastos.bookshelf.presentation.BookshelfDetailViewModel
import com.karetlabs.erastos.data.model.Book
import com.karetlabs.erastos.library.getAuthorsString
import com.karetlabs.erastos.library.presentation.doOnScreenCreate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfDetailScreen(
    navController: NavController,
    bookshelfId: Int,
    bookshelfDetailViewModel: BookshelfDetailViewModel = hiltViewModel()
) {
    val bookshelf by bookshelfDetailViewModel.bookshelf.collectAsState()
    val books by bookshelfDetailViewModel.books.collectAsState()

    doOnScreenCreate {
        bookshelfDetailViewModel.initialize(bookshelfId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bookshelf?.name ?: "Bookshelf Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AddBookDestination(bookshelfId)) }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (books.isEmpty()) {
                Text("No books in this bookshelf yet. Click '+' to add one!")
            } else {
                LazyColumn {
                    items(books) { book ->
                        BookCard(book = book)
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "by ${book.getAuthorsString()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookshelfDetailScreenPreview() {
    BookshelfDetailScreen(navController = rememberNavController(), bookshelfId = 1)
}
