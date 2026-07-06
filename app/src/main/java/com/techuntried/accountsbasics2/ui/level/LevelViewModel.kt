package com.techuntried.accountsbasics2.ui.level


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.level.LevelState
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.usecases.GetLevelsUseCase
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
class LevelViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase,
    private val globalConfigController: GlobalConfigController
) :
    ViewModel() {

    private val _levelUiState = MutableStateFlow<LevelUiState>(LevelUiState.Loading)
    val levelUiState = _levelUiState.asStateFlow()

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


    val args = savedStateHandle.toRoute<Routes.LevelScreenRoute>()

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchLevels(args.categoryId)
    }

    fun refreshData() {
        fetchData()
    }


    private fun fetchLevels(categoryId: Int) {
        viewModelScope.launch {
            _levelUiState.value = LevelUiState.Loading
            try {
                when (val response = getLevelsUseCase(categoryId)) {
                    is ApiResult.Error -> {
                        Log.d("MYDEBUG", response.errorMessage)
                        _levelUiState.value = LevelUiState.Error(
                            errorMessage = response.errorMessage
                        )
                    }

                    is ApiResult.Success -> {
                        val levelsCompleted =
                            roomRepository.getLevelsCompleted(categoryId)
                        val unlockLevelCoins = dataStoreRepository.fetchUnlockLevelCoinsCost()
                        val levels = response.data.mapIndexed { index, level ->
                            level.copy(
                                levelState = getLevelState(level.levelId, levelsCompleted),
                                isLast = index == response.data.size - 1
                            )
                        }
                        _levelUiState.value = LevelUiState.Success(
                            gameLevels = levels,
                            levelsCompleted = levelsCompleted,
                            unlockCoinsCost = unlockLevelCoins
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                _levelUiState.value = LevelUiState.Error(
                    errorMessage = e.message
                )
            }

        }
    }

    private fun getLevelState(levelId: Int, completedLevels: Int): LevelState {
        val c = 0..completedLevels
        return if (c.contains(levelId)) {
            LevelState.Completed
        } else if (levelId == completedLevels + 1) {
            LevelState.Unlocked
        } else {
            LevelState.Locked //change to Locked before publishing
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
                _levelUiState.update {
                    it.withActionLoading(false).updateSuccess { copy(levelUnlocked = levelId) }
                }
            } else {
                val coins = coinsState.value
                val unlockCost = dataStoreRepository.fetchUnlockLevelCoinsCost()
                if (coins >= unlockCost) {
                    dataStoreRepository.useCoins(unlockCost)
                    _levelUiState.update { it.updateSuccess { copy(levelUnlocked = levelId) } }
                } else {
                    _levelUiState.update { it.withMessage("Insufficient Coins") }
                }
            }
        }
    }

    private fun clearIsLevelUnlocked() {
        _levelUiState.update { it.updateSuccess { copy(levelUnlocked = null) } }
    }

    fun clearMsg() {
        _levelUiState.update { it.withMessage(null) }
    }

    private fun uploadSuggestion(comment: String) {
        viewModelScope.launch {
            _levelUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _levelUiState.update {
                        it.withActionLoading(false).withMessage(result.errorMessage)
                    }
                }

                is ApiResult.Success -> {
                    _levelUiState.update {
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
                _levelUiState.update { it.withMessage("Earned $rewardAmount coins") }
            } catch (e: Exception) {
                _levelUiState.update { it.withMessage(e.message) }
            }
        }
    }
}