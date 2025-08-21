package com.example.mustafakocer.presentation.mvi

import com.example.mustafakocer.domain.exception.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines the base contract for any screen's UI state.
 * This ensures that every feature's UI state consistently handles loading and error conditions.
 */
interface BaseUiState {
    val isLoading: Boolean
    val error: AppException?
}

/**
 * A marker interface for all UI events.
 * Events are user actions or system triggers that intend to change the state.
 */
interface BaseUiEvent

/**
 * A marker interface for one-time UI effects.
 * Effects are side-effects that should be consumed only once, such as navigation,
 * showing a Snackbar, or triggering a toast.
 */
interface BaseUiEffect

/**
 * Defines the standard MVI contract between a UI component (View) and a ViewModel.
 *
 * @param State The type of the UI state.
 * @param Event The type of the UI events.
 * @param Effect The type of the UI effects.
 */
interface UiContract<State, Event, Effect> {
    /**
     * The single source of truth for the screen's state, represented as a [StateFlow].
     */
    val uiState: StateFlow<State>

    /**
     * A stream of one-time UI effects, typically represented as a [Flow] or [SharedFlow].
     */
    val uiEffect: Flow<Effect>

    /**
     * The entry point for the View to send events to the ViewModel.
     */
    fun onEvent(event: Event)
}