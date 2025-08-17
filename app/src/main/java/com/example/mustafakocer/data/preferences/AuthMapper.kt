package com.example.mustafakocer.data.preferences

import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.domain.model.AuthSession

fun LoginResponseDto.toAuthSession(): AuthSession {
    return AuthSession(
        token = this.token,
        userId = this.id,
    )
}