package com.techuntried.accountsbasics2.ui.sectionCategories

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.BannerAdCard
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.home.HomeCategoryItemCard
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun SectionCategoriesScreenRoot(
    modifier: Modifier = Modifier,
    section: String,
    onBack: () -> Unit,
    openGameLevel: (LevelArgs) -> Unit
) {
    val context = LocalContext.current
    val viewModel: SectionCategoriesViewModel = hiltViewModel()
    val quizSectionUiState = viewModel.sectionCategoriesUiState.collectAsStateWithLifecycle().value

    val globalConfigState = viewModel.globalConfigState.collectAsStateWithLifecycle().value
    val bannerAdUnit = remember(globalConfigState) {
        globalConfigState.testOrRealBannerAdUnit()
    }

    LaunchedEffect(Unit) {
        viewModel.logEvent(
            LogEventType.ScreenVisit(
                "$section Section"
            )
        )
    }

    LaunchedEffect(quizSectionUiState.message) {
        quizSectionUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.d("MYDEBUG", message)
            viewModel.clearMsg()
        }
    }

    SectionCategoriesScreen(
        section = section,
        sectionCategoriesUiState = quizSectionUiState,
        onBack = onBack,
        bannerAdUnit = bannerAdUnit,
        logEvent = viewModel::logEvent,
        refresh = {
            viewModel.fetchCategories()
        },
        reportIssue = {
            viewModel.uploadSuggestion("Report : $section Section didn't Load")
        },
        onQuizCategoryClick = openGameLevel
    )
}


@Composable
private fun SectionCategoriesScreen(
    sectionCategoriesUiState: SectionCategoriesUiState,
    section: String,
    bannerAdUnit: String?,
    logEvent: (LogEventType) -> Unit,
    onQuizCategoryClick: (LevelArgs) -> Unit,
    reportIssue: () -> Unit,
    onBack: () -> Unit,
    refresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        CommonToolbar(
            title = section,
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = onBack,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (sectionCategoriesUiState) {
                is SectionCategoriesUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(sectionCategoriesUiState.errorMsg),
                        description = getErrorMessageDescription(sectionCategoriesUiState.errorMsg),
                        actionButton = "Try Again",
                        action = refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                SectionCategoriesUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                        color = Color.Black
                    )
                }

                is SectionCategoriesUiState.Success -> {
                    QuizSectionContent(
                        reportIssue = reportIssue,
                        sectionCategoriesUiState = sectionCategoriesUiState,
                        onQuizCategoryClick = onQuizCategoryClick,
                        bannerAdUnit = bannerAdUnit,
                        logEvent = logEvent
                    )
                }
            }
        }
    }
}

@Composable
fun QuizSectionContent(
    sectionCategoriesUiState: SectionCategoriesUiState.Success,
    reportIssue: () -> Unit,
    bannerAdUnit: String?,
    onQuizCategoryClick: (LevelArgs) -> Unit,
    logEvent: (LogEventType) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()){
        if (sectionCategoriesUiState.isEmpty()) {
            ErrorMessageView(
                icon = R.drawable.error_icon_1,
                errorTitle = "Content unavailable",
                description = "Something went wrong on our end — not yours.",
                actionButton = "Report Issue",
                action = reportIssue,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sectionCategoriesUiState.categories) { category ->
                        HomeCategoryItemCard (
                            categoryModel = category,
                            onClick = {
                                onQuizCategoryClick(
                                    LevelArgs(
                                        category.categoryId,
                                        category.categoryName,
                                        category.showTopics
                                    )
                                )
                            }
                        )
                    }
                }
                bannerAdUnit?.let {
                    BannerAdCard(bannerAdUnit = it, logEvent = logEvent)
                }
            }
        }

        if (sectionCategoriesUiState.actionLoading){
            CommonCircularProgress(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                color = Color.Black
            )
        }
    }
}