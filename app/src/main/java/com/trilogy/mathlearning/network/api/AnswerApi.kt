package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.AnswerResDto
import com.trilogy.mathlearning.domain.model.CreateAnswerReqDto
import com.trilogy.mathlearning.domain.model.ToggleLikeAnswerReqDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AnswerApi {
    @POST("/api/answers/create-answer")
    suspend fun createAnswer(@Body body: CreateAnswerReqDto): AnswerResDto

    @POST("/api/answers/toggle-like")
    suspend fun toggleLike(@Body body: ToggleLikeAnswerReqDto): AnswerResDto
}