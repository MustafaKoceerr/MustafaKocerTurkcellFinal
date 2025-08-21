package com.example.mustafakocer.domain.exception

/**
 * The single source of truth for all handled exceptions in the application.
 *
 * This sealed class provides a type-safe hierarchy for all predictable errors,
 * ensuring that different layers can communicate errors in a structured way without
 * depending on specific implementation details of other layers.
 *
 * It represents WHAT went wrong, not HOW it should be displayed to the user.
 */
sealed class AppException(
    val developerMessage: String,
    override val cause: Throwable? = null,
) : Exception(developerMessage, cause) {

    /**
     * Represents errors originating from network infrastructure issues (e.g., no connectivity, timeouts).
     */
    sealed class Network(message: String, cause: Throwable? = null) : AppException(message, cause) {
        data class NoInternet(override val cause: Throwable?) :
            Network("No internet connection available.", cause)

        data class Timeout(override val cause: Throwable?) :
            Network("The request timed out.", cause)
    }

    /**
     * Represents errors originating from the server's HTTP responses (e.g., 4xx, 5xx codes).
     */
    sealed class Server(val code: Int, message: String, cause: Throwable? = null) :
        AppException(message, cause) {
        data class Unauthorized(override val cause: Throwable?) :
            Server(401, "Unauthorized access. Token might be invalid or expired.", cause)

        data class NotFound(override val cause: Throwable?) :
            Server(404, "The requested resource was not found.", cause)

        data class ServiceUnavailable(override val cause: Throwable?) :
            Server(503, "The service is temporarily unavailable.", cause)

        data class Unexpected(val httpCode: Int, override val cause: Throwable?) :
            Server(httpCode, "An unexpected HTTP error occurred: $httpCode", cause)
    }

    /**
     * Represents errors that occur during data processing (e.g., parsing JSON, database issues).
     */
    sealed class Data(message: String, cause: Throwable? = null) : AppException(message, cause) {
        data class Parsing(override val cause: Throwable?) :
            Data("Failed to parse data.", cause)

        data class EmptyResponse(override val cause: Throwable? = null) :
            Data("The response from the server was empty.", cause)

        data class InputError(val reason: String) : Data(reason, null)
    }

    /**
     * Represents errors related to the user's session state before a network call is made.
     */
    sealed class Session(message: String, cause: Throwable? = null) : AppException(message, cause) {
        data class MissingSessionData(val reason: String) : Session(reason, null)
    }

    /**
     * A catch-all for any unexpected exceptions that are not explicitly handled.
     */
    data class Unknown(override val cause: Throwable?) :
        AppException("An unknown error occurred.", cause)
}