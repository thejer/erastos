package com.karetlabs.erastos.library.presentation

interface PresentationDestination {
    val fromDeepLink: Boolean
        get() = false
}