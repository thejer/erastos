package com.karetlabs.erastos.auth.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.auth.AuthDeepLink
import com.karetlabs.erastos.library.presentation.mvi.HasEvents
import com.karetlabs.erastos.library.presentation.mvi.HasState
import com.karetlabs.erastos.library.presentation.mvi.HasStateImpl
import com.karetlabs.erastos.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val libraryRepository: LibraryRepository
) : ViewModel(), HasEvents<AuthEvent>, HasState<AuthViewState> by HasStateImpl(AuthViewState()) {

    private val preferences by lazy {
        context.getSharedPreferences(AuthDeepLink.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    init {
        observeSession()
    }

    override fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> updateState {
                it.copy(email = event.value, errorMessage = null, statusMessage = null)
            }
            is AuthEvent.PasswordChanged -> updateState {
                it.copy(password = event.value, errorMessage = null, statusMessage = null)
            }
            is AuthEvent.ConfirmPasswordChanged -> updateState {
                it.copy(confirmPassword = event.value, errorMessage = null, statusMessage = null)
            }
            AuthEvent.OpenSignUp -> setMode(AuthMode.SIGN_UP)
            AuthEvent.OpenLogin -> setMode(AuthMode.LOGIN)
            AuthEvent.OpenForgotPassword -> setMode(AuthMode.FORGOT_PASSWORD)
            AuthEvent.Submit -> submit()
            AuthEvent.CompleteOnboarding -> completeOnboarding()
            AuthEvent.SignOut -> signOut()
        }
    }

    private fun setMode(mode: AuthMode) {
        updateState {
            it.copy(
                mode = mode,
                password = "",
                confirmPassword = "",
                errorMessage = null,
                statusMessage = null
            )
        }
    }

    private fun submit() {
        when (viewState.mode) {
            AuthMode.LOGIN -> signIn()
            AuthMode.SIGN_UP -> signUp()
            AuthMode.FORGOT_PASSWORD -> sendPasswordReset()
            AuthMode.UPDATE_PASSWORD -> updatePassword()
        }
    }

    private fun signIn() {
        val email = viewState.email.trim()
        val password = viewState.password
        if (!email.contains('@') || password.isBlank()) {
            showError("Enter a valid email address and password.")
            return
        }

        viewModelScope.launch {
            beginSubmission()
            runCatching { libraryRepository.signIn(email, password) }
                .onSuccess { updateState { it.copy(isSubmitting = false) } }
                .onFailure { finishWithError(it) }
        }
    }

    private fun signUp() {
        val email = viewState.email.trim()
        val password = viewState.password
        if (!email.contains('@')) {
            showError("Enter a valid email address.")
            return
        }
        if (password.length < 6) {
            showError("Choose a password with at least 6 characters.")
            return
        }
        if (password != viewState.confirmPassword) {
            showError("The passwords do not match.")
            return
        }

        viewModelScope.launch {
            beginSubmission()
            runCatching { libraryRepository.signUp(email, password) }
                .onSuccess {
                    updateState {
                        it.copy(
                            mode = AuthMode.LOGIN,
                            password = "",
                            confirmPassword = "",
                            isSubmitting = false,
                            statusMessage = "Account created. Check your email to confirm your address, then sign in.",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { finishWithError(it) }
        }
    }

    private fun sendPasswordReset() {
        val email = viewState.email.trim()
        if (!email.contains('@')) {
            showError("Enter a valid email address.")
            return
        }

        viewModelScope.launch {
            beginSubmission()
            runCatching { libraryRepository.requestPasswordReset(email) }
                .onSuccess {
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            statusMessage = "If an account exists for this email, reset instructions have been sent.",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { finishWithError(it) }
        }
    }

    private fun updatePassword() {
        val password = viewState.password
        if (password.length < 6) {
            showError("Choose a password with at least 6 characters.")
            return
        }
        if (password != viewState.confirmPassword) {
            showError("The passwords do not match.")
            return
        }

        viewModelScope.launch {
            beginSubmission()
            try {
                libraryRepository.updatePassword(password)
                preferences.edit().remove(AuthDeepLink.PASSWORD_RECOVERY_KEY).apply()
                libraryRepository.signOut()
                updateState {
                    it.copy(
                        mode = AuthMode.LOGIN,
                        password = "",
                        confirmPassword = "",
                        requiresPasswordUpdate = false,
                        isSubmitting = false,
                        statusMessage = "Password updated. Sign in with your new password.",
                        errorMessage = null
                    )
                }
            } catch (error: Throwable) {
                finishWithError(error)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            runCatching { libraryRepository.signOut() }
                .onFailure { finishWithError(it) }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            runCatching {
                libraryRepository.authSessionStatus.collect { status ->
                    when (status) {
                        SessionStatus.Initializing -> updateState { it.copy(isCheckingSession = true) }
                        is SessionStatus.Authenticated -> {
                            val userId = status.session.user?.id
                            val isRecovery = preferences.getBoolean(AuthDeepLink.PASSWORD_RECOVERY_KEY, false)
                            val onboarded = userId?.let { preferences.getBoolean(onboardingKey(it), false) } ?: true
                            updateState {
                                it.copy(
                                    isCheckingSession = false,
                                    isAuthenticated = true,
                                    requiresOnboarding = !onboarded && !isRecovery,
                                    requiresPasswordUpdate = isRecovery,
                                    mode = if (isRecovery) AuthMode.UPDATE_PASSWORD else it.mode,
                                    errorMessage = null
                                )
                            }
                        }
                        is SessionStatus.NotAuthenticated -> updateState {
                            it.copy(
                                isCheckingSession = false,
                                isAuthenticated = false,
                                requiresOnboarding = false,
                                requiresPasswordUpdate = false
                            )
                        }
                        is SessionStatus.RefreshFailure -> updateState {
                            it.copy(
                                isCheckingSession = false,
                                isAuthenticated = false,
                                requiresOnboarding = false,
                                requiresPasswordUpdate = false,
                                errorMessage = "Your session expired. Sign in again."
                            )
                        }
                    }
                }
            }.onFailure { error ->
                updateState {
                    it.copy(
                        isCheckingSession = false,
                        isAuthenticated = false,
                        errorMessage = error.message ?: "Unable to connect to Supabase. Check the app configuration."
                    )
                }
            }
        }
    }

    private fun completeOnboarding() {
        val userId = libraryRepository.currentUserId() ?: return
        preferences.edit().putBoolean(onboardingKey(userId), true).apply()
        updateState { it.copy(requiresOnboarding = false) }
    }

    private fun beginSubmission() {
        updateState { it.copy(isSubmitting = true, errorMessage = null, statusMessage = null) }
    }

    private fun showError(message: String) {
        updateState { it.copy(errorMessage = message, statusMessage = null) }
    }

    private fun finishWithError(error: Throwable) {
        updateState {
            it.copy(
                isSubmitting = false,
                errorMessage = error.message ?: "Something went wrong. Please try again.",
                statusMessage = null
            )
        }
    }

    private fun onboardingKey(userId: String) = "${KEY_ONBOARDING_PREFIX}$userId"

    private companion object {
        const val KEY_ONBOARDING_PREFIX = "onboarding_completed_"
    }
}
