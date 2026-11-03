package com.karetlabs.erastos.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.auth.presentation.AuthEvent
import com.karetlabs.erastos.auth.presentation.AuthMode
import com.karetlabs.erastos.auth.presentation.AuthViewState
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.components.ErastosButton
import com.karetlabs.erastos.library.ui.components.ErastosLabeledPasswordField
import com.karetlabs.erastos.library.ui.components.ErastosLabeledTextField
import com.karetlabs.erastos.library.ui.components.TextFieldLength
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun AuthScreen(
    state: AuthViewState,
    onEvent: (AuthEvent) -> Unit
) {
    val fieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimension.screenPaddingHorizontal, vertical = dimension.screenPaddingVertical),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Erastos", style = typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(dimension.componentSpacingSmall))
        Text(
            text = when (state.mode) {
                AuthMode.LOGIN -> "Sign in to your library"
                AuthMode.SIGN_UP -> "Create your account"
                AuthMode.FORGOT_PASSWORD -> "Reset your password"
                AuthMode.UPDATE_PASSWORD -> "Choose a new password"
            },
            style = typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(dimension.componentSpacingSmall))
        Text(
            text = when (state.mode) {
                AuthMode.LOGIN -> "Your personal book catalog, kept with your account."
                AuthMode.SIGN_UP -> "Create an account for your private book catalog."
                AuthMode.FORGOT_PASSWORD -> "We’ll email you a link to reset your password."
                AuthMode.UPDATE_PASSWORD -> "Set a new password for your Erastos account."
            },
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(dimension.paddingLarge))

        if (state.mode != AuthMode.UPDATE_PASSWORD) {
            ErastosLabeledTextField(
                label = "Email",
                value = state.email,
                onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
                fieldLength = TextFieldLength.EMAIL,
                colors = fieldColors,
                keyboardType = KeyboardType.Email,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dimension.componentSpacingMedium))
        }

        if (state.mode == AuthMode.LOGIN || state.mode == AuthMode.SIGN_UP || state.mode == AuthMode.UPDATE_PASSWORD) {
            ErastosLabeledPasswordField(
                label = if (state.mode == AuthMode.UPDATE_PASSWORD) "New password" else "Password",
                value = state.password,
                colors = fieldColors,
                enabled = !state.isSubmitting,
                onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) }
            )
            Spacer(Modifier.height(dimension.componentSpacingMedium))
        }

        if (state.mode == AuthMode.SIGN_UP || state.mode == AuthMode.UPDATE_PASSWORD) {
            ErastosLabeledPasswordField(
                label = "Confirm password",
                value = state.confirmPassword,
                colors = fieldColors,
                enabled = !state.isSubmitting,
                onValueChange = { onEvent(AuthEvent.ConfirmPasswordChanged(it)) }
            )
            Spacer(Modifier.height(dimension.componentSpacingMedium))
        }

        state.errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = typography.bodyMedium)
            Spacer(Modifier.height(dimension.componentSpacingSmall))
        }
        state.statusMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.primary, style = typography.bodyMedium)
            Spacer(Modifier.height(dimension.componentSpacingSmall))
        }

        ErastosButton(
            onClick = { onEvent(AuthEvent.Submit) },
            enabled = !state.isSubmitting && !state.isCheckingSession,
            modifier = Modifier.fillMaxWidth(),
            content = {
                if (state.isSubmitting || state.isCheckingSession) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimension.iconSmall),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        when (state.mode) {
                            AuthMode.LOGIN -> "Sign in"
                            AuthMode.SIGN_UP -> "Create account"
                            AuthMode.FORGOT_PASSWORD -> "Send reset link"
                            AuthMode.UPDATE_PASSWORD -> "Update password"
                        },
                        style = CustomTypography.buttonText
                    )
                }
            }
        )

        when (state.mode) {
            AuthMode.LOGIN -> {
                TextButton(
                    onClick = { onEvent(AuthEvent.OpenForgotPassword) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Forgot password?", style = CustomTypography.buttonText) }
                TextButton(
                    onClick = { onEvent(AuthEvent.OpenSignUp) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("New to Erastos? Sign up", style = CustomTypography.buttonText) }
            }
            AuthMode.SIGN_UP, AuthMode.FORGOT_PASSWORD -> TextButton(
                onClick = { onEvent(AuthEvent.OpenLogin) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Back to sign in", style = CustomTypography.buttonText) }
            AuthMode.UPDATE_PASSWORD -> Unit
        }
    }
}

@Composable
fun LibraryOnboardingScreen(
    onContinue: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(dimension.screenPaddingHorizontal),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Erastos", style = typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(dimension.componentSpacingMedium))
        Text(
            "Your library belongs to your account. Scan a book, search for it, or add its details manually to start building your catalog.",
            style = typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(dimension.paddingLarge))
        ErastosButton(onClick = onContinue, text = "Start my library")
        TextButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("Sign out", style = CustomTypography.buttonText)
        }
    }
}

@DevicePreviews
@Composable
private fun AuthScreenLoginPreview() {
    ErastosTheme {
        AuthScreen(
            state = AuthViewState(
                email = "reader@example.com",
                isCheckingSession = false
            ),
            onEvent = {}
        )
    }
}

@DevicePreviews
@Composable
private fun AuthScreenSignUpPreview() {
    ErastosTheme {
        AuthScreen(
            state = AuthViewState(
                mode = AuthMode.SIGN_UP,
                email = "reader@example.com",
                password = "password123",
                confirmPassword = "password123",
                isCheckingSession = false
            ),
            onEvent = {}
        )
    }
}

@DevicePreviews
@Composable
private fun LibraryOnboardingScreenPreview() {
    ErastosTheme {
        LibraryOnboardingScreen(onContinue = {}, onSignOut = {})
    }
}
