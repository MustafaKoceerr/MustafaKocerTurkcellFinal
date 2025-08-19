package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.LoginResponseDto
import com.example.mustafakocer.domain.model.AuthSession

/**
Converts a [LoginResponseDto] from the data layer into an [AuthSession] domain model.
This strips away unnecessary user details, retaining only the data required for
managing the user's session (e.g., token, user ID).
 */
fun LoginResponseDto.toAuthSession(): AuthSession = AuthSession(
    token = token,
    userId = id
)