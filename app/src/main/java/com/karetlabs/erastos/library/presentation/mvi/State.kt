package com.karetlabs.erastos.library.presentation.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface HasState<T : BaseViewState> {

    val viewState: T
    val viewStateFlow: StateFlow<T>
    fun updateState(reducer: (T) -> T)
}

class HasStateImpl<T : BaseViewState>(
    initialState: T
) : HasState<T> {

    private val _state = MutableStateFlow(initialState)

    override val viewStateFlow: StateFlow<T>
        get() = _state

    override var viewState: T
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun updateState(reducer: (T) -> T) {
        viewState = reducer(viewState)
    }
}

@Composable
fun <T : BaseViewState> HasState<T>.observableViewState(): State<T> {
    return viewStateFlow.collectAsStateWithLifecycle()
}

interface BaseViewState