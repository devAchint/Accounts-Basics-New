package com.techuntried.accountsbasics2.ui.game

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.NativeAdManager
import com.techuntried.accountsbasics2.domain.model.questions.GameOption
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.dialog.ResumeGameDialog
import com.techuntried.accountsbasics2.ui.navigation.GameArgs
import com.techuntried.accountsbasics2.ui.navigation.ScoreArgs
import com.techuntried.accountsbasics2.ui.sheets.ConfirmBackSheet
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.bgColor
import com.techuntried.accountsbasics2.utils.borderColor
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle
import com.techuntried.accountsbasics2.utils.textColor

@Composable
fun GameScreenRoot(
    modifier: Modifier = Modifier,
    args: GameArgs,
    onBack: () -> Unit = {},
    navigateToScore: (
        scoreArgs: ScoreArgs
    ) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current
    val viewModel: GameViewModel = hiltViewModel()
    val gameUiState = viewModel.gameUiState.collectAsStateWithLifecycle().value
    val coinsState = viewModel.coinsState.collectAsStateWithLifecycle().value
    val globalConfigState = viewModel.globalConfigState.collectAsStateWithLifecycle().value
    val gameEconomyState = viewModel.gameEconomy.collectAsStateWithLifecycle().value

    var resumeDialog by remember { mutableStateOf(false) }

    val nativeAdUnit = remember(globalConfigState) {
        globalConfigState.testOrRealGameNativeAdUnit()
    }

    val rewardedAdUnit = remember(globalConfigState) {
        globalConfigState.testOrRealRewardedAdUnit()
    }

    LaunchedEffect(Unit) {
        NativeAdManager.resetDialogShown()
        nativeAdUnit?.let {
            NativeAdManager.loadAd(nativeAdUnit, logEvent = viewModel::logEvent)
        }
    }

    val activeGameState = gameUiState as? GameUiState.ActiveGame
    val isGameCompleted = activeGameState?.isGameCompleted
    val isHapticEnabled = activeGameState?.isHapticEnabled

    val triggerHaptic = remember(isHapticEnabled) {
        {
            if (isHapticEnabled == true) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    LaunchedEffect(gameUiState.message) {
        gameUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }


    LaunchedEffect(isGameCompleted) {
        isGameCompleted?.let { gameCompleted ->
            if (gameCompleted) {
                navigateToScore(
                    ScoreArgs(
                        categoryId = args.categoryId,
                        levelId = args.levelId,
                        correctAnswers = gameUiState.correctAnswers,
                        totalQuestions = gameUiState.totalQuestions,
                        questionReview = viewModel.questionReviewList,
                    )
                )
            }
        }
    }

    GameScreen(
        gameUiState = gameUiState,
        onAction = viewModel::onAction,
        coins = coinsState,
        tryAgainCost = gameEconomyState.tryAgainCoins,
        splitCost = gameEconomyState.bombCoins,
        addTimeCost = gameEconomyState.addTimeCoins,
        nativeAdUnit = nativeAdUnit,
        rewardedAdUnit = rewardedAdUnit,
        reportQuestion = { reason, details ->
            viewModel.uploadReport(
                reason = reason,
                details = details
            )
        },
        refresh = {
            viewModel.refresh()
        },
        onBack = onBack,
        logEvent = viewModel::logEvent,
        timerCount = args.timerCount,
        endGame = {
            val activeGameState = gameUiState as? GameUiState.ActiveGame
            activeGameState?.let {
                navigateToScore(
                    ScoreArgs(
                        categoryId = args.categoryId,
                        levelId = args.levelId,
                        correctAnswers = gameUiState.correctAnswers,
                        totalQuestions = gameUiState.totalQuestions,
                        questionReview = viewModel.questionReviewList,
                    )
                )
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (viewModel.pause != null) {
                        resumeDialog = true
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pauseGame()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (resumeDialog) {
        ResumeGameDialog(
            onStartOver = {
                triggerHaptic()
                viewModel.onAction(GameEvent.StartOver)
                resumeDialog = false
            },
            onResume = {
                triggerHaptic()
                viewModel.onAction(GameEvent.ResumeGame)
                resumeDialog = false
            }
        )
    }

}


@Composable
private fun GameScreen(
    modifier: Modifier = Modifier,
    gameUiState: GameUiState,
    coins: Int,
    tryAgainCost:Int,
    splitCost:Int,
    addTimeCost:Int,
    nativeAdUnit: String?,
    rewardedAdUnit: String?,
    logEvent: (LogEventType) -> Unit,
    timerCount: Int?,
    onAction: (event: GameEvent) -> Unit,
    endGame: () -> Unit,
    onBack: () -> Unit = {},
    refresh: () -> Unit,
    reportQuestion: (reason: String, details: String?) -> Unit,
) {
    var confirmBack by remember { mutableStateOf(false) }

    BackHandler {
        if (gameUiState is GameUiState.ActiveGame) {
            confirmBack = !confirmBack
        } else {
            onBack()
        }
    }

    if (confirmBack) {
        ConfirmBackSheet(
            onDismiss = { confirmBack = false }) {
            endGame()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    )
    {
        when (gameUiState) {

            is GameUiState.Error -> {
                ErrorMessageView(
                    icon = R.drawable.error_icon_1,
                    errorTitle = getErrorMessageTitle(gameUiState.errorMessage),
                    description = getErrorMessageDescription(gameUiState.errorMessage),
                    actionButton = "Try Again",
                    action = refresh,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is GameUiState.Loading -> {
                CommonCircularProgress(modifier = Modifier.align(Alignment.Center))
            }

            is GameUiState.PreGameCountdown -> {
                CountdownTimer(
                    value = gameUiState.countdown,
                    modifier = Modifier.align(Alignment.Center),
                    isSoundEnabled = gameUiState.isSoundEnabled
                )
            }

            is GameUiState.ActiveGame -> {
                ActiveGameContent(
                    modifier = Modifier.fillMaxSize(),
                    gameUiState = gameUiState,
                    nativeAdUnit = nativeAdUnit,
                    rewardedAdUnit = rewardedAdUnit,
                    logEvent = logEvent,
                    coins = coins,
                    tryAgainCost = tryAgainCost,
                    splitCost = splitCost,
                    addTimeCost = addTimeCost,
                    onAction = onAction,
                    timerCount = timerCount,
                    reportQuestion = reportQuestion,
                    confirmBack = confirmBack,
                    updateConfirmBack = { confirmBack = it }
                )
            }
        }
    }
}

@Composable
fun PowerUpsRow(
    startOver: () -> Unit = {},
    split: () -> Unit = {},
    addTime: () -> Unit = {},
    share: () -> Unit = {},
    splitEnabled: Boolean = true,
    addTimeEnabled: Boolean = true,
    addTimeCost:Int,
    splitCost:Int,

) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp)) // 👈 important
            .background(PrimaryColor)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(top = 12.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PowerUp3DButton(icon = R.drawable.refresh_icon) { startOver() }
        PowerUp3DButton(icon = R.drawable.group_icon) { share() }
        PowerUp3DButton(
            icon = R.drawable.split_icon,
            enabled = splitEnabled,
            coins = splitCost
        ) {
            if (splitEnabled) {
                split()
            } else {

            }
        }
        PowerUp3DButton(
            icon = R.drawable.time_icon_filled,
            enabled = addTimeEnabled,
            coins = addTimeCost
        ) {
            if (addTimeEnabled) {
                addTime()
            } else {

            }
        }
    }
}

@Composable
fun PowerUp3DButton(
    modifier: Modifier = Modifier,
    icon: Int,
    enabled: Boolean = true,
    coins: Int? = null,
    onClick: () -> Unit
) {
    val baseColor = Color(0xFF2A3399) // Much darker blue
    val highlightColor = Color(0xFF3642B8) // Darker medium blue
    val glowColor = Color(0xFFD6CFFF) // Light purple-blue glow

    val bgBase = if (enabled) baseColor else Color(0xFF545468)
    val bgHighlight = if (enabled) highlightColor else Color(0xFF454558)
    val iconTint = if (enabled) Color(0xFFF5F2ED) else Color(0xFF9B97B8)

    Box(
        modifier = modifier
            .size(60.dp),
        contentAlignment = Alignment.Center
    ) {

        // 🔻 Bottom shadow layer (depth)
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = if (enabled) baseColor.copy(alpha = 0.5f) else Color(0xFF35354A),
                    shape = RoundedCornerShape(20.dp)
                )
                .debouncedClickable {
                    onClick()
                }
        )

        // 🟪 Main 3D body
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(bgHighlight, bgBase)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            // ✨ Glow ring
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (enabled) glowColor.copy(alpha = 0.2f) else Color(0xFF8C8CA0).copy(
                            alpha = 0.35f
                        ),
                        shape = CircleShape
                    )
            )

            // 🚀 Icon (floating above surface)
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(26.dp)

            )
        }
        coins?.let {
            CoinTag(
                coins = it,
                enabled = enabled,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
            )
        }
    }
}


@Composable
fun CoinTag(
    coins: Int,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bgColor = if (enabled)
        Brush.verticalGradient(
            listOf(Color(0xFFFFD166), Color(0xFFFFB703))
        )
    else
        Brush.verticalGradient(
            listOf(Color(0xFFB0ADC9), Color(0xFF8E8AAE))
        )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(13.dp)
        )

        Spacer(Modifier.width(2.dp))

        Text(
            text = coins.toString(),
            color = MainText,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp)
        )
    }
}

@Composable
fun MCQOption(
    modifier: Modifier = Modifier,
    option: GameOption,
    visible: Boolean = true,
    onClick: () -> Unit
) {

    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val offsetX by animateDpAsState(
        targetValue = if (visible) 0.dp else -screenWidth,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "optionSlide"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .offset(x = offsetX)
            .clip(RoundedCornerShape(12.dp))
            .background(option.optionType.bgColor())
            .border(1.5.dp, option.optionType.borderColor(), RoundedCornerShape(12.dp))
            .clickable(option.optionType == OptionType.Unselected) { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val icon = when (option.optionType) {
            OptionType.Wrong -> R.drawable.close_circle_icon
            OptionType.Selected,
            OptionType.Correct -> R.drawable.check_circle_icon

            else -> R.drawable.unselected_icon
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            tint = option.optionType.textColor(),
            modifier = Modifier
                .size(24.dp)
        )
        Text(
            text = option.optionText,
            color = option.optionType.textColor(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun QuestionText(question: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(2.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val fontSize = if (question.length <= 120) 20.sp else 18.sp
        Text(
            text = question,
            color = MainText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize)
        )
    }
}


//@Preview(widthDp = 360, heightDp = 640)
//@Composable
//fun GameScreenPreview(modifier: Modifier = Modifier) {
//    val questions = listOf(
//        QuestionModel(
//            categoryId = 1,
//            levelId = 1,
//            correctOptionId = 1,
//            options = listOf(
//                GameOption(1, "3"),
//                GameOption(1, "3"),
//                GameOption(1, "3"),
//                GameOption(1, "3"),
//            ),
//            questionId = 1,
//            questionText = "How many quarters make 1 dollar?"
//        )
//    )
//    GameScreen(
//        gameUiState = GameUiState(
//            currentQuestion = questions.first()
//        ),
//        endGame = {},
//        onAction = {},
//        reportQuestion = { _, _ -> },
//        refresh = {},
//        isTimerEnabled = true
//    )
//}

@Composable
fun rememberSoundPlayer(): (Context, Int, Boolean) -> Unit {
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    return { context, resId, isSoundEnabled ->
        if (isSoundEnabled) {
            try {
                mediaPlayer.reset()
                val afd = context.resources.openRawResourceFd(resId)
                mediaPlayer.setDataSource(
                    afd.fileDescriptor,
                    afd.startOffset,
                    afd.length
                )
                afd.close()
                mediaPlayer.setVolume(
                    0.4f,
                    0.4f
                )
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
