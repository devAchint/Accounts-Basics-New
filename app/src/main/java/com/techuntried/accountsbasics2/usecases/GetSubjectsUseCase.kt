package com.techuntried.accountsbasics2.usecases

import android.util.Log
import com.techuntried.accountsbasics2.data.mappers.asCategoryEntity
import com.techuntried.accountsbasics2.data.mappers.asSubjectModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {

    companion object {
        private const val TAG = "GetSubjectsUseCase"
    }

    suspend operator fun invoke(course: Int?): ApiResult<List<SubjectModel>> {
        Log.d(TAG, "Started. Course: $course")

        val localResult = repository.getLocalSubjects(course)
        val hasLocalData = localResult is ApiResult.Success && localResult.data.isNotEmpty()
        val isOnline = networkMonitor.isConnected()

        Log.d(TAG, "Has Local Data: $hasLocalData")
        Log.d(TAG, "Internet Connected: $isOnline")

        // 1. If we have no local data and no internet, we are stuck.
        if (!hasLocalData && !isOnline) {
            Log.d(TAG, "No local data and no internet. Returning error.")
            return ApiResult.Error("No internet connection")
        }

        val needsSessionCheck = !repository.wasCategoriesUpdatedThisSession()
        val shouldAttemptSync = isOnline && (!hasLocalData || needsSessionCheck)

        Log.d(TAG, "Needs Session Check: $needsSessionCheck")
        Log.d(TAG, "Should Attempt Sync: $shouldAttemptSync")

        if (shouldAttemptSync) {
            try {
                val needsUpdate = !hasLocalData || repository.categoriesNeedsUpdate()
                Log.d(TAG, "Categories Need Update: $needsUpdate")

                if (needsUpdate) {
                    Log.d(TAG, "Fetching subjects from remote...")

                    val remoteResponse = repository.fetchRemoteSubjects()

                    when (remoteResponse) {
                        is ApiResult.Success -> {
                            Log.d(
                                TAG,
                                "Remote fetch success. Status: ${remoteResponse.data.status}, Count: ${remoteResponse.data.categories.size}"
                            )

                            if (remoteResponse.data.status) {
                                val entities = remoteResponse.data.categories.map { it.asCategoryEntity() }

                                Log.d(TAG, "Saving ${entities.size} subjects locally.")

                                repository.saveSubjects(entities)
                                repository.markCategoriesUpdated()

                                val categories = if (course == null) {
                                    entities.map { it.asSubjectModel() }
                                } else {
                                    entities.filter { it.course == course }
                                        .map { it.asSubjectModel() }
                                }

                                Log.d(TAG, "Returning ${categories.size} subjects from remote.")

                                return ApiResult.Success(categories)
                            } else {
                                Log.d(TAG, "Remote response status was false.")
                            }
                        }

                        is ApiResult.Error -> {
                            Log.e(TAG, "Remote fetch failed: ${remoteResponse.errorMessage}")
                        }


                    }
                } else {
                    Log.d(TAG, "Categories are already up-to-date. Marking session updated.")
                    repository.markCategoriesUpdated()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while syncing categories.", e)
            }
        } else {
            Log.d(TAG, "Skipping remote sync.")
        }

        Log.d(TAG, "Returning local data.")

        return if (hasLocalData) {
            Log.d(TAG, "Local subjects count: ${localResult.data.size}")
            ApiResult.Success(localResult.data.map { it.asSubjectModel() })
        } else {
            Log.e(TAG, "No local data available. Returning error.")
            ApiResult.Error("Failed to fetch categories.")
        }
    }
}