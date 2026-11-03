package com.karetlabs.erastos.auth.presentation

import com.karetlabs.erastos.library.presentation.mvi.BaseEvent
import com.karetlabs.erastos.library.presentation.mvi.BaseViewState

enum class AuthMode { LOGIN, SIGN_UP, FORGOT_PASSWORD, UPDATE_PASSWORD }

sealed class AuthEvent : BaseEvent {
    data class EmailChanged(val value: String) : AuthEvent()
    data class PasswordChanged(val value: String) : AuthEvent()
    data class ConfirmPasswordChanged(val value: String) : AuthEvent()
    data object OpenSignUp : AuthEvent()
    data object OpenLogin : AuthEvent()
    data object OpenForgotPassword : AuthEvent()
    data object Submit : AuthEvent()
    data object CompleteOnboarding : AuthEvent()
    data object SignOut : AuthEvent()
}

data class AuthViewState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isCheckingSession: Boolean = true,
    val isAuthenticated: Boolean = false,
    val requiresOnboarding: Boolean = false,
    val requiresPasswordUpdate: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null
) : BaseViewState
