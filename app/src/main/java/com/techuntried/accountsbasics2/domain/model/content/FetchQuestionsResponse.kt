package com.techuntried.accountsbasics2.domain.model.content

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
    val subjectId: Int,
    val chapterId: Int,
    val correctOptionId: Int,
    val options: List<Option>,
    val questionId: Int,
    val questionText: String,
    val explanation:String,
)

@Serializable
data class Option(
    val id: Int,
    val text: String
)