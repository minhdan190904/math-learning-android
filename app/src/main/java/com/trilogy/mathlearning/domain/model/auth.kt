package com.trilogy.mathlearning.domain.model


import kotlinx.serialization.Serializable

@Serializable
data class LoginResDto(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class ResDto(
    val message: String
)

@Serializable
data class ActiveUserDto(
    val email: String,
    val confirmCode: String
)

@Serializable
data class RegisterDto(
    val name: String? = null,
    val email: String,
    val password: String
)

@Serializable
data class LoginDto(
    val email: String,
    val password: String
)

@Serializable
data class ForgotPasswordDto(
    val email: String
)

@Serializable
data class ResetPasswordDto(
    val newPassword: String,
    val confirmNewPassword: String,
    val forgotPasswordCode: String
)

@Serializable
data class RefreshTokenDto(
    val refreshToken: String
)
