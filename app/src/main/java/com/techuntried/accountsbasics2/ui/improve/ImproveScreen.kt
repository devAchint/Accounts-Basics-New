package com.techuntried.accountsbasics2.ui.improve

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CoinsSheet
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun ImproveScreenRoot(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: ImproveViewModel = hiltViewModel()
    val improveUiState = viewModel.improveUiState.collectAsStateWithLifecycle().value

    val coinsState =
        viewModel.coinsState.collectAsStateWithLifecycle().value

    LaunchedEffect(improveUiState.message) {
        improveUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    ImproveScreen(
        improveUiState = improveUiState,
        coins = coinsState,
        rewardedAdUnit = null,
        onAction = viewModel::onAction,
    )
}


@Composable
fun ImproveScreen(
    improveUiState: ImproveUiState,
    coins: Int,
    rewardedAdUnit: String?,
    onAction: (ImproveActions) -> Unit,
) {

    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
        CoinsSheet(
            balance = coins,
            onDismiss = { showCoinsSheet = false },
            rewardedAdUnit = rewardedAdUnit,
            onAddCoins = {
                // onAction(ChapterActions.AddCoin(50))
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Top Toolbar
        CommonToolbar(
            title = "Improve",
            isBalance = true,
            balance = coins,
            onBalanceClick = {
                showCoinsSheet = true
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (improveUiState) {
                is ImproveUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(improveUiState.errorMessage),
                        description = getErrorMessageDescription(improveUiState.errorMessage),
                        actionButton = "Try Again",
                        action = {
                            onAction(ImproveActions.Refresh)
                        },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                ImproveUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is ImproveUiState.Success -> {
                    if (improveUiState.wrongQuestions.isEmpty()) {
                        ErrorMessageView(
                            modifier = Modifier.align(Alignment.Center),
                            icon = R.drawable.error_icon_1,
                            errorTitle = "Content unavailable",
                            description = "Something went wrong on our end — not yours.",
                            actionButton = "Report Issue",
                            action = {
//                                onAction(ImproveActions.UploadSuggestion("Report : $categoryName Levels didn't Load"))
                            }
                        )
                    } else {
                        ImproveContent(
                            improveUiState = improveUiState,
                            bannerAdUnit = null,
                            showSuggestionSheet = {
//                                suggestionSheet = true
                            },
                            logEvent = { onAction(ImproveActions.LogEvent(it)) },
                        )
                    }
                }
            }
            if (improveUiState.actionLoading) {
                CommonCircularProgress(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
}

@Preview
@Composable
fun ImproveScreenPreview() {
    ImproveScreen(improveUiState = ImproveUiState.Loading, coins = 10, null, onAction = {})
}