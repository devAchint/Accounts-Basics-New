package com.techuntried.accountsbasics2.ui.searchData

import androidx.compose.ui.text.input.TextFieldValue
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel

sealed interface SearchDataUiState {
    val message:String?

    data object InitialLoading: SearchDataUiState{
        override val message: String? = null
    }

    data class Error(val errorMessage: String?, override val message: String? = null) : SearchDataUiState
    data object EmptyQuery: SearchDataUiState{
        override val message: String? = null
    }
    data class Success(
        val searchResults: List<SubjectModel>? = null,
        val searchQuery: TextFieldValue = TextFieldValue(""),
        val searchHints: List<String> = emptyList(),
        val hasFocus: Boolean = false,
        val isLoading: Boolean = false,
        override val message: String? = null
    ) : SearchDataUiState {
        fun searchResultsEmpty(): Boolean = searchResults?.isEmpty() == true
    }
}

fun SearchDataUiState.withMessage(newMessage: String?): SearchDataUiState {
    return when (this) {
        is SearchDataUiState.Success -> this.copy(message = newMessage)
        is SearchDataUiState.Error -> this.copy(message = newMessage)
        is SearchDataUiState.InitialLoading -> this
        is SearchDataUiState.EmptyQuery -> this
    }
}

inline fun SearchDataUiState.updateSuccess(
    block: SearchDataUiState.Success.() -> SearchDataUiState
): SearchDataUiState {
    return if (this is SearchDataUiState.Success) this.block() else this
}