package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.UserDetailDto
import com.example.mustafakocer.data.model.dto.UserUpdateDto
import com.example.mustafakocer.data.model.entity.UserEntity
import com.example.mustafakocer.domain.model.User

fun UserDetailDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email.orEmpty(),
        username = this.username.orEmpty(),
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        image = this.image.orEmpty(),
        phone = this.phone.orEmpty(),
        birthDate = this.birthDate.orEmpty(),
        age = this.age ?: 0,
        gender = this.gender.orEmpty() // GÜNCELLENDİ
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phone = this.phone,
        username = this.username,
        age = this.age,
        imageUrl = this.image,
        gender = this.gender // GÜNCELLENDİ
    )
}

fun UserDetailDto.toDomain(): User {
    return User(
        id = this.id,
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        email = this.email.orEmpty(),
        phone = this.phone.orEmpty(),
        username = this.username.orEmpty(),
        age = this.age ?: 0,
        imageUrl = this.image.orEmpty(),
        gender = this.gender.orEmpty() // GÜNCELLENDİ
    )
}

// Bu dosyaya aşağıdaki fonksiyonu ekleyelim

// Domain modelinden (User) -> Data katmanı modeline (UserUpdateDto) dönüşüm
fun User.toUpdateDto(): UserUpdateDto {
    return UserUpdateDto(
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phone = this.phone,
        age = this.age
        // Not: UserUpdateDto'da olmayan 'username', 'gender', 'imageUrl' gibi
        // alanlar burada bilinçli olarak map'lenmez.
    )
}