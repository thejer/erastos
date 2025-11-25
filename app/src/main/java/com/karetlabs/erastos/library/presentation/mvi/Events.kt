package com.karetlabs.erastos.library.presentation.mvi

interface HasEvents<T : BaseEvent> {
    fun onEvent(event: T)
}

interface BaseEvent