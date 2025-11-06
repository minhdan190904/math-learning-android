// network/api/MathConfigApi.kt
package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.MathConfigResDto
import retrofit2.http.GET

interface MathConfigApi {
    @GET("/api/math-config")
    suspend fun getMathConfig(): MathConfigResDto
}