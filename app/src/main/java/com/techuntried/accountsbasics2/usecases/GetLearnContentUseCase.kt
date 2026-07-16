package com.techuntried.accountsbasics2.usecases

import android.util.Log
import com.techuntried.accountsbasics2.data.mappers.asLearnContentEntity
import com.techuntried.accountsbasics2.data.mappers.asLearnContentModel
import com.techuntried.accountsbasics2.data.mappers.asQuestionEntity
import com.techuntried.accountsbasics2.data.mappers.asQuestionModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetLearnContentUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(subjectId: Int, chapterId: Int): ApiResult<List<LearnContentModel>> {
        val localResult = repository.getLocalLearnContent(subjectId, chapterId)
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
                    val remoteResponse = repository.fetchRemoteLearnContent(subjectId, chapterId)

                    if (remoteResponse is ApiResult.Success && remoteResponse.data.status) {
                        val entities = remoteResponse.data.questions.map { it.asLearnContentEntity() }
                        repository.saveLearnContent(subjectId, chapterId, entities)
                        repository.markQuestionsUpdated()
                        return ApiResult.Success(entities.map { it.asLearnContentModel() })
                    }
                } else {
                    repository.markQuestionsUpdated()
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                // If the network check fails, we don't crash; we just fall through to local data
            }
        }

        return if (hasLocalData) {
            ApiResult.Success(localResult.data.map { it.asLearnContentModel() })
        } else {
            ApiResult.Error("Failed to fetch questions.")
        }
    }
}