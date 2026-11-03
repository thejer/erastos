package com.karetlabs.erastos.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.karetlabs.erastos.auth.AuthScreen
import com.karetlabs.erastos.auth.LibraryOnboardingScreen
import com.karetlabs.erastos.auth.presentation.AuthEvent
import com.karetlabs.erastos.auth.presentation.AuthViewModel
import com.karetlabs.erastos.home.LibraryHomeScreen
import com.karetlabs.erastos.library.presentation.mvi.observableViewState

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.observableViewState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val authDestination = when {
        authState.requiresPasswordUpdate -> PASSWORD_UPDATE_ROUTE
        !authState.isAuthenticated -> AUTH_ROUTE
        authState.requiresOnboarding -> ONBOARDING_ROUTE
        else -> LIBRARY_ROUTE
    }

    LaunchedEffect(authDestination, backStackEntry?.destination?.id) {
        val currentId = backStackEntry?.destination?.id
        if (backStackEntry?.destination?.route != authDestination) {
            navController.navigate(authDestination) {
                if (currentId != null) popUpTo(currentId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = AUTH_ROUTE) {
        composable(AUTH_ROUTE) {
            AuthScreen(state = authState, onEvent = authViewModel::onEvent)
        }
        composable(PASSWORD_UPDATE_ROUTE) {
            AuthScreen(state = authState, onEvent = authViewModel::onEvent)
        }
        composable(ONBOARDING_ROUTE) {
            LibraryOnboardingScreen(
                onContinue = { authViewModel.onEvent(AuthEvent.CompleteOnboarding) },
                onSignOut = { authViewModel.onEvent(AuthEvent.SignOut) }
            )
        }
        composable(LIBRARY_ROUTE) {
            LibraryHomeScreen(onSignOut = { authViewModel.onEvent(AuthEvent.SignOut) })
        }
    }
}

private const val AUTH_ROUTE = "auth-login"
private const val PASSWORD_UPDATE_ROUTE = "auth-password-update"
private const val ONBOARDING_ROUTE = "library-onboarding"
private const val LIBRARY_ROUTE = "library-home"
