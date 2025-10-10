package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.UserDto
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("/api/user")
    suspend fun me(@Header("Authorization") bearer: String = ""): UserDto
}