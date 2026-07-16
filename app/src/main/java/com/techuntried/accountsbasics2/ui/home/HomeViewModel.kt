package com.techuntried.accountsbasics2.ui.home

import android.util.Log
import androidx.compose.ui.text.font.FontVariation.grade
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.BuildConfig
import com.techuntried.accountsbasics2.data.mappers.asCategoryWithProgressModel
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.usecases.GetSubjectsUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import com.techuntried.accountsbasics2.utils.Grade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AppUpdateModel(
    val updateTitle: String,
    val updateBody: String,
)


data class SectionCategoriesModel(
    val title: String,
    val categories: List<SubjectModel>,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSubjectsUseCase: GetSubjectsUseCase,
    private val roomRepository: RoomRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val globalConfigController: GlobalConfigController
) :
    ViewModel() {

    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val homeUiState = _homeUiState.asStateFlow()

    private val _appUpdateState = MutableStateFlow<AppUpdateModel?>(null)
    val appUpdateState = _appUpdateState.asStateFlow()

    val username: StateFlow<String?> = dataStoreRepository.getUserNameFlow()
        .map { storedName ->
            storedName ?: listOf(
                "ScienceExplorer",
                "CuriousMind",
                "BrainVoyager",
                "QuizAdventurer",
                "NeuronSpark"
            ).random()
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val coinsState: StateFlow<Int> = dataStoreRepository.fetchCoins()
        .catch { e ->
            Log.e("MYDEBUG", "Error fetching coins: ${e.message}")
            emit(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val config = globalConfigController.globalConfig


    private var cachedLastPlayed: CategoryWithProgressModel? = null

    val categoriesFlow = flow {
        emit(getSubjectsUseCase(null))
    }.catch { e ->
        emit(ApiResult.Error(e.message ?: "Unknown error"))
    }

    val lastPlayedFlow = roomRepository.observeLatestPlayedCategory()
        .map { it?.asCategoryWithProgressModel() }
        .distinctUntilChanged()


    init {
        observeData()
        fetchAppUpdateInfo()
    }


    private fun observeData() {


        combine(
            categoriesFlow,   // already uses grade internally
            lastPlayedFlow
        ) { result, lastPlayed ->

            when (result) {
                is ApiResult.Error -> HomeUiState.Error(result.errorMessage)

                is ApiResult.Success -> {
                    HomeUiState.Success(
                        userGrade = null,
                        sectionCategories = buildSections(result.data),
                        lastPlayedCategory = lastPlayed
                    )
                }
            }

        }.onStart {
            emit(HomeUiState.Loading)
        }.onEach { state ->
                _homeUiState.value = state
            }
            .launchIn(viewModelScope)
    }

    private fun fetchAppUpdateInfo() {
        viewModelScope.launch {
            try {
                when (val response = networkRepository.fetchAppUpdateInfo()) {
                    is ApiResult.Error -> {
                        Log.d("MYDEBUG", "App Update failed ${response.errorMessage}")
                    }

                    is ApiResult.Success -> {
                        if (response.data.status) {
                            val updateInfo = response.data.updateInfo
                            if (BuildConfig.VERSION_CODE < updateInfo.versionCode) {
                                _appUpdateState.update {
                                    AppUpdateModel(
                                        updateTitle = updateInfo.updateTitle ?: "Update Available",
                                        updateBody = updateInfo.updateBody
                                            ?: "New improvements are ready"
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
            }
        }
    }

    fun dismissAppUpdateCard() {
        _appUpdateState.value = null
    }


    fun buildSections(categories: List<SubjectModel>): List<SectionCategoriesModel> {
        return categories
            .asSequence()
            //  .filter { it.active } change before publishing
            .groupBy { it.section ?: "Others" }
            .toList()
            .sortedByDescending { (_, items) -> items.sumOf { it.sectionWeight } }
            .map { (title, categories) ->
                SectionCategoriesModel(
                    title = title,
                    categories = categories.sortedByDescending { it.weight }
                )
            }
    }

    fun uploadSuggestion(comment: String) {
        viewModelScope.launch {
            _homeUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _homeUiState.update {
                        it.withActionLoading(false)
                            .withMessage(result.errorMessage)
                    }
                }

                is ApiResult.Success -> {
                    _homeUiState.update {
                        it.withActionLoading(false)
                            .withMessage(result.data)
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

    fun addCoins(rewardAmount: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.addCoins(rewardAmount)
                _homeUiState.update { it.withMessage("Earned $rewardAmount coins") }
            } catch (e: Exception) {
                _homeUiState.update { it.withMessage(e.message.toString()) }
            }
        }
    }

    fun clearMsg() {
        _homeUiState.update { it.withMessage(newMessage = null) }
    }
}