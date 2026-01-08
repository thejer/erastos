package com.karetlabs.erastos.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    }
}