package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.CreateQuestionReqDto
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.domain.model.QuestionsEnvelope
import retrofit2.http.*

interface QuestionApi {
    @POST("/api/questions")
    suspend fun createQuestion(@Body body: CreateQuestionReqDto): QuestionResDto

    @GET("/api/questions")
    suspend fun getQuestions(): QuestionsEnvelope

    @GET("/api/questions/question-detail")
    suspend fun getQuestionDetail(@Query("id") id: String): QuestionResDto
}
