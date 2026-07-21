package com.techuntried.accountsbasics2.ui.chapter


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.level.ChapterState
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.usecases.GetChaptersUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val getChaptersUseCase: GetChaptersUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase,
    private val globalConfigController: GlobalConfigController
) :
    ViewModel() {

    private val _chapterUiState = MutableStateFlow<ChapterUiState>(ChapterUiState.Loading)
    val chaptersUiState = _chapterUiState.asStateFlow()

    val coinsState: StateFlow<Int> = dataStoreRepository.fetchCoins()
        .catch { e ->
            Log.e("MYDEBUG", "Error fetching coins: ${e.message}")
            emit(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Stops collecting 5s after UI leaves
            initialValue = 0
        )
    val globalConfigState = globalConfigController.globalConfig


    val args = savedStateHandle.toRoute<Routes.ChaptersScreenRoute>()

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchChapters(args.categoryId)
    }

    fun refreshData() {
        fetchData()
    }


    private fun fetchChapters(categoryId: Int) {
        viewModelScope.launch {
            _chapterUiState.value = ChapterUiState.Loading
            try {
                when (val response = getChaptersUseCase(categoryId)) {
                    is ApiResult.Error -> {
                        Log.d("MYDEBUG", response.errorMessage)
                        _chapterUiState.value = ChapterUiState.Error(
                            errorMessage = response.errorMessage
                        )
                    }

                    is ApiResult.Success -> {
                        val chaptersCompleted =
                            roomRepository.getLevelsCompleted(categoryId)
                        val unlockChapterCoins = dataStoreRepository.fetchUnlockLevelCoinsCost()
                        val chapters = response.data.mapIndexed { index, chapter ->
                            chapter.copy(
                                chapterState = getChapterState(chapter.chapterId, chaptersCompleted),
                                isLast = index == response.data.size - 1
                            )
                        }
                        _chapterUiState.value = ChapterUiState.Success(
                            gameLevels = chapters,
                            levelsCompleted = chaptersCompleted,
                            unlockCoinsCost = unlockChapterCoins
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                _chapterUiState.value = ChapterUiState.Error(
                    errorMessage = e.message
                )
            }

        }
    }

    private fun getChapterState(levelId: Int, completedLevels: Int): ChapterState {
        val c = 0..completedLevels
        return if (c.contains(levelId)) {
            ChapterState.Completed
        } else if (levelId == completedLevels + 1) {
            ChapterState.Unlocked
        } else {
            ChapterState.Unlocked //change to Locked before publishing
        }
    }

    fun onAction(gameLevelActions: LevelActions) {
        when (gameLevelActions) {
            is LevelActions.AddCoin -> addCoins(gameLevelActions.coins)
            LevelActions.ClearLevelUnlocked -> clearIsLevelUnlocked()
            is LevelActions.UnlockLevel -> unlockLevel(
                gameLevelActions.levelId,
                gameLevelActions.isAdWatched
            )

            is LevelActions.UploadSuggestion -> uploadSuggestion(gameLevelActions.comment)
            LevelActions.Refresh -> refreshData()
        }
    }

    private fun unlockLevel(levelId: Int, isAdWatched: Boolean) {
        viewModelScope.launch {
            if (isAdWatched) {
                _chapterUiState.update {
                    it.withActionLoading(false).updateSuccess { copy(levelUnlocked = levelId) }
                }
            } else {
                val coins = coinsState.value
                val unlockCost = dataStoreRepository.fetchUnlockLevelCoinsCost()
                if (coins >= unlockCost) {
                    dataStoreRepository.useCoins(unlockCost)
                    _chapterUiState.update { it.updateSuccess { copy(levelUnlocked = levelId) } }
                } else {
                    _chapterUiState.update { it.withMessage("Insufficient Coins") }
                }
            }
        }
    }

    private fun clearIsLevelUnlocked() {
        _chapterUiState.update { it.updateSuccess { copy(levelUnlocked = null) } }
    }

    fun clearMsg() {
        _chapterUiState.update { it.withMessage(null) }
    }

    private fun uploadSuggestion(comment: String) {
        viewModelScope.launch {
            _chapterUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _chapterUiState.update {
                        it.withActionLoading(false).withMessage(result.errorMessage)
                    }
                }

                is ApiResult.Success -> {
                    _chapterUiState.update {
                        it.withActionLoading(false)
                            .withMessage("Thanks! Your report has been recorded.")
                    }
                }
            }
        }

    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    private fun addCoins(rewardAmount: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.addCoins(rewardAmount)
                _chapterUiState.update { it.withMessage("Earned $rewardAmount coins") }
            } catch (e: Exception) {
                _chapterUiState.update { it.withMessage(e.message) }
            }
        }
    }
}