package com.trilogy.mathlearning.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TopUserDto(
    val name: String,
    val email: String,
    val point: Int,
    val rank: Int
)

@Serializable
data class UserRankDto(
    val point: Int,
    val rank: Int
)

@Serializable
data class TopUserLikeDto(
    val name: String,
    val email: String,
    val like: Int,
    val rank: Int
)

@Serializable
data class UserRankLikeDto(
    val like: Int,
    val rank: Int
)

@Serializable
data class StatisticResDto(
    val topUsers: List<TopUserDto>,
    val currentUser: UserRankDto
)

@Serializable
data class StatisticLikeResDto(
    val topUsers: List<TopUserLikeDto>,
    val currentUser: UserRankLikeDto
)

@Serializable
data class WeakTopicDto(
    val topic: String,
    val questionCount: Int
)

@Serializable
data class UserProgressResDto(
    val weakTopics: List<WeakTopicDto>
)
