package com.techuntried.accountsbasics2.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.techuntried.accountsbasics2.domain.model.GameEconomy
import com.techuntried.accountsbasics2.domain.model.user.UserModel
import com.techuntried.accountsbasics2.ui.settings.UserSettingsModel
import com.techuntried.accountsbasics2.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class DataStoreRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }


    //username
    suspend fun saveUsername(value: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_USERNAME] = value
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }


    fun getUserNameFlow(): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_USERNAME]
            }.catch {e->
                if (e is CancellationException) throw e
                emit(null)
            }
    }

    //streak
    suspend fun increaseStreak() {
        dataStore.edit { preferences ->
            val currentStreak = preferences[PreferenceKey.KEY_STREAK] ?: 0
            preferences[PreferenceKey.KEY_STREAK] = currentStreak + 1
        }
    }

    suspend fun getStreak(): Int {
        val preferences = dataStore.data.first()
        return preferences[PreferenceKey.KEY_STREAK] ?: 0
    }

    //isFirstTime
    suspend fun saveIsFirstTime(value: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_IS_FIRST_TIME] = value
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    suspend fun getIsFirstTime(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[PreferenceKey.KEY_IS_FIRST_TIME] ?: true
    }


    fun observeFirstTimeFlow(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_IS_FIRST_TIME] ?: true
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(true)
            }
    }

    //user profile

    suspend fun getUserProfile(): UserModel? {
        try {
            val preferences = dataStore.data.first()
            val json = preferences[PreferenceKey.KEY_USER_PROFILE]
            return json?.let {
                Json.decodeFromString<UserModel>(it)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return null
        }
    }

    fun getUserProfileFlow(): Flow<UserModel?> {
        return dataStore.data
            .map { preferences ->
                val json = preferences[PreferenceKey.KEY_USER_PROFILE]
                json?.let { Json.decodeFromString<UserModel>(it) }
            }
            .catch { e ->
                if (e is CancellationException) throw e
                Log.d("MYDEBUG", "${e.message}")
                emit(null)
            }
    }


    suspend fun saveUserProfile(userModel: UserModel?) {
        try {
            dataStore.edit { preferences ->
                if (userModel != null) {
                    val jsonString = json.encodeToString(userModel)
                    preferences[PreferenceKey.KEY_USER_PROFILE] = jsonString
                } else {
                    preferences.remove(PreferenceKey.KEY_USER_PROFILE)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", e.message ?: "Unknown error")
        }
    }

    //feedback count

    suspend fun getFeedbacksCount(): Int {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_FEEDBACKS_COUNT] ?: 0
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return 0
        }
    }

    suspend fun resetFeedbackCount() {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_FEEDBACKS_COUNT] = 0
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun incrementFeedbacksCount() {
        try {
            dataStore.edit { preferences ->
                val currentCount = preferences[PreferenceKey.KEY_FEEDBACKS_COUNT] ?: 0
                preferences[PreferenceKey.KEY_FEEDBACKS_COUNT] = currentCount + 1
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    //feedback date
    suspend fun getLastFeedbackDate(): String? {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_LAST_FEEDBACK_DATE]
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return null
        }
    }

    suspend fun saveLastFeedbackDate(date: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_LAST_FEEDBACK_DATE] = date
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    //categories last updated date
    suspend fun saveCategoryLastUpdatedDate(date: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_CATEGORY_LAST_UPDATED_DATE] = date
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun getCategoryLastUpdatedDate(): String? {
        try {
            return dataStore.data.first()[PreferenceKey.KEY_CATEGORY_LAST_UPDATED_DATE]
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return null
        }
    }

    //levels last updated date

    suspend fun saveLevelLastUpdatedDate(
        categoryId: String,
        date: String
    ) {
        try {
            val preferences = dataStore.data.first()
            val existingJson = preferences[PreferenceKey.KEY_LEVEL_LAST_UPDATED_DATE]

            val dateMap: MutableMap<String, String> =
                existingJson?.let {
                    json.decodeFromString<Map<String, String>>(it).toMutableMap()
                } ?: mutableMapOf()

            dateMap[categoryId] = date

            dataStore.edit { prefs ->
                prefs[PreferenceKey.KEY_LEVEL_LAST_UPDATED_DATE] =
                    json.encodeToString(dateMap)
            }

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "Error saving date: ${e.message}")
        }
    }

    suspend fun getLevelLastUpdatedDate(categoryId: String): String? {
        return try {
            val preferences = dataStore.data.first()
            val jsonData = preferences[PreferenceKey.KEY_LEVEL_LAST_UPDATED_DATE]

            val dateMap: Map<String, String> =
                jsonData?.let {
                    json.decodeFromString<Map<String, String>>(it)
                } ?: emptyMap()

            dateMap[categoryId]
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "Error fetching date: ${e.message}")
            null
        }
    }


    //questions last updated date
    suspend fun saveQuestionLastUpdatedDate(date: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_QUESTION_LAST_UPDATED_DATE] = date
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun getQuestionLastUpdatedDate(): String? {
        try {
            return dataStore.data.first()[PreferenceKey.KEY_QUESTION_LAST_UPDATED_DATE]
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return null
        }
    }


    //notification dialog
    suspend fun isNotificationDialogDismissed(): Boolean {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_NOTIFICATION_DIALOG_DISMISSED] ?: false
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            return false
        }
    }

    suspend fun setNotificationDialogDismissed(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.KEY_NOTIFICATION_DIALOG_DISMISSED] = value
        }
    }


    //score rating
    suspend fun isScoreRatingDismissed(): Boolean {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_SCORE_RATING_DISMISSED] ?: false
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return false
        }
    }

    suspend fun setScoreRatingDismissed(value: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_SCORE_RATING_DISMISSED] = value
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }


    //sound enabled
    suspend fun setSoundEnabled(isSoundEnabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_APP_SOUNDS] = isSoundEnabled
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun isSoundsEnabled(): Boolean {
        try {
            val preferences = dataStore.data.first()
            val isSoundOn = preferences[PreferenceKey.KEY_APP_SOUNDS] ?: true
            return isSoundOn
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return true
        }
    }

    fun getSoundFlow(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_APP_SOUNDS] ?: true
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(true)
            }
    }

    //sound enabled
    suspend fun setShowCorrect(isShowCorrect: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_SHOW_CORRECT] = isShowCorrect
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun isShowCorrectEnabled(): Boolean {
        try {
            val preferences = dataStore.data.first()
            val isShowCorrect = preferences[PreferenceKey.KEY_SHOW_CORRECT] ?: true
            return isShowCorrect
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return true
        }
    }

    fun getShowCorrectFlow(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_SHOW_CORRECT] ?: true
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(true)
            }
    }

    //timer enabled
    suspend fun setTimerEnabled(timerEnabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_APP_TIMER] = timerEnabled
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun isTimerEnabled(): Boolean {
        try {
            val preferences = dataStore.data.first()
            val isSoundOn = preferences[PreferenceKey.KEY_APP_TIMER] ?: true
            return isSoundOn
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return true
        }
    }

    fun getTimerEnabledFlow(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_APP_TIMER] ?: true
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(true)
            }
    }

    //haptic enabled
    suspend fun setHapticEnabled(isHapticEnabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_APP_HAPTICS] = isHapticEnabled
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun isHapticEnabled(): Boolean {
        try {
            val preferences = dataStore.data.first()
            val isHapticOn = preferences[PreferenceKey.KEY_APP_HAPTICS] ?: true
            return isHapticOn
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return true
        }
    }

    fun getHapticEnabledFlow(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferenceKey.KEY_APP_HAPTICS] ?: true
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(true)
            }
    }

    //haptic enabled
    suspend fun setRuleFirstTime(value: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.KEY_RULE_FIRST_TIME] = value
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun isRuleFirstTime(): Boolean {
        try {
            val preferences = dataStore.data.first()
            val isHapticOn = preferences[PreferenceKey.KEY_RULE_FIRST_TIME] ?: true
            return isHapticOn
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return true
        }
    }

    //add coins
    suspend fun addCoins(coins: Int) {
        try {
            dataStore.edit { preferences ->
                val currentCoins = preferences[PreferenceKey.USER_COINS] ?: Constants.DEFAULT_COINS
                preferences[PreferenceKey.USER_COINS] = currentCoins + coins
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun updateCoins(coins: Int) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKey.USER_COINS] = coins
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    //fetch coins
    fun fetchCoins(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKey.USER_COINS] ?: Constants.DEFAULT_COINS
        }.catch { e ->
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            emit(0)
        }
    }


    //convert coins
    suspend fun useCoins(coins: Int) {
        try {
            dataStore.edit { preferences ->
                val currentCoins = preferences[PreferenceKey.USER_COINS] ?: Constants.DEFAULT_COINS
                preferences[PreferenceKey.USER_COINS] = currentCoins - coins
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }


    suspend fun saveGuestUID(uid: String?) {
        try {
            dataStore.edit { preferences ->
                if (uid != null) {
                    preferences[PreferenceKey.KEY_GUEST_UID_PROFILE] = uid
                } else {
                    preferences.remove(PreferenceKey.KEY_GUEST_UID_PROFILE)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    suspend fun getGuestUID(): String? {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_GUEST_UID_PROFILE]
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return null
        }
    }

    fun getUserSettingsFlow(): Flow<UserSettingsModel> {
        return dataStore.data
            .map { preferences ->
                UserSettingsModel(
                    sound = preferences[PreferenceKey.KEY_APP_SOUNDS] ?: true,
                    haptic = preferences[PreferenceKey.KEY_APP_HAPTICS] ?: true,
                    showCorrect = preferences[PreferenceKey.KEY_SHOW_CORRECT] ?: true,
                    timer = preferences[PreferenceKey.KEY_APP_TIMER] ?: true,
                    username = preferences[PreferenceKey.KEY_USERNAME]
                )
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(
                    UserSettingsModel(
                        sound = true,
                        haptic = true,
                        showCorrect = true,
                        timer = true,
                        username = null
                    )
                )
            }
    }

    suspend fun fetchUnlockLevelCoinsCost(): Int {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_UNLOCK_LEVEL_COINS_COST] ?: 100
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return 100
        }
    }

    suspend fun fetchCorrectAnswerCoins(): Int {
        try {
            val preferences = dataStore.data.first()
            return preferences[PreferenceKey.KEY_CORRECT_ANSWER_COINS] ?: 5
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            return 5
        }
    }

    suspend fun saveGameEconomy(
        gameEconomy: GameEconomy
    ) {
        try {

            dataStore.edit { preferences ->

                preferences[PreferenceKey.KEY_TRY_AGAIN_COINS_COST] =
                    gameEconomy.tryAgainCoins

                preferences[PreferenceKey.KEY_BOMB_COINS_COST] =
                    gameEconomy.bombCoins

                preferences[PreferenceKey.KEY_ADD_TIME_COINS_COST] =
                    gameEconomy.addTimeCoins

                preferences[PreferenceKey.KEY_UNLOCK_LEVEL_COINS_COST] =
                    gameEconomy.unlockLevelCoins

                preferences[PreferenceKey.KEY_CORRECT_ANSWER_COINS] =
                    gameEconomy.correctAnswerCoins
            }

            Result.success(Unit)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "MYDEBUG",
                "Failed to save game economy: ${e.message}",
                e
            )
        }
    }


    fun getGameEconomyFlow(): Flow<GameEconomy> {
        return dataStore.data
            .map { preferences ->
                GameEconomy(
                    tryAgainCoins = preferences[PreferenceKey.KEY_TRY_AGAIN_COINS_COST] ?: 25,
                    bombCoins = preferences[PreferenceKey.KEY_BOMB_COINS_COST] ?: 100,
                    addTimeCoins = preferences[PreferenceKey.KEY_ADD_TIME_COINS_COST] ?: 50,
                    unlockLevelCoins = preferences[PreferenceKey.KEY_UNLOCK_LEVEL_COINS_COST]
                        ?: 100,
                    correctAnswerCoins = preferences[PreferenceKey.KEY_CORRECT_ANSWER_COINS] ?: 5,
                )
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(
                    GameEconomy()
                )
            }
    }

}

