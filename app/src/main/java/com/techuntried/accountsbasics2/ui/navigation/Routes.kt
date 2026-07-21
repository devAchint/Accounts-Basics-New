package com.techuntried.accountsbasics2.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


sealed class Routes {

    @Serializable
    object NotificationPermissionScreenRoute : Routes()

    @Serializable
    object HomeScreenRoute : Routes()

    @Serializable
    object SettingsScreenRoute : Routes()

    @Serializable
    object StartScreenRoute : Routes()

    @Serializable
    data class QuestionsScreenRoute(
        val subjectId: Int,
        val chapterId: Int,
        val timerCount: Int?
    ) : Routes()

    @Serializable
    data class ScoreScreenRoute(
        val subjectId: Int,
        val chapterId: Int,
        val isPracticeType: Boolean,
        val correctAnswers: Int,
        val totalQuestions: Int,
        val questionReview: List<QuestionReviewModel>
    ) : Routes()

    @Serializable
    data class RulesScreenRoute(
        val subjectId: Int,
        val chapterId: Int,
        val isPracticeType: Boolean
    ) : Routes()

    @Serializable
    data class ChaptersScreenRoute(
        val subjectId: Int,
        val subjectName: String,
        val showTopic: Boolean
    ) : Routes()

    @Serializable
    data object FeedbackScreenRoute : Routes()

    @Serializable
    data object ProgressScreenRoute : Routes()

    @Serializable
    data class SectionCategoriesScreenRoute(val section: String, val grades: List<Int>?) : Routes()

    @Serializable
    data class LearnScreenRoute(val subjectId: Int, val chapterId: Int) : Routes()

    @Serializable
    object ImproveScreenRoute : Routes()
}


inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}

