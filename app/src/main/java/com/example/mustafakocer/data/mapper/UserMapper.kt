package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.UserDetailDto
import com.example.mustafakocer.data.model.entity.UserEntity
import com.example.mustafakocer.domain.model.User

// DTO -> Entity (DÜZELTİLDİ)
fun UserDetailDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id ?: 0,
        email = this.email.orEmpty(),
        username = this.username.orEmpty(),
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        image = this.image.orEmpty(),
        phone = this.phone.orEmpty(),
        birthDate = this.birthDate.orEmpty(),
        age = this.age ?: 0 // DÜZELTME: Eksik olan 'age' alanı eklendi.
    )
}

// Entity -> Domain (GÜNCELLENDİ)
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        // fullName'i burada atamaya gerek yok, kendi kendini hesaplayacak.
        email = this.email,
        phone = this.phone,
        username = this.username,
        age = this.age,
        imageUrl = this.image
    )
}

// UserDetailDto -> Domain (GÜNCELLENDİ)
fun UserDetailDto.toDomain(): User {
    return User(
        id = this.id ?: 0,
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        // fullName'i burada atamaya gerek yok.
        email = this.email.orEmpty(),
        phone = this.phone.orEmpty(),
        username = this.username.orEmpty(),
        age = this.age ?: 0,
        imageUrl = this.image.orEmpty()
    )
}