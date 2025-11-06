package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.UserResDto
import retrofit2.http.GET

interface UserApi {
    @GET("/api/user")
    suspend fun getMyUser(): UserResDto
}