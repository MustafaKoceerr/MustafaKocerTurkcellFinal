package com.example.mustafakocer.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.presentation.mvi.BaseUiEffect
import com.example.mustafakocer.presentation.mvi.BaseUiEvent
import com.example.mustafakocer.presentation.mvi.BaseUiState
import com.example.mustafakocer.presentation.mvi.UiContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * An abstract ViewModel that enforces a consistent MVI pattern.
 * It manages the state and effect streams, providing a structured foundation for all ViewModels.
 *
 * @param State The type of the UI state.
 * @param Event The type of the UI events.
 * @param Effect The type of the UI side-effects.
 * @param initialState The initial state of the UI.
 */
abstract class BaseViewModel<
        State : BaseUiState,
        Event : BaseUiEvent,
        Effect : BaseUiEffect,
        >(
    initialState: State,
) : ViewModel(), UiContract<State, Event, Effect> {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiEffect = Channel<Effect>()
    override val uiEffect: Flow<Effect> = _uiEffect.receiveAsFlow()

    /**
     * The public entry point for the View to send events.
     * This method delegates the event handling to the protected [handleEvent] method.
     */
    final override fun onEvent(event: Event) {
        handleEvent(event)
    }

    /**
     * Handles incoming UI events. This must be implemented by subclasses.
     */
    protected abstract fun handleEvent(event: Event)

    /**
     * Provides read-only access to the current UI state.
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Updates the UI state in an immutable way by applying a reducer function
     * that transforms the current state into a new state.
     *
     * @param reduce A lambda that receives the current state and returns a new state.
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.update { currentState.reduce() }
    }

    /**
     * Sends a one-time side-effect to be consumed by the UI.
     *
     * @param effect The side-effect to be sent.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }
}