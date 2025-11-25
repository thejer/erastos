package com.karetlabs.erastos.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.karetlabs.erastos.ui.screens.AddBookScreen
import com.karetlabs.erastos.ui.screens.AddBookshelfScreen
import com.karetlabs.erastos.ui.screens.BookshelfDetailScreen
import com.karetlabs.erastos.ui.screens.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.BookshelfDetail.route + "/{bookshelfId}") { backStackEntry ->
            val bookshelfId = backStackEntry.arguments?.getString("bookshelfId")?.toIntOrNull()
            if (bookshelfId != null) {
                BookshelfDetailScreen(navController = navController, bookshelfId = bookshelfId)
            } else {
                Text(text = "Error: Bookshelf ID is missing or invalid.")
            }
        }
        composable(Screen.AddBook.route + "/{bookshelfId}") { backStackEntry ->
            val bookshelfId = backStackEntry.arguments?.getString("bookshelfId")?.toIntOrNull()
            if (bookshelfId != null) {
                AddBookScreen(navController = navController, bookshelfId = bookshelfId)
            } else {
                Text(text = "Error: Bookshelf ID is missing for Add Book screen.")
            }
        }
        composable(Screen.AddBookshelf.route) {
            AddBookshelfScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookshelfDetail : Screen("bookshelf_detail")
    object AddBook : Screen("add_book")
    object AddBookshelf : Screen("add_bookshelf")
}
