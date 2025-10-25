package com.trilogy.mathlearning.ui.presentation.math

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ExamIndexItem(val id: String, val title: String, val durationMin: Int)

@Serializable
data class Choice(val label: String, val text: String? = null, val tex: String? = null)

@Serializable
data class Question(
    val id: String,
    val index: Int,
    val content: String,
    val choices: List<Choice>,
    val answer: String? = null
)

@Serializable
data class Exam(val id: String, val title: String, val durationMin: Int, val questions: List<Question>)

private val JSON = Json { ignoreUnknownKeys = true }

fun assetText(ctx: Context, path: String) = ctx.assets.open(path).bufferedReader().use { it.readText() }
fun parseExamIndex(raw: String) = JSON.decodeFromString<List<ExamIndexItem>>(raw)
fun parseExam(raw: String) = JSON.decodeFromString<Exam>(raw)
