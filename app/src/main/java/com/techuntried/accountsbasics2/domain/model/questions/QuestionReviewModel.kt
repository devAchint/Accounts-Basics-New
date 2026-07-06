package com.techuntried.accountsbasics2.domain.model.questions

import kotlinx.serialization.Serializable


@Serializable
data class QuestionReviewModel(
    val question: String,
    val answer: String,
    val correctAnswer: String
)