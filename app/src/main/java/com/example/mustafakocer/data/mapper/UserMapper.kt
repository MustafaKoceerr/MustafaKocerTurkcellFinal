package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.UserDetailDto
import com.example.mustafakocer.data.model.dto.UserUpdateDto
import com.example.mustafakocer.data.model.entity.UserEntity
import com.example.mustafakocer.domain.model.User

/**
 * Private factory function to create a [User] domain model.
 * Encapsulates the common mapping logic to prevent code duplication.
 */
private fun createUserDomainModel(
    id: Int,
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    username: String,
    age: Int,
    imageUrl: String,
    gender: String
): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    username = username,
    age = age,
    imageUrl = imageUrl,
    gender = gender
)

/**
 * Converts a [UserDetailDto] from the data layer to a [UserEntity] for database caching.
 */
fun UserDetailDto.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email.orEmpty(),
    username = username.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    image = image.orEmpty(),
    phone = phone.orEmpty(),
    birthDate = birthDate.orEmpty(),
    age = age ?: 0,
    gender = gender.orEmpty()
)

/**
 * Converts a [UserEntity] from the database to a [User] in the domain layer.
 */
fun UserEntity.toDomain(): User = createUserDomainModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    username = username,
    age = age,
    imageUrl = image,
    gender = gender
)

/**
 * Converts a [UserDetailDto] from the data layer to a [User] in the domain layer.
 */
fun UserDetailDto.toDomain(): User = createUserDomainModel(
    id = id,
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    email = email.orEmpty(),
    phone = phone.orEmpty(),
    username = username.orEmpty(),
    age = age ?: 0,
    imageUrl = image.orEmpty(),
    gender = gender.orEmpty()
)

/**
 * Converts a [User] from the domain layer to a [UserUpdateDto] for the data layer.
 * This is used when sending updated user data to the server.
 */
fun User.toUpdateDto(): UserUpdateDto = UserUpdateDto(
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    age = age
)
