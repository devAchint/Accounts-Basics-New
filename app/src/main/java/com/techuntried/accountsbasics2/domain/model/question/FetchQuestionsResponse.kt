package com.techuntried.accountsbasics2.domain.model.question

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchQuestionsResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val questions: List<QuestionApiResponse>
)

@Serializable
data class QuestionApiResponse(
    val categoryId: Int,
    val levelId: Int,
    val correctOptionId: Int,
    val options: List<Option>,
    val questionId: Int,
    val questionText: String
)

@Serializable
data class Option(
    val optionId: Int,
    val optionText: String
)