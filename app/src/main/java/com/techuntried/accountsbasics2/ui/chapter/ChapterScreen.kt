package com.techuntried.accountsbasics2.ui.chapter

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.model.level.LevelState
import com.techuntried.accountsbasics2.ui.commons.CoinsSheet
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.dialog.CommonInformationDialog
import com.techuntried.accountsbasics2.ui.dialog.LevelLockedDialog
import com.techuntried.accountsbasics2.ui.dialog.LevelUnLockedDialog
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.navigation.RuleArgs
import com.techuntried.accountsbasics2.ui.sheets.SuggestionSheet
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.CompletedLevelColor
import com.techuntried.accountsbasics2.ui.theme.CurrentLevelColor
import com.techuntried.accountsbasics2.ui.theme.LockedLevelColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.Spacer
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun ChaptersScreenRoot(
    modifier: Modifier = Modifier,
    args: LevelArgs,
    navigateToRules: (RuleArgs) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ChapterViewModel = hiltViewModel()
    val chaptersUiState = viewModel.chaptersUiState.collectAsStateWithLifecycle().value

    val coinsState =
        viewModel.coinsState.collectAsStateWithLifecycle().value

    val globalConfigState = viewModel.globalConfigState.collectAsStateWithLifecycle().value
    val bannerAdUnit = remember(globalConfigState) {
        globalConfigState.testOrRealBannerAdUnit()
    }

    val rewardedAdUnit = remember(globalConfigState) {
        globalConfigState.testOrRealRewardedAdUnit()
    }

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("GameLevels"))
    }

    LaunchedEffect(chaptersUiState.message) {
        chaptersUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    ChaptersScreen(
        chapterUiState = chaptersUiState,
        showTopics = args.showTopic,
        coins = coinsState,
        bannerAdUnit = bannerAdUnit,
        rewardedAdUnit = rewardedAdUnit,
        openRules = { levelId ->
            val ruleArgs = RuleArgs(
                categoryId = args.categoryId,
                levelId = levelId
            )
            navigateToRules(ruleArgs)
        },
        categoryName = args.categoryName,
        logEvent = viewModel::logEvent,
        onAction = viewModel::onAction,
        onBack = onBack
    )
}


