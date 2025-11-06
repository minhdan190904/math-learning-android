// domain/model/practice.kt
package com.trilogy.mathlearning.domain.model

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
enum class PracticeStatus { PENDING, DOING, COMPLETED }

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
    val status: PracticeStatus = PracticeStatus.PENDING,
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
    val result: Int,      // chỉ dùng khi chấm/hiển thị lời giải
    val solution: String
)

@Serializable
data class ListPracticesResDto(
    val practices: List<PracticeResDto>,
    val total: Int
)
