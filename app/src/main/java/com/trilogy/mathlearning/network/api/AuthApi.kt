package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.ActiveUserDto
import com.trilogy.mathlearning.domain.model.ForgotPasswordDto
import com.trilogy.mathlearning.domain.model.LoginDto
import com.trilogy.mathlearning.domain.model.LoginResDto
import com.trilogy.mathlearning.domain.model.RefreshTokenDto
import com.trilogy.mathlearning.domain.model.RegisterDto
import com.trilogy.mathlearning.domain.model.ResDto
import com.trilogy.mathlearning.domain.model.ResetPasswordDto
import com.trilogy.mathlearning.domain.model.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterDto): UserDto

    @POST("/api/auth/activate")
    suspend fun activate(@Body body: ActiveUserDto): UserDto

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginDto): LoginResDto

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordDto): ResDto

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordDto): ResDto

    @POST("/api/auth/refresh-token")
    suspend fun refreshToken(@Body body: RefreshTokenDto): LoginResDto
}