@Composable
private fun ChaptersScreen(
    chapterUiState: ChapterUiState,
    categoryName: String,
    showTopics: Boolean,
    coins: Int,
    rewardedAdUnit: String?,
    bannerAdUnit: String?,
    onAction: (LevelActions) -> Unit,
    openRules: (levelId: Int) -> Unit = {},
    logEvent:(LogEventType)->Unit,
    onBack: () -> Unit
) {
    var showLevelLockedDialog by remember { mutableStateOf(false) }
    var levelLockedSheet by remember { mutableStateOf<Int?>(null) }
    var suggestionSheet by remember { mutableStateOf<Boolean>(false) }


    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
        CoinsSheet(
            balance = coins,
            onDismiss = { showCoinsSheet = false },
            rewardedAdUnit = rewardedAdUnit,
            onAddCoins = {
                onAction(LevelActions.AddCoin(50))
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
            title = categoryName,
            isBalance = true,
            balance = coins,
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = onBack,
            onBalanceClick = {
                showCoinsSheet = true
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (chapterUiState) {
                is ChapterUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(chapterUiState.errorMessage),
                        description = getErrorMessageDescription(chapterUiState.errorMessage),
                        actionButton = "Try Again",
                        action = { onAction(LevelActions.Refresh) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                ChapterUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }
                is ChapterUiState.Success -> {
                    if (chapterUiState.isEmpty()) {
                        ErrorMessageView(
                            modifier = Modifier.align(Alignment.Center),
                            icon = R.drawable.error_icon_1,
                            errorTitle = "Content unavailable",
                            description = "Something went wrong on our end — not yours.",
                            actionButton = "Report Issue",
                            action = {
                                onAction(LevelActions.UploadSuggestion("Report : $categoryName Levels didn't Load"))
                            }
                        )
                    } else {
                        ChapterContent(
                            chapterUiState = chapterUiState,
                            showTopics = showTopics,
                            bannerAdUnit = bannerAdUnit,
                            openRules = openRules,
                            showSuggestionSheet = { suggestionSheet = true },
                            showLevelLockedDialog = { showLevelLockedDialog = true },
                            logEvent = logEvent,
                            showLevelLockedSheet = { levelLockedSheet = it },
                        )
                    }
                }
            }
            if (chapterUiState.actionLoading) {
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
    levelLockedSheet?.let { levelId ->
        val unlockCoins = (chapterUiState as? ChapterUiState.Success)?.unlockCoinsCost ?: 100
        LevelLockedDialog(
            onDismiss = {
                levelLockedSheet = null
            },
            rewardedAdUnit = rewardedAdUnit,
            logEvent = logEvent,
            unlockCoins = unlockCoins,
            onAdRewardEarned = {
                onAction(LevelActions.UnlockLevel(levelId = levelId, isAdWatched = true))
                levelLockedSheet = null
            },
            useCoins = {
                onAction(LevelActions.UnlockLevel(levelId = levelId, isAdWatched = false))
                levelLockedSheet = null
            }
        )
    }

    (chapterUiState as? ChapterUiState.Success)?.levelUnlocked?.let { levelId ->
        LevelUnLockedDialog(
            onDismiss = {
                onAction(LevelActions.ClearLevelUnlocked)
            },
            play = {
                onAction(LevelActions.ClearLevelUnlocked)
                openRules(levelId)
            }
        )
    }

    if (suggestionSheet) {
        SuggestionSheet(
            onDismiss = {
                suggestionSheet = false
            }, onSubmit = {
                onAction(LevelActions.UploadSuggestion("Level Suggestion: $categoryName - $it"))
                suggestionSheet = false
            }
        )
    }
}


@Composable
fun ChapterCard(
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    index:Int,
    isLast: Boolean = false,
    level: ChapterModel,
    onClick: () -> Unit
) {
    val circleIndicator =
        when (level.levelState) {
            LevelState.Completed -> AppIcons.CompletedLevelIndicator
            LevelState.Locked -> AppIcons.LockedLevelIndicator
            else -> AppIcons.CurrentLevelIndicator
        }
    val circleColor =
        when (level.levelState) {
            LevelState.Completed -> CompletedLevelColor
            LevelState.Locked -> LockedLevelColor
            else -> CurrentLevelColor
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (level.levelState == LevelState.Locked) {
                VerticalDashedLine(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .alpha(if (isFirst) 0f else 1f),
                    color = LockedLevelColor
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .alpha(if (isFirst) 0f else 1f)
                        .background(CompletedLevelColor)
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (level.levelState == LevelState.Completed) Color.White else Color.Transparent)
            ) {
                Icon(
                    painter = painterResource(circleIndicator),
                    contentDescription = null,
                    tint = circleColor,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            if (level.levelState == LevelState.Completed) {
                val bottomLineColor = CompletedLevelColor
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .alpha(if (isLast) 0f else 1f)
                        .background(bottomLineColor)
                )
            } else {
                VerticalDashedLine(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .alpha(if (isLast) 0f else 1f),
                    color = LockedLevelColor
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .debouncedClickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (level.type=="learn") {
                    Text(
                        text = "Chapter ${index.plus(1)}",
                        maxLines = 1,
                        color = SecondaryText,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = level.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MainText,
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        text = level.name,
                        maxLines = 1,
                        color = MainText,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${level.questions} Questions",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                }
            }
            if (level.levelState == LevelState.Locked) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock_icon),
                        contentDescription = null,
                        tint = MainText,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardColor)
                            .padding(8.dp)
                    )
                    Spacer(4.dp)
                    Text(
                        text = "locked",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(40.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardColor)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.book_icon), // your module icon
                contentDescription = null,
                tint = MainText,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MainText
                )

                subtitle?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalDashedLine(
    modifier: Modifier = Modifier,
    color: Color = Color.DarkGray
) {
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(8f, 8f),
        0f
    )

    Canvas(
        modifier = modifier
    ) {
        drawLine(
            color = color,
            strokeWidth = 1.dp.toPx(),
            start = Offset(x = size.width / 2, y = 0f),
            end = Offset(x = size.width / 2, y = size.height),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun ScrollButton(
    modifier: Modifier = Modifier,
    isTargetVisible: Boolean,
    onClick: () -> Unit,
    arrowDirectionDown: Boolean
) {
    AnimatedVisibility(
        visible = !isTargetVisible,
        modifier = modifier
            .padding(20.dp),
        enter = scaleIn(
            initialScale = 0.6f,
            animationSpec = tween(250)
        ) + fadeIn(animationSpec = tween(200)),
        exit = scaleOut(
            targetScale = 0.6f,
            animationSpec = tween(200)
        ) + fadeOut(animationSpec = tween(150))
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(2.dp, BorderColor, CircleShape)
                .background(Color.White)
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (arrowDirectionDown) R.drawable.expand_down_icon
                    else R.drawable.expand_up_icon
                ),
                modifier = Modifier.size(28.dp),
                tint = Color.Black,
                contentDescription = "Scroll to target"
            )
        }
    }
}

