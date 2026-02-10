package com.ondot.ui.base.mvi

import com.ondot.ui.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StateStore<S : UiState>(
    initialState: S,
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    val current: S
        get() = _state.value

    fun reduce(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }
}
