package com.trilogy.mathlearning.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Request ---
@Serializable
data class CreatePracticeReqDto(
    val grade: Int,
    val chapterId: Int? = null,
    val examType: String? = null
)

@Serializable
data class SubmitAnswerReqDto(
    val exerciseId: String,
    val userAnswer: Int
)

@Serializable
data class SubmitPracticeReqDto(
    val practiceId: String,
    val timeSpent: Int,
    val answers: List<SubmitAnswerReqDto>
)

// --- Response ---
@Serializable
enum class PracticeStatus {
    @SerialName("in_progress")
    IN_PROGRESS,

    @SerialName("completed")
    COMPLETED,

    @SerialName("abandoned")
    ABANDONED
}

@Serializable
data class PracticeResDto(
    val practiceId: String,
    val userEmail: String,
    val grade: Int,
    val chapterId: Int? = null,
    val exerciseIds: List<String> = emptyList(),
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val score: Int = 0,
    // cho nullable + default, tránh crash nếu backend không gửi status
    val status: PracticeStatus? = PracticeStatus.IN_PROGRESS,
    val timeSpent: Int = 0,
    val startedAt: Long? = null,
    val completedAt: Long? = null
)

@Serializable
data class ExerciseResDto(
    val id: String,
    val chapterId: Int,
    val problem: String,
    val difficulty: String,
    val choices: List<String>,
    val result: Int,
    val solution: String,
    val userAnswer: Int? = null // added de
)

@Serializable
data class ListPracticesResDto(
    val practices: List<PracticeResDto>,
    val total: Int
)
