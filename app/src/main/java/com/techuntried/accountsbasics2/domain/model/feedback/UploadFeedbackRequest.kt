package com.techuntried.accountsbasics2.domain.model.feedback

import kotlinx.serialization.Serializable

@Serializable
data class UploadFeedbackRequest(
    val comment: String,
    val sender: String,
    val userId: Int?
)