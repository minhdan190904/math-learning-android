package com.trilogy.mathlearning.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateQuestionReqDto(
    val content: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class AnswerResDto(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String,
    val questionId: String,
    val authorEmail: String,
    val authorName: String,
    val content: String,
    val imageUrl: String? = null,
    val likedBy: List<String> = emptyList(),
    val likes: Int = 0
)

@Serializable
data class QuestionResDto(
    // API list/detail trả "questionId" -> map vào id bằng alternate
    @SerializedName(value = "id", alternate = ["questionId"])
    val id: String,
    val authorEmail: String,
    val authorName: String,
    val content: String? = null,
    val imageUrl: String? = null,
    val topic: String = "string",
    val answers: List<AnswerResDto> = emptyList()
)

/** Envelope cho /api/questions: { "docs": [ ... ] } */
@Serializable
data class QuestionsEnvelope(
    val docs: List<QuestionResDto> = emptyList()
)

@Serializable
data class ToggleLikeAnswerReqDto(val answerId: String)

@Serializable
data class CreateAnswerReqDto(
    val questionId: String,
    val content: String? = null,
    val imageUrl: String? = null
)
