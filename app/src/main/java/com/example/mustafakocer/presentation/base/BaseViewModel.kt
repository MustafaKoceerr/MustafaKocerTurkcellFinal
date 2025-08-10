package com.example.mustafakocer.presentation.base


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.presentation.BaseUiEffect
import com.example.mustafakocer.domain.presentation.BaseUiEvent
import com.example.mustafakocer.domain.presentation.BaseUiState
import com.example.mustafakocer.domain.presentation.UiContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Tutarlı bir MVI modelini zorunlu kılan soyut bir ViewModel.
 * State ve effect akışlarını yönetir, tüm ViewModel'ler için yapısal bir temel sağlar.
 *
 * @param State UI state'inin türü.
 * @param Event UI event'lerinin türü.
 * @param Effect Yan etkilerin türü.
 * @param initialState UI'ın başlangıç durumu.
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

    // SharedFlow yerine Channel kullanmak, effect'lerin sadece bir kez
    // tüketileceğini garanti etmenin daha sağlam bir yoludur.
    private val _uiEffect = Channel<Effect>()
    override val uiEffect: Flow<Effect> = _uiEffect.receiveAsFlow()

    /**
     * Gelen UI event'lerini işler. Bu, alt sınıflar tarafından implemente edilmelidir.
     */
    abstract override fun onEvent(event: Event)

    /**
     * Mevcut UI state'ine sadece okunabilir erişim sağlar.
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Mevcut state'i alıp yeni bir state döndüren bir 'reduce' fonksiyonu uygulayarak
     * UI state'ini değiştirilemez (immutable) bir şekilde günceller.
     *
     * @param reduce Mevcut state'i alıp yeni bir state döndüren lambda.
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.update { currentState.reduce() }
    }

    /**
     * UI tarafından tüketilecek tek seferlik bir yan etki gönderir.
     *
     * @param effect Gönderilecek yan etki.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }
}