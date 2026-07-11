package com.techuntried.accountsbasics2.ui.searchData

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.usecases.GetSubjectsUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadFeedbackUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDataViewModel @Inject constructor(
    private val getSubjectsUseCase: GetSubjectsUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val uploadFeedbackUseCase: UploadFeedbackUseCase,
) :
    ViewModel() {

    private val _searchDataUiState =
        MutableStateFlow<SearchDataUiState>(SearchDataUiState.InitialLoading)
    val searchDataUiState = _searchDataUiState.asStateFlow()

    private var allData: List<SubjectModel> = emptyList()
    private var allHints: List<String> = emptyList()
    private val _savedSearchQueries = mutableSetOf<String>()

    private var fetchDeferred: Deferred<Boolean>? = null

    init {
        _searchDataUiState.value = SearchDataUiState.Success(hasFocus = true)
    }

    private suspend fun ensureDataFetched(): Boolean {

        // 1. We securely capture the job in a local immutable variable 'deferred'
        val deferred = fetchDeferred ?: viewModelScope.async {
            try {
                when (val result = getSubjectsUseCase(null)) {
                    is ApiResult.Success -> {
                        //removed before publishing
                        allData = result.data  //.filter { it.active }
                        allHints = allData
                            .flatMap { listOfNotNull(it.section, it.categoryName) }
                            .map { it.lowercase() }
                            .distinct()
                        true // Success
                    }

                    is ApiResult.Error -> {
                        fetchDeferred = null // Reset globally so next try works
                        false
                    }
                }
            } catch (e: Exception) {
                fetchDeferred = null // Reset globally
                false
            }
        }.also {
            // 2. If we had to create a new job, save it to the global variable
            fetchDeferred = it
        }

        return deferred.await()
    }

    fun updateFocus(focus: Boolean) {
        _searchDataUiState.update {
            it.updateSuccess { copy(hasFocus = focus) }
        }
    }

    fun updateQuery(query: TextFieldValue) {
        // 1. Instantly update UI text
        _searchDataUiState.update {
            if (it is SearchDataUiState.Success) {
                it.copy(searchQuery = query)
            } else {
                SearchDataUiState.Success(searchQuery = query, hasFocus = true)
            }
        }

        // 2. Trigger fetch in background. If it's already fetching, this does nothing!
        viewModelScope.launch {
            if (ensureDataFetched()) {
                // Once data is ready, update the hints for what they typed
                updateSearchHints(query.text)
            }
        }
    }

    fun clearSearch() {
        _searchDataUiState.value = SearchDataUiState.Success(hasFocus = true)
    }

    fun onSearch(query: TextFieldValue) {
        viewModelScope.launch {
            updateFocus(false)
            val text = query.text.trim()

            if (text.isBlank()) {
                _searchDataUiState.value = SearchDataUiState.EmptyQuery
                return@launch
            }

            // Show loading spinner just in case they hit Enter before the first keystroke's fetch finished
            _searchDataUiState.update {
                it.updateSuccess { copy(isLoading = true, searchQuery = query) }
            }

            // Wait for data, then search
            if (ensureDataFetched()) {
                performLocalSearch(text)
            }
            //change before publishing

//            uploadSearch(
//                comment = "Search Query: ${query.text}",
//                query.text
//            )
        }
    }

    private fun performLocalSearch(text: String) {
        val filtered = allData.filter { item ->
            val sectionMatch = item.section?.contains(text, ignoreCase = true) ?: false
            val titleMatch = item.categoryName.contains(text, ignoreCase = true)
            sectionMatch || titleMatch
        }
            .sortedWith(
                compareBy<SubjectModel> { it.course }
                    .thenByDescending { it.weight }
            )

        _searchDataUiState.update {
            it.updateSuccess {
                copy(
                    searchResults = filtered,
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchHints(query: String) {
        val trimmed = query.trim().lowercase()

        val filtered = if (trimmed.isBlank()) {
            emptyList()
        } else {
            allHints.filter { it.startsWith(trimmed) }.take(10)
        }

        _searchDataUiState.update {
            it.updateSuccess { copy(searchHints = filtered) }
        }
    }


    private suspend fun uploadSearch(comment: String, searchText: String) {
        if (searchText in _savedSearchQueries) {
            return
        }
        val result = uploadFeedbackUseCase(comment = comment)

        if (result is ApiResult.Success) {
            // update StateFlow
            _savedSearchQueries += searchText
        }

    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }


    fun clearMsg() {
        _searchDataUiState.update { it.withMessage(newMessage = null) }
    }
}
   