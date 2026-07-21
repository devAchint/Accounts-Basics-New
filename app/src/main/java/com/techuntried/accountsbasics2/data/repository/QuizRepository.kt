package com.techuntried.accountsbasics2.data.repository

import android.util.Log
import com.techuntried.accountsbasics2.data.database.SubjectDao
import com.techuntried.accountsbasics2.data.database.ChaptersDao
import com.techuntried.accountsbasics2.data.database.LearnContentDao
import com.techuntried.accountsbasics2.data.database.QuestionDao
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val subjectDao: SubjectDao,
    private val chaptersDao: ChaptersDao,
    private val questionDao: QuestionDao,
    private val learnContentDao: LearnContentDao
) {

    private var isCategoriesUpdatedInSession: Boolean = false
    private var cachedRemoteLevelDate: String? = null
    private val verifiedLevelsCategories = mutableSetOf<Int>()
    private var isQuestionsUpdatedInSession: Boolean = false

    fun markCategoriesUpdated() {
        isCategoriesUpdatedInSession = true
    }
    fun markLevelsUpdatedForCategory(categoryId: Int) {
        verifiedLevelsCategories.add(categoryId)
    }
    fun markQuestionsUpdated() {
        isQuestionsUpdatedInSession = true
    }

    fun wasCategoriesUpdatedThisSession(): Boolean = isCategoriesUpdatedInSession
    fun wasLevelsUpdatedThisSession(categoryId: Int) = verifiedLevelsCategories.contains(categoryId)
    fun wasQuestionsUpdatedThisSession(): Boolean = isQuestionsUpdatedInSession

    suspend fun getLocalSubjects(course: Int?): ApiResult<List<SubjectEntity>> {
        return try {
            val categories = if (course==null){
                subjectDao.getSubjects()
            }else{
                subjectDao.getSubjectsByGrades(course)
            }
            ApiResult.Success(categories)
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching categories. Please try again.")
        }
    }

    suspend fun saveSubjects(subjects: List<SubjectEntity>) {
        subjectDao.clearAndInsertSubjects(subjects)
        dataStoreRepository.saveCategoryLastUpdatedDate(currentDate())
    }

    suspend fun fetchRemoteSubjects() = networkRepository.fetchSubjects()

    suspend fun categoriesNeedsUpdate(): Boolean {
        val localDate = dataStoreRepository.getCategoryLastUpdatedDate() ?: return true
        val remoteResult = networkRepository.fetchCategoryLastUpdatedDate()

        return if (remoteResult is ApiResult.Success) {
            localDate < remoteResult.data.message // Assuming message is the date string
        } else true
    }


    //levels
    suspend fun getLocalLevels(categoryId: Int): ApiResult<List<ChapterEntity>> {
        return try {
            ApiResult.Success(chaptersDao.getChaptersBySubject(categoryId))
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching levels. Please try again.")
        }
    }

    suspend fun saveLevels(categoryId: Int, levels: List<ChapterEntity>) {
        chaptersDao.clearAndInsertLevels(categoryId, levels)
        dataStoreRepository.saveLevelLastUpdatedDate("$categoryId", currentDate())
    }

    suspend fun fetchRemoteChapters(categoryId: Int) =
        networkRepository.fetchChaptersBySubject(subjectId = categoryId)

    suspend fun levelsNeedsUpdate(categoryId: Int): Boolean {
        val localDate = dataStoreRepository.getLevelLastUpdatedDate("$categoryId") ?: return true

        // 1. Get remote date (from memory cache or network)
        val remoteDate = getOrFetchRemoteLevelDate() ?: return true

        // 2. Compare local vs remote
        return localDate < remoteDate
    }

    private suspend fun getOrFetchRemoteLevelDate(): String? {
        // Return cached date if we already have it
        cachedRemoteLevelDate?.let { return it }

        // Otherwise, fetch from network
        return when (val result = networkRepository.fetchLevelLastUpdatedDate()) {
            is ApiResult.Success -> {
                cachedRemoteLevelDate = result.data.message
                cachedRemoteLevelDate
            }

            is ApiResult.Error -> null
        }
    }

    //Questions
    suspend fun getLocalQuestions(categoryId: Int, levelId: Int): ApiResult<List<QuestionEntity>> {
        return try {
            ApiResult.Success(questionDao.getQuestions(categoryId, levelId))
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching questions. Please try again.")
        }
    }

    suspend fun saveQuestions(categoryId: Int, levelId: Int, questions: List<QuestionEntity>) {
        questionDao.clearAndInsertQuestions(categoryId, levelId, questions)
        dataStoreRepository.saveQuestionLastUpdatedDate(currentDate())
    }

    suspend fun fetchRemoteQuestions(categoryId: Int, levelId: Int) =
        networkRepository.fetchQuestionsByChapter(categoryId = categoryId, levelId = levelId)

    suspend fun questionsNeedsUpdate(): Boolean {
        val localDate = dataStoreRepository.getQuestionLastUpdatedDate() ?: return true
        val remoteResult = networkRepository.fetchQuestionLastUpdatedDate()

        return if (remoteResult is ApiResult.Success) {
            localDate < remoteResult.data.message // Assuming message is the date string
        } else true
    }

    //LearnContent
    suspend fun getLocalLearnContent(subjectId: Int, chapterId: Int): ApiResult<List<LearnContentEntity>> {
        return try {
            ApiResult.Success(learnContentDao.getContent(subjectId, chapterId))
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching questions. Please try again.")
        }
    }

    suspend fun saveLearnContent(subjectId: Int, chapterId: Int, questions: List<LearnContentEntity>) {
        learnContentDao.clearAndInsertContent(subjectId, chapterId, questions)
        dataStoreRepository.saveQuestionLastUpdatedDate(currentDate())
    }

    suspend fun fetchRemoteLearnContent(subjectId: Int, chapterId: Int) =
        networkRepository.fetchLearnContent(subjectId = subjectId, chapterId = chapterId)

    private fun currentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}

