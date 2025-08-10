package com.example.mustafakocer.domain.presentation

import com.example.mustafakocer.domain.exception.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Her ekranın UI state'i için temel sözleşmeyi tanımlar.
 * Bu arayüz, her özelliğin UI state'inin yüklenme ve hata durumlarını
 * tutarlı bir şekilde işlemesini garanti eder.
 */
interface BaseUiState {
    val isLoading: Boolean
    val error: AppException?
}

/**
 * Tüm UI event'leri için bir işaretçi arayüzü.
 */
interface BaseUiEvent

/**
 * Tek seferlik UI effect'leri (yan etkiler) için bir işaretçi arayüzü.
 * Örn: Navigasyon, Snackbar gösterme.
 */
interface BaseUiEffect


/**
 * Bir UI bileşeni (View) ve ViewModel arasındaki standart MVI sözleşmesini tanımlar.
 *
 * @param State UI state'inin türü.
 * @param Event UI event'lerinin türü.
 * @param Effect Yan etkilerin türü.
 */
interface UiContract<State, Event, Effect> {
    val uiState: StateFlow<State>
    // ARTIK DAHA GENEL BİR TİP BEKLİYORUZ
    val uiEffect: Flow<Effect>
    fun onEvent(event: Event)
}