package com.example.mustafakocer.presentation.common

import com.example.mustafakocer.R
import com.example.mustafakocer.domain.exception.AppException
import javax.inject.Inject

/**
 * A centralized mapper to convert domain-level [AppException] objects into presentation-level
 * [UiError] models. This decouples the UI from the specifics of the domain exceptions,
 * allowing the UI to only be concerned with displaying the final, user-friendly error state.
 */
class UiErrorMapper @Inject constructor() {

    /**
     * Maps a given [AppException] to a corresponding [UiError].
     */
    fun map(exception: AppException): UiError {
        return when (exception) {
            is AppException.Network.NoInternet -> UiError(
                title = R.string.error_title_no_internet,
                subtitle = R.string.error_subtitle_no_internet,
                icon = R.drawable.ic_cloud_off_24
            )

            is AppException.Server.Unauthorized, is AppException.Session.MissingSessionData -> UiError(
                title = R.string.error_title_unauthorized,
                subtitle = R.string.error_subtitle_unauthorized,
                icon = R.drawable.ic_lock_24,
                retryButtonText = R.string.action_login
            )

            is AppException.Server -> UiError(
                title = R.string.error_title_server,
                subtitle = R.string.error_subtitle_server,
                icon = R.drawable.ic_server_error_24
            )

            else -> UiError(
                title = R.string.error_title_unknown,
                subtitle = R.string.error_subtitle_unknown,
                icon = R.drawable.ic_error_outline_24
            )
        }
    }
}