object PreferenceKey {
    val KEY_USERNAME = stringPreferencesKey("KEY_USERNAME")
    val KEY_STREAK = intPreferencesKey("KEY_STREAK")
    val KEY_IS_FIRST_TIME = booleanPreferencesKey("KEY_IS_FIRST_TIME")

    val KEY_APP_TIMER = booleanPreferencesKey("KEY_APP_TIMER")
    val KEY_APP_SOUNDS = booleanPreferencesKey("KEY_APP_SOUNDS")
    val KEY_SHOW_CORRECT = booleanPreferencesKey("KEY_SHOW_CORRECT")
    val KEY_APP_HAPTICS = booleanPreferencesKey("KEY_APP_HAPTICS")
    val KEY_RULE_FIRST_TIME = booleanPreferencesKey("KEY_RULE_FIRST_TIME")
    val USER_COINS = intPreferencesKey("USER_COINS")


    val KEY_NOTIFICATION_DIALOG_DISMISSED =
        booleanPreferencesKey("KEY_NOTIFICATION_DIALOG_DISMISSED")


    val KEY_SCORE_RATING_DISMISSED =
        booleanPreferencesKey("KEY_SCORE_RATING_DISMISSED")

    val KEY_CATEGORY_LAST_UPDATED_DATE = stringPreferencesKey("KEY_CATEGORY_LAST_UPDATED_DATE")
    val KEY_LEVEL_LAST_UPDATED_DATE = stringPreferencesKey("KEY_LEVEL_LAST_UPDATED_DATE")
    val KEY_QUESTION_LAST_UPDATED_DATE = stringPreferencesKey("KEY_QUESTION_LAST_UPDATED_DATE")
    val KEY_CHALLENGE_LAST_UPDATED_DATE = stringPreferencesKey("KEY_CHALLENGE_LAST_UPDATED_DATE")

    val KEY_LAST_FEEDBACK_DATE = stringPreferencesKey("KEY_LAST_FEEDBACK_DATE")
    val KEY_FEEDBACKS_COUNT = intPreferencesKey("KEY_FEEDBACKS_COUNT")


    val KEY_USER_PROFILE = stringPreferencesKey("KEY_USER_PROFILE")

    val KEY_GUEST_UID_PROFILE = stringPreferencesKey("KEY_GUEST_UID_PROFILE")

    val KEY_TRY_AGAIN_COINS_COST = intPreferencesKey("KEY_TRY_AGAIN_COINS_COST")
    val KEY_UNLOCK_LEVEL_COINS_COST = intPreferencesKey("KEY_UNLOCK_LEVEL_COINS_COST")
    val KEY_ADD_TIME_COINS_COST = intPreferencesKey("KEY_ADD_TIME_COINS_COST")
    val KEY_CORRECT_ANSWER_COINS = intPreferencesKey("KEY_CORRECT_ANSWER_COINS")
    val KEY_BOMB_COINS_COST = intPreferencesKey("KEY_BOMB_COINS_COST")
}