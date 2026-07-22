package com.techuntried.accountsbasics2.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.commons.ToolbarAction
import com.techuntried.accountsbasics2.ui.dialog.CommonInformationDialog
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun RulesScreenRoot(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    navigateToQuestionsOrLearn: (timerCount: Int?,isLearnType:Boolean) -> Unit,
) {
    val viewModel: RulesViewModel = hiltViewModel()
    val rulesUiState = viewModel.rulesScreenUiState.collectAsStateWithLifecycle().value


    RulesScreen(
        rulesScreenUiState = rulesUiState,
        onBackClick = onBackClick,
        updateTimer = viewModel::updateTimer,
        refresh = { viewModel.refresh() },
        updateRuleIsFirstTime = viewModel::updateIsRuleFirstTime,
        onStart = {
            if (rulesUiState is RulesScreenUiState.Success) {
                navigateToQuestionsOrLearn(rulesUiState.timerCount,rulesUiState.isLearnType)
            }
        }
    )
}


@Composable
private fun RulesScreen(
    rulesScreenUiState: RulesScreenUiState,
    refresh: () -> Unit,
    onBackClick: () -> Unit,
    onStart: () -> Unit,
    updateTimer: (Boolean) -> Unit,
    updateRuleIsFirstTime: () -> Unit,
) {

    var timerStatusDialog by remember { mutableStateOf<Boolean?>(null) }

    val timerCount = (rulesScreenUiState as? RulesScreenUiState.Success)?.timerCount

    val timerToolbarAction = ToolbarAction(
        icon = if (timerCount != null) R.drawable.timer_icon else R.drawable.timer_off_icon,
        onClick = {
            val isTimerEnabled = timerCount != null
            updateTimer(!isTimerEnabled)
            timerStatusDialog = !isTimerEnabled
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        CommonToolbar(
            title = "Rules",
            isNavigationIcon = true,
            onNavigationClick = onBackClick,
            actions = listOf(timerToolbarAction)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (rulesScreenUiState) {
                is RulesScreenUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(rulesScreenUiState.message),
                        description = getErrorMessageDescription(rulesScreenUiState.message),
                        actionButton = "Try Again",
                        action = { refresh() },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                RulesScreenUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is RulesScreenUiState.Success -> {
                    RuleListContent(
                        rulesScreenUiState = rulesScreenUiState,
                        onStart = onStart
                    )
                }
            }
        }
    }

    timerStatusDialog?.let {
        CommonInformationDialog(
            title = if (!it) "Timer Disabled" else "Timer Enabled",
            description = "If you want to update this setting for all games, please update from the profile page.",
            onDismiss = { timerStatusDialog = null })
    }

    if (rulesScreenUiState.isRuleFirstTime()) {
        CommonInformationDialog(
            title = "Timer Settings",
            description = "You can enable or disable the timer anytime using the timer icon in the top-right corner.",
            onDismiss = updateRuleIsFirstTime
        )
    }
}

