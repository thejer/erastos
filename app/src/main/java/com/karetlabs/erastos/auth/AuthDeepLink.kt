package com.karetlabs.erastos.auth

object AuthDeepLink {
    const val SCHEME = "erastos"
    const val HOST = "auth"
    const val CALLBACK_URL = "$SCHEME://$HOST"
    const val RECOVERY_PATH = "/recovery"
    const val PREFERENCES_NAME = "erastos_auth"
    const val PASSWORD_RECOVERY_KEY = "password_recovery_pending"
    const val PASSWORD_RECOVERY_URL = "$CALLBACK_URL$RECOVERY_PATH"
}
