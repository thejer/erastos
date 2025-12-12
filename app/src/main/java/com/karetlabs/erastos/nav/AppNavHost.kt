package com.karetlabs.erastos.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.karetlabs.erastos.addbook.AddBookScreen
import com.karetlabs.erastos.addbook.presentation.AddBookDestination
import com.karetlabs.erastos.bookshelf.AddBookshelfScreen
import com.karetlabs.erastos.bookshelf.BookshelfDetailScreen
import com.karetlabs.erastos.bookshelf.presentation.AddBookshelfDestination
import com.karetlabs.erastos.bookshelf.presentation.BookshelfDetailDestination
import com.karetlabs.erastos.home.LibraryHomeScreen
import com.karetlabs.erastos.home.presentation.LibraryHomeDestination

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = LibraryHomeDestination) {
        composable<LibraryHomeDestination> {
            LibraryHomeScreen()
        }
        composable<BookshelfDetailDestination> { backStackEntry ->
            val destination: BookshelfDetailDestination = backStackEntry.toRoute()
            val bookshelfId = destination.bookshelfId
            BookshelfDetailScreen(navController = navController, bookshelfId = bookshelfId)
        }
        composable<AddBookDestination> { backStackEntry ->
            val bookshelf: AddBookDestination = backStackEntry.toRoute()
            val bookshelfId = bookshelf.bookshelfId
            AddBookScreen(navController = navController, bookshelfId = bookshelfId)
        }
        composable<AddBookshelfDestination> {
            AddBookshelfScreen(navController = navController)
        }
    }
}