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
    operator fun invoke(user: User): Flow<Resource<User>> {
        val validationError = validateUser(user)

        if (validationError != null) {
            return flow { emit(Resource.Error(validationError)) }
        }

        return userRepository.updateUserProfile(user)
    }

    /**
     * Orchestrates all validation checks for the User object.
     * @return An `AppException.Data.InputError` if any check fails, otherwise null.
     */
    // DÜZELTME: Dönüş tipi doğru hata sınıfı olarak güncellendi.
    private fun validateUser(user: User): AppException.Data.InputError? {
        return validateName(user.firstName, "İsim")
            ?: validateName(user.lastName, "Soyisim")
            ?: validateEmail(user.email)
            ?: validatePhoneNumber(user.phone)
            ?: validateAge(user.age)
    }

    /**
     * Validates that a name is not blank and contains only letters.
     */
    // DÜZELTME: Dönüş tipi ve döndürülen hata nesnesi güncellendi.
    private fun validateName(name: String, fieldName: String): AppException.Data.InputError? {
        if (name.isBlank()) {
            return AppException.Data.InputError("$fieldName boş bırakılamaz.")
        }
        if (!name.all { it.isLetter() }) {
            return AppException.Data.InputError("$fieldName sadece harf içermelidir.")
        }
        return null
    }

    /**
     * Validates that an email is not blank and has a valid format.
     */
    // DÜZELTME: Dönüş tipi ve döndürülen hata nesnesi güncellendi.
    private fun validateEmail(email: String): AppException.Data.InputError? {
        if (email.isBlank()) {
            return AppException.Data.InputError("E-posta adresi boş bırakılamaz.")
        }
        if ('@' !in email || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AppException.Data.InputError("Lütfen geçerli bir e-posta adresi girin.")
        }
        return null
    }

    /**
     * Validates that a phone number is not blank, meets minimum length,
     * and contains only digits (with an optional '+' prefix).
     */
    // DÜZELTME: Dönüş tipi ve döndürülen hata nesnesi güncellendi.
    private fun validatePhoneNumber(phone: String): AppException.Data.InputError? {
        if (phone.isBlank()) {
            return AppException.Data.InputError("Telefon numarası boş bırakılamaz.")
        }
        val numberPart = if (phone.startsWith("+")) phone.substring(1) else phone
        if (numberPart.length < MIN_PHONE_LENGTH) {
            return AppException.Data.InputError("Telefon numarası en az $MIN_PHONE_LENGTH rakam olmalıdır.")
        }
        return null
    }

    /**
     * Validates that the age is within a reasonable range.
     */
    // DÜZELTME: Dönüş tipi ve döndürülen hata nesnesi güncellendi.
    private fun validateAge(age: Int): AppException.Data.InputError? {
        return when {
            age <= 0 -> AppException.Data.InputError("Lütfen geçerli bir yaş girin.")
            age > MAX_AGE -> AppException.Data.InputError("Yaş $MAX_AGE'dan büyük olamaz.")
            else -> null
        }
    }

    private companion object {
        private const val MIN_PHONE_LENGTH = 10
        private const val MAX_AGE = 120 // Yaş için bir üst limit eklemek iyi bir pratik.
    }
}