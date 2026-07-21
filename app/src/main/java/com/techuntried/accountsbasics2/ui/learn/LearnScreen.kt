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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.dynamic.IFragmentWrapper
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle
import kotlinx.coroutines.launch

@Composable
fun LearnScreenRoot(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    onBackClick: () -> Unit
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
        onFinish = onFinish,
        onBackClick = onBackClick
    )
}


@Composable
fun LearnScreen(
    learnUiState: LearnUiState,
    onFinish: () -> Unit,
    onBackClick: () -> Unit
) {
    val learnPagesSize = (learnUiState as? LearnUiState.Success)?.content?.size ?: 0

    val pagerState = rememberPagerState {
        learnPagesSize
    }

    val title =
        (learnUiState as? LearnUiState.Success)?.content?.get(pagerState.currentPage)?.title ?: ""

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Top Toolbar
        CommonToolbar(
            title = title,
            isBalance = false,
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = {
                if (pagerState.currentPage == 0) {
                    onBackClick()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            },
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
                        HorizontalPager(state = pagerState) { currentPage ->
                            LearnContentPage(
                                question = learnUiState.content[currentPage],
                                isLast = learnPagesSize - 1 == currentPage,
                                onFinishClicked = onFinish,
                                onContinueClicked = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(page = currentPage + 1)
                                    }
                                }
                            )
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
}

