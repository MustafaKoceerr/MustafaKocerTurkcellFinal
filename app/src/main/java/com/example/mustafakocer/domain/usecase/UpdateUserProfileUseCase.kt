package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Encapsulates the business logic for updating a user's profile.
 * Its primary responsibility is to validate the user data before passing it to the repository.
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(user: User): Flow<Resource<User>> {
        val validationError = validateUser(user)
        if (validationError != null) {
            return flowOf(Resource.Error(validationError))
        }
        return userRepository.updateUserProfile(user)
    }

    /**
     * Orchestrates all validation checks for the User object.
     * @return An [AppException.Data.InputError] if any check fails, otherwise null.
     */
    private fun validateUser(user: User): AppException.Data.InputError? {
        return validateName(user.firstName, "First name")
            ?: validateName(user.lastName, "Last name")
            ?: validateEmail(user.email)
            ?: validatePhoneNumber(user.phone)
            ?: validateAge(user.age)
    }

    private fun validateName(name: String, fieldName: String): AppException.Data.InputError? {
        if (name.isBlank()) {
            return AppException.Data.InputError("$fieldName cannot be empty.")
        }
        if (!name.all { it.isLetter() }) {
            return AppException.Data.InputError("$fieldName must contain only letters.")
        }
        return null
    }

    /**
     * NOTE: This validation uses `android.util.Patterns`, creating a dependency on the
     * Android framework within the domain layer. This is a pragmatic trade-off to avoid
     * maintaining a complex regex, but violates strict Clean Architecture principles.
     */
    private fun validateEmail(email: String): AppException.Data.InputError? {
        if (email.isBlank()) {
            return AppException.Data.InputError("Email address cannot be empty.")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AppException.Data.InputError("Please enter a valid email address.")
        }
        return null
    }

    private fun validatePhoneNumber(phone: String): AppException.Data.InputError? {
        if (phone.isBlank()) {
            return AppException.Data.InputError("Phone number cannot be empty.")
        }
        val numberPart = phone.removePrefix("+")
        if (numberPart.length < MIN_PHONE_LENGTH) {
            return AppException.Data.InputError("Phone number must be at least $MIN_PHONE_LENGTH digits.")
        }
        return null
    }

    private fun validateAge(age: Int): AppException.Data.InputError? {
        return when {
            age <= 0 -> AppException.Data.InputError("Please enter a valid age.")
            age > MAX_AGE -> AppException.Data.InputError("Age cannot be greater than $MAX_AGE.")
            else -> null
        }
    }

    private companion object {
        private const val MIN_PHONE_LENGTH = 10
        private const val MAX_AGE = 120
    }
}