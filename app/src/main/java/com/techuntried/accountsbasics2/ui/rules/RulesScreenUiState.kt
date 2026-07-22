package com.techuntried.accountsbasics2.ui.rules

sealed interface RulesScreenUiState {
    object Loading : RulesScreenUiState
    data class Error(val message: String?) : RulesScreenUiState
    data class Success(
        val rules: List<RuleModel> = emptyList(),
        val title: String? = null,
        val bgColor: String? = null,
        val iconUrl: String? = null,
        val timerCount: Int? = null,
        val isRuleFirstTime: Boolean? = null,
        val isLearnType:Boolean,
        val chapterId:Int
    ) : RulesScreenUiState
}

fun RulesScreenUiState.isRuleFirstTime(): Boolean {
    return when (this) {
        is RulesScreenUiState.Success -> this.isRuleFirstTime ?: false
        else -> false
    }
}

inline fun RulesScreenUiState.updateSuccess(block: RulesScreenUiState.Success.() -> RulesScreenUiState): RulesScreenUiState {
    return if (this is RulesScreenUiState.Success) this.block() else this
}