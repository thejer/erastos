package com.karetlabs.erastos.library.presentation.mvi

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


interface HasIntents<T : BaseIntent> {

    val intentsFlow: SharedFlow<T>
    fun sendIntent(scope: CoroutineScope, intent: T)
}

class HasIntentsImpl<T : BaseIntent> : HasIntents<T> {

    private val _intentsFlow = MutableSharedFlow<T>()
    override val intentsFlow: SharedFlow<T> get() = _intentsFlow

    override fun sendIntent(scope: CoroutineScope, intent: T) {
        scope.launch {
            _intentsFlow.emit(intent)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T : BaseIntent> HasIntents<T>.subscribeForIntents(onIntent: (T) -> Unit) {
    LaunchedEffect(Unit) {
        intentsFlow.collect { onIntent(it) }
    }
}

interface BaseIntent