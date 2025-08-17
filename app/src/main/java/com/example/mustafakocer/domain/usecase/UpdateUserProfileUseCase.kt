package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.repository.UserRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Validates the user profile data against a set of business rules
     * before requesting the update from the repository.
     */
    operator fun invoke(user: User): Flow<Resource<User>> {
        val validationError = validateUser(user)

        if (validationError != null) {
            // If there is a validation error, emit it immediately and stop.
            return flow { emit(Resource.Error(validationError)) }
        }

        // If all validations pass, proceed with the repository call.
        return userRepository.updateUserProfile(user)
    }

    /**
     * Orchestrates all validation checks for the User object.
     * @return An `AppException.Data.ValidationError` if any check fails, otherwise null.
     */
    private fun validateUser(user: User): AppException.Data.ValidationError? {
        return validateName(user.firstName, "İsim")
            ?: validateName(user.lastName, "Soyisim")
            ?: validateEmail(user.email)
            ?: validatePhoneNumber(user.phone)
            ?: validateAge(user.age) // YENİ: Yaş kontrolü eklendi
    }

    /**
     * Validates that a name is not blank and contains only letters.
     */
    private fun validateName(name: String, fieldName: String): AppException.Data.ValidationError? {
        if (name.isBlank()) {
            return AppException.Data.ValidationError("$fieldName boş bırakılamaz.")
        }
        if (!name.all { it.isLetter() }) {
            return AppException.Data.ValidationError("$fieldName sadece harf içermelidir.")
        }
        return null
    }

    /**
     * Validates that an email is not blank and has a valid format.
     */
    private fun validateEmail(email: String): AppException.Data.ValidationError? {
        if (email.isBlank()) {
            return AppException.Data.ValidationError("E-posta adresi boş bırakılamaz.")
        }
        // A simple check for the '@' symbol and using Android's pattern matcher.
        if ('@' !in email || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AppException.Data.ValidationError("Lütfen geçerli bir e-posta adresi girin.")
        }
        return null
    }

    /**
     * Validates that a phone number is not blank, meets minimum length,
     * and contains only digits (with an optional '+' prefix).
     */
    private fun validatePhoneNumber(phone: String): AppException.Data.ValidationError? {
        if (phone.isBlank()) {
            return AppException.Data.ValidationError("Telefon numarası boş bırakılamaz.")
        }
        // Remove prefix and check length
        val numberPart = if (phone.startsWith("+")) phone.substring(1) else phone
        if (numberPart.length < MIN_PHONE_LENGTH) {
            return AppException.Data.ValidationError("Telefon numarası en az $MIN_PHONE_LENGTH rakam olmalıdır.")
        }
        return null
    }

    /**
     * Validates that the age is within a reasonable range.
     */
    private fun validateAge(age: Int): AppException.Data.ValidationError? {
        val MAX_AGE = 110
        return when {
            age <= 0 -> AppException.Data.ValidationError("Lütfen geçerli bir yaş girin.")
            age > MAX_AGE -> AppException.Data.ValidationError("Yaş $MAX_AGE'dan büyük olamaz.")
            else -> null
        }
    }


    private companion object {
        private const val MIN_PHONE_LENGTH = 10
    }
}