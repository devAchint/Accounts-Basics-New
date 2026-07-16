package com.techuntried.accountsbasics2.ui.learn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.dialog.CommonInformationDialog
import com.techuntried.accountsbasics2.ui.sheets.SuggestionSheet
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun LearnScreenRoot(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: LearnViewModel = hiltViewModel()
    val learnUiState = viewModel.learnUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(learnUiState.message) {
        learnUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    LearnScreen(
        learnUiState = learnUiState,
    )
}


@Composable
fun LearnScreen(
    learnUiState: LearnUiState,
) {
    var showLevelLockedDialog by remember { mutableStateOf(false) }
    var levelLockedSheet by remember { mutableStateOf<Int?>(null) }
    var suggestionSheet by remember { mutableStateOf<Boolean>(false) }


    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
//        CoinsSheet(
//            balance = coins,
//            onDismiss = { showCoinsSheet = false },
//            rewardedAdUnit = rewardedAdUnit,
//            onAddCoins = {
//                onAction(LevelActions.AddCoin(50))
//            }
//        )
    }

    val questionsSize = (learnUiState as? LearnUiState.Success )?.content?.size ?: 0

    val pagerState = rememberPagerState {
        questionsSize
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Top Toolbar
        CommonToolbar(
            title = "",
            isBalance = true,
            balance = 5,
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = {  },
            onBalanceClick = {
                showCoinsSheet = true
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (learnUiState) {
                is LearnUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(learnUiState.errorMessage),
                        description = getErrorMessageDescription(learnUiState.errorMessage),
                        actionButton = "Try Again",
                        action = {
//                            onAction(LevelActions.Refresh)
                                 },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                LearnUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }
                is LearnUiState.Success -> {
                    if (learnUiState.content.isEmpty()) {
                        ErrorMessageView(
                            modifier = Modifier.align(Alignment.Center),
                            icon = R.drawable.error_icon_1,
                            errorTitle = "Content unavailable",
                            description = "Something went wrong on our end — not yours.",
                            actionButton = "Report Issue",
                            action = {
//                                onAction(LevelActions.UploadSuggestion("Report : $categoryName Levels didn't Load"))
                            }
                        )
                    } else {
                        HorizontalPager(state =pagerState ) {page->
                            LearnContentPage(learnUiState.content[page])
                        }
                    }
                }
            }
            if (learnUiState.actionLoading) {
                CommonCircularProgress(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }

    if (showLevelLockedDialog) {
        CommonInformationDialog(
            title = stringResource(R.string.level_locked),
            description = stringResource(R.string.locked_description),
            buttonText = stringResource(R.string.ok),
            onDismiss = {
                showLevelLockedDialog = false
            }
        )
    }

    if (suggestionSheet) {
        SuggestionSheet(
            onDismiss = {
                suggestionSheet = false
            }, onSubmit = {
//                onAction(LevelActions.UploadSuggestion("Level Suggestion: $categoryName - $it"))
                suggestionSheet = false
            }
        )
    }
}

