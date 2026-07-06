package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asCategoryEntity
import com.techuntried.accountsbasics2.data.mappers.asCategoryModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.category.CategoryModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(grades: List<Int>?): ApiResult<List<CategoryModel>> {
        val localResult = repository.getLocalCategories(grades)
        val hasLocalData = localResult is ApiResult.Success && localResult.data.isNotEmpty()
        val isOnline = networkMonitor.isConnected()

        // 1. If we have no local data and no internet, we are stuck.
        if (!hasLocalData && !isOnline) {
            return ApiResult.Error("No internet connection")
        }

        // 2. Decide if we SHOULD try to sync
        // Try sync if: We have nothing locally OR (We are online AND haven't checked this session)
        val needsSessionCheck = !repository.wasCategoriesUpdatedThisSession()
        val shouldAttemptSync = isOnline && (!hasLocalData || needsSessionCheck)

        if (shouldAttemptSync) {
            try {
                if (!hasLocalData || repository.categoriesNeedsUpdate()) {
                    val remoteResponse = repository.fetchRemoteCategories()

                    if (remoteResponse is ApiResult.Success && remoteResponse.data.status) {
                        val entities = remoteResponse.data.categories.map { it.asCategoryEntity() }
                        repository.saveCategories(entities)
                        repository.markCategoriesUpdated()
                        val categories = if (grades == null) {
                            entities.map { it.asCategoryModel() }
                        } else {
                            entities .filter { it.grade in grades }.map { it.asCategoryModel() }
                        }
                        return ApiResult.Success(categories)
                    }
                } else {
                    repository.markCategoriesUpdated()
                }
            } catch (e: Exception) {
                // If the network check fails, we don't crash; we just fall through to local data
            }
        }

        // 3. Final Fallback: Return local data
        return if (hasLocalData) {
            ApiResult.Success(localResult.data.map { it.asCategoryModel() })
        } else {
            ApiResult.Error("Failed to fetch categories.")
        }
    }
}