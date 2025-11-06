// domain/model/math_config.kt
package com.trilogy.mathlearning.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterResDto(
    val id: Int,
    val title: String,
    val description: String? = null
)

@Serializable
data class GradeCurriculumResDto(
    val grade: Int,
    val title: String,
    val chapters: List<ChapterResDto> = emptyList()
)

@Serializable
data class MathConfigResDto(
    @SerialName("curriculum")
    val curriculum: List<GradeCurriculumResDto> = emptyList()
)
