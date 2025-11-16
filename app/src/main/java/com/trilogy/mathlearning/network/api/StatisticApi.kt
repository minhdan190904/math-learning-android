package com.trilogy.mathlearning.network.api

import com.trilogy.mathlearning.domain.model.StatisticLikeResDto
import com.trilogy.mathlearning.domain.model.StatisticResDto
import com.trilogy.mathlearning.domain.model.UserProgressResDto
import retrofit2.http.GET

interface StatisticApi {

    @GET("/api/statistic/top-users")
    suspend fun getTopUsersByPoints(): StatisticResDto

    @GET("/api/statistic/top-users-likes")
    suspend fun getTopUsersByLikes(): StatisticLikeResDto

    @GET("/api/statistic/progress")
    suspend fun getUserProgress(): UserProgressResDto
}
