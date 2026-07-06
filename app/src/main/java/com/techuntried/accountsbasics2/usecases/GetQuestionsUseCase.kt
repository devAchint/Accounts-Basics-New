package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asQuestionEntity
import com.techuntried.accountsbasics2.data.mappers.asQuestionModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(categoryId: Int, levelId: Int): ApiResult<List<QuestionModel>> {
        val localResult = repository.getLocalQuestions(categoryId, levelId)
        val hasLocalData = localResult is ApiResult.Success && localResult.data.isNotEmpty()
        val isOnline = networkMonitor.isConnected()

        if (!hasLocalData && !isOnline) {
            return ApiResult.Error("No internet connection")
        }

        val needsSessionCheck = !repository.wasQuestionsUpdatedThisSession()
        val shouldAttemptSync = isOnline && (!hasLocalData || needsSessionCheck)
        // We only call categoriesNeedsUpdate() if we actually have internet and haven't checked yet

        if (shouldAttemptSync) {
            try {
                if (!hasLocalData || repository.questionsNeedsUpdate()) {
                    val remoteResponse = repository.fetchRemoteQuestions(categoryId, levelId)

                    if (remoteResponse is ApiResult.Success && remoteResponse.data.status) {
                        val entities = remoteResponse.data.questions.map { it.asQuestionEntity() }
                        repository.saveQuestions(categoryId, levelId, entities)
                        repository.markQuestionsUpdated()
                        return ApiResult.Success(entities.map { it.asQuestionModel() })
                    }
                } else {
                    repository.markQuestionsUpdated()
                }
            } catch (e: Exception) {
                // If the network check fails, we don't crash; we just fall through to local data
            }
        }

        return if (hasLocalData) {
            ApiResult.Success(localResult.data.map { it.asQuestionModel() })
        } else {
            ApiResult.Error("Failed to fetch questions.")
        }
    }
}