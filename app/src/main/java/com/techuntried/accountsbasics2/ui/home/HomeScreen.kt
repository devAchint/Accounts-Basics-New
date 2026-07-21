package com.techuntried.accountsbasics2.ui.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.AppUpdateCard
import com.techuntried.accountsbasics2.ui.commons.CoinsSheet
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.sheets.SuggestCategorySheet
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.findActivity
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle
import kotlinx.coroutines.delay

@Composable
fun HomeScreenRoot(
    modifier: Modifier = Modifier,
    openLevels: (LevelArgs) -> Unit,
    onMoreCategoriesClick: (section: String, grade: Int?) -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val homeUiState = viewModel.homeUiState.collectAsStateWithLifecycle().value
    val appUpdateState = viewModel.appUpdateState.collectAsStateWithLifecycle().value
    val username = viewModel.username.collectAsStateWithLifecycle().value
    val coins = viewModel.coinsState.collectAsStateWithLifecycle().value
    val config =
        viewModel.config.collectAsStateWithLifecycle().value

    val rewardedAdUnit = remember(config) {
        config.testOrRealRewardedAdUnit()
    }

    var backPressedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = backPressedOnce) {
        if (backPressedOnce) {
            delay(2000L)
            backPressedOnce = false
        }
    }
    val exitAppString = stringResource(R.string.exit_app)
    BackHandler {
        if (backPressedOnce) {
            context.findActivity()?.finish()
        } else {
            Toast.makeText(
                context,
                exitAppString, Toast.LENGTH_SHORT
            ).show()
            backPressedOnce = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("Home"))
    }

    LaunchedEffect(homeUiState.message) {
        homeUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }
    HomeScreen(
        homeUiState = homeUiState,
        appUpdateModel = appUpdateState,
        rewardedAdUnit= rewardedAdUnit,
        username = username,
        coins = coins,
        onQuizCategoryClick = { categoryId, categoryName, showTopic ->
            openLevels(
                LevelArgs(
                    categoryId = categoryId, categoryName = categoryName, showTopic = showTopic
                )
            )
        },
        uploadSuggestion = { suggestion ->
            viewModel.uploadSuggestion(comment = "Suggested Category: $suggestion")
        },
        refresh = {
//            viewModel.createHomeScreen()
        },
        onSectionMoreClick = { section ->
//            val currentGrade = (homeUiState as? HomeUiState.Success)?.userGrade?.gradeNumber
//            onMoreCategoriesClick(section, currentGrade)
        },
        dismissAppUpdateCard = viewModel::dismissAppUpdateCard,
        addCoins = viewModel::addCoins
    )
}


@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    appUpdateModel: AppUpdateModel?,
    rewardedAdUnit: String?,
    username: String?,
    coins: Int,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onSectionMoreClick: (section: String) -> Unit,
    dismissAppUpdateCard: () -> Unit,
    refresh: () -> Unit,
    uploadSuggestion: (String) -> Unit,
    addCoins:(Int)->Unit,
) {
    var suggestCategorySheetVisible by remember { mutableStateOf(false) }
    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
        CoinsSheet(
            balance = coins,
            onDismiss = { showCoinsSheet = false },
            rewardedAdUnit = rewardedAdUnit,
            onAddCoins = {
                addCoins(50)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        HomeToolbar(
            username = username,
            coins = coins,
            showCoinsSheet = { showCoinsSheet = true }
        )
        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (homeUiState) {
                is HomeUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(homeUiState.message),
                        description = getErrorMessageDescription(homeUiState.message),
                        actionButton = "Try Again",
                        action = refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                HomeUiState.Loading -> {
                    ShimmerHomeSkeleton()
                }

                is HomeUiState.Success -> {
                    HomeContent(
                        homeUiState,
                        onQuizCategoryClick = onQuizCategoryClick,
                        onSectionMoreClick = onSectionMoreClick,
                        onSuggestClick = { suggestCategorySheetVisible = true },
                    )

                    if (homeUiState.actionLoading) {
                        CommonCircularProgress(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }

            appUpdateModel?.let {
                AppUpdateCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    title = it.updateTitle,
                    body = it.updateBody,
                    onDismiss = dismissAppUpdateCard
                )
            }
        }
    }

    if (suggestCategorySheetVisible) {
        SuggestCategorySheet(
            onDismiss = {
                suggestCategorySheetVisible = false
            },
            onSubmit = {
                uploadSuggestion(it)
                suggestCategorySheetVisible = false
            }
        )
    }
}




@Composable
fun QuizSectionCard(
    modifier: Modifier = Modifier,
    section: SectionCategoriesModel,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onAllCategoriesClick: () -> Unit
) {
    section.categories.let { categories ->
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MainText
                )
                if (categories.size > 4) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardColor)
                            .debouncedClickable { onAllCategoriesClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.titleSmall,
                            color = MainText
                        )

                        Icon(
                            painter = painterResource(R.drawable.right_arrow_icon),
                            contentDescription = null,
                            tint = MainText,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            val rows = categories.take(4).chunked(2)
            rows.forEachIndexed { index, rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { category ->
                        HomeSubjectItemCard(
                            modifier = Modifier.weight(1f), subjectModel = category, onClick = {
                                onQuizCategoryClick(
                                    category.categoryId, category.categoryName, category.showTopics
                                )
                            })
                    }

                    // Fill empty space if row has 1 item
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (index != rowItems.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

//@Composable
//fun YourGradeCard(modifier: Modifier = Modifier, grade: Grade, onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "Your Grade",
//            maxLines = 1,
//            style = MaterialTheme.typography.titleMedium,
//            overflow = TextOverflow.Ellipsis,
//            color = MainText
//        )
//
//        ActionText(action = grade.gradeName, onClick = onClick)
//    }
//}

@Composable
fun ActionText(modifier: Modifier = Modifier, action: String, onClick: () -> Unit) {
    Text(
        text = action,
        maxLines = 1,
        style = MaterialTheme.typography.titleSmall,
        overflow = TextOverflow.Ellipsis,
        color = Color(0xFF212121),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFEDEDED))
            .debouncedClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp))
}


