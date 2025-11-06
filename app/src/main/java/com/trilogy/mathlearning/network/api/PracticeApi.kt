package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.*
import retrofit2.http.*


interface PracticeApi {
    @POST("/api/practice/create")
    suspend fun createPractice(@Body body: CreatePracticeReqDto): PracticeResDto

    @GET("/api/practice/exercises/{practiceId}")
    suspend fun getExercises(@Path("practiceId") practiceId: String): List<ExerciseResDto>

    @POST("/api/practice/submit")
    suspend fun submitPractice(@Body body: SubmitPracticeReqDto): PracticeResDto

    @GET("/api/practice/list-practices")
    suspend fun listPractices(): ListPracticesResDto
}