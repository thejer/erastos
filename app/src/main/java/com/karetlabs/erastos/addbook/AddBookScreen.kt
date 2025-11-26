package com.karetlabs.erastos.addbook


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.karetlabs.erastos.R
import com.karetlabs.erastos.addbook.presentation.AddBookViewModel
import com.karetlabs.erastos.addbook.presentation.SearchType
import com.karetlabs.erastos.ui.components.BarcodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    navController: NavController,
    bookshelfId: Int,
    addBookViewModel: AddBookViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSearchType by remember { mutableStateOf(SearchType.TITLE) } // New state for search type
    var showScanner by remember { mutableStateOf(false) } // State to control scanner visibility

    val searchResults by addBookViewModel.searchResults.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Book") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showScanner) {
            BarcodeScanner(
                onBarcodeScanned = { isbn ->
                    searchQuery = isbn
                    selectedSearchType = SearchType.ISBN
                    addBookViewModel.searchBooks(SearchType.ISBN, isbn)
                    showScanner = false
                },
                onError = {
                    showScanner = false
                    // Optionally show a toast message or other error handling
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Search type selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { selectedSearchType = SearchType.TITLE },
                        enabled = selectedSearchType != SearchType.TITLE
                    ) {
                        Text("Search by Title")
                    }
                    Button(
                        onClick = { selectedSearchType = SearchType.ISBN },
                        enabled = selectedSearchType != SearchType.ISBN
                    ) {
                        Text("Search by ISBN")
                    }
                    Button(
                        onClick = { showScanner = true }
                    ) {
                        Text("Scan ISBN")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (searchQuery.isNotBlank()) {
                            addBookViewModel.searchBooks(selectedSearchType, searchQuery)
                        }
                    },
                    label = { Text("Search by ${selectedSearchType.name}") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (searchResults.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(searchResults) { volume ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        title = volume.volumeInfo.title
                                        author = volume.volumeInfo.authors?.joinToString(", ") ?: ""
                                        searchQuery = "" // Clear search after selection
                                    },
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = volume.volumeInfo.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    volume.volumeInfo.authors?.joinToString(", ")?.let {
                                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank() && author.isNotBlank()) {
                            addBookViewModel.addBook(title, author, bookshelfId)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Book")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddBookScreenPreview() {
    AddBookScreen(navController = rememberNavController(), bookshelfId = 1)
}