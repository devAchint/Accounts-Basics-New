package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asQuestionEntity
import com.techuntried.accountsbasics2.data.mappers.asQuestionModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetSingleQuestionUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val networkMonitor: NetworkMonitor,
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        subjectId: Int,
        chapterId: Int,
        questionId: Int
    ): ApiResult<List<QuestionModel>> {
        // 1. Check Local Source first
        val localResult = quizRepository.getSingleLocalQuestion(subjectId, chapterId, questionId)
        if (localResult is ApiResult.Success) {
            return ApiResult.Success(localResult.data.map { it.asQuestionModel() })
        }

        // 2. Check Connectivity
        if (!networkMonitor.isConnected()) {
            return ApiResult.Error("No internet connection")
        }

        // 3. Fetch from Remote
        return fetchSingleQuestionRemote(subjectId, chapterId, questionId)
    }

    private suspend fun fetchSingleQuestionRemote(
        subjectId: Int,
        chapterId: Int,
        questionId: Int
    ): ApiResult<List<QuestionModel>> {
        return try {
            when (val response =
                networkRepository.fetchSingleQuestion(subjectId, chapterId, questionId)) {
                is ApiResult.Success -> {
                    if (response.data.status) {
                        val model =
                            response.data.questions.map { it.asQuestionEntity().asQuestionModel() }
                        ApiResult.Success(model)
                    } else {
                        ApiResult.Error(response.data.message)
                    }
                }

                is ApiResult.Error -> ApiResult.Error(response.errorMessage)
            }
        } catch (e: Exception) {
            ApiResult.Error("Oops! Something went wrong.")
        }
    }
}