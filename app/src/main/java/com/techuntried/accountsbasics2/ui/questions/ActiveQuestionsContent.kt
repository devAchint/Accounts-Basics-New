package com.techuntried.accountsbasics2.ui.questions

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.GameToolbar
import com.techuntried.accountsbasics2.ui.dialog.CorrectAnswerDialog
import com.techuntried.accountsbasics2.ui.dialog.RestartGameDialog
import com.techuntried.accountsbasics2.ui.dialog.TimeUpDialog
import com.techuntried.accountsbasics2.ui.dialog.WrongAnswerDialog
import com.techuntried.accountsbasics2.ui.sheets.GameControlsSheet
import com.techuntried.accountsbasics2.ui.sheets.ReportQuestionSheet
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.ProgressTrackColor
import com.techuntried.accountsbasics2.ui.theme.TimerCriticalColor
import com.techuntried.accountsbasics2.ui.theme.TimerNormalColor
import com.techuntried.accountsbasics2.ui.theme.TimerTextColor
import com.techuntried.accountsbasics2.ui.theme.TimerWarningColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.captureGameScreenshot
import com.techuntried.accountsbasics2.utils.saveScreenshotToInternalStorage
import com.techuntried.accountsbasics2.utils.shareScreenshotPreferWhatsApp

@Composable
fun ActiveGameContent(
    modifier: Modifier = Modifier,
    logEvent: (LogEventType) -> Unit,
    rewardedAdUnit: String?,
    nativeAdUnit: String?,
    confirmBack: Boolean = false,
    updateConfirmBack: (Boolean) -> Unit,
    coins: Int?,
    tryAgainCost: Int,
    splitCost:Int,
    addTimeCost:Int,
    timerCount: Int?,
    onAction: (event: QuestionEvent) -> Unit,
    reportQuestion: (reason: String, details: String?) -> Unit,
    gameUiState: GameUiState.ActiveGame
) {
    var restartConfirmDialog by remember { mutableStateOf(false) }
    var gameControls by remember { mutableStateOf(false) }
    var reportQuestion by remember { mutableStateOf(false) }
    val view = LocalView.current
    val context = LocalContext.current
    val shareQuestionTextText = stringResource(R.string.share_question_text)
    val errorSavingScreenShot = stringResource(R.string.error_saving_screenshot)
    val errorCapturingScreenShot = stringResource(R.string.error_capture_screenshot)
    val haptic = LocalHapticFeedback.current
    val soundPlayer = rememberSoundPlayer()

    val triggerHaptic = remember(gameUiState.isHapticEnabled) {
        {
            if (gameUiState.isHapticEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    when (gameUiState.answerState) {
        AnswerState.Correct -> {
            CorrectAnswerDialog(
                onNext = {
                    triggerHaptic()
                    onAction(QuestionEvent.NextQuestion)
                },
                adUnit = nativeAdUnit,
                logEvent = logEvent
            )
        }

        AnswerState.Wrong -> {
            WrongAnswerDialog(
                correctAnswer = gameUiState.currentQuestionCorrectAnswer.takeIf { gameUiState.isShowCorrectEnabled },
                coinsAvailable = (coins ?: 0) >= tryAgainCost,
                tryAgainCost = tryAgainCost,
                rewardedAdUnit = rewardedAdUnit,
                logEvent = logEvent,
                onTryAgain = {
                    triggerHaptic()
                    onAction(QuestionEvent.TryAgain(it))
                },
                onNext = {
                    triggerHaptic()
                    onAction(QuestionEvent.NextQuestion)
                }
            )
        }

        AnswerState.TimeUp -> {
            TimeUpDialog(
                coinsAvailable = (coins ?: 0) >= tryAgainCost,
                tryAgainCost = tryAgainCost,
                rewardedAdUnit = rewardedAdUnit,
                logEvent = logEvent,
                onTryAgain = {
                    triggerHaptic()
                    onAction(QuestionEvent.TryAgain(it))
                },
                onNext = {
                    triggerHaptic()
                    onAction(QuestionEvent.NextQuestion)
                }
            )
        }

        else -> {}
    }

    LaunchedEffect(gameUiState.answerState) {
        gameUiState.answerState?.let {
            val sound = when (it) {
                AnswerState.Correct -> R.raw.right
                AnswerState.Wrong -> R.raw.wrong1
                AnswerState.TimeUp -> R.raw.timeup
            }
            soundPlayer.invoke(context, sound, gameUiState.isSoundEnabled)
        }
    }

    LaunchedEffect(gameUiState.hideWrongs) {
        if (gameUiState.hideWrongs != null) {
            soundPlayer.invoke(context, R.raw.slide2, gameUiState.isSoundEnabled)
        }
    }

    Column(
        modifier = modifier
    ) {
        GameToolbar(
            title = "",
            navigationIcon = R.drawable.close_icon,
            balance = coins,
            onNavigationClick = {
                if (!confirmBack) {
                    updateConfirmBack(true)
                }
            },
            onGameControlsClick = {
                gameControls = true
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
        {
            val progress by remember(
                gameUiState.currentQuestionIndex,
                gameUiState.answerState != null
            ) {
                derivedStateOf {
                    val index = gameUiState.currentQuestionIndex + 1
                    (index.toFloat() / gameUiState.totalQuestions).coerceIn(0f, 1f)
                }
            }
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 500) // adjust speed here
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ProgressTrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MainText)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${gameUiState.currentQuestionIndex.plus(1)} of ${gameUiState.totalQuestions}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MainText
                )
                QuizTimer(timeLeft = gameUiState.questionTimer ?: 0, totalTime = timerCount ?: 15)
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = gameUiState.currentQuestion,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(initialScale = 0.9f)) togetherWith
                            (fadeOut(tween(150)) + scaleOut(targetScale = 1.05f))
                },
                contentKey = { it.questionId },
                label = "QuestionAnimation"
            ) {
                QuestionText(it.questionText)
            }
            AnimatedContent(
                targetState = gameUiState.currentQuestion,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(initialScale = 0.9f)) togetherWith
                            (fadeOut(tween(150)) + scaleOut(targetScale = 1.05f))
                },
                contentKey = { it.questionId },
                label = "OptionsAnimation"
            ) {
                val options = it.options
                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { option ->
                        MCQOption(
                            option = option,
                            visible = gameUiState.hideWrongs == null || !gameUiState.hideWrongs.contains(
                                option
                            ),
                            onClick = {
                                triggerHaptic()
                                onAction(QuestionEvent.CheckAnswer(option.optionId))
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        PowerUpsRow(
            startOver = {
                triggerHaptic()
                restartConfirmDialog = true
            },
            split = {
                triggerHaptic()
                onAction(QuestionEvent.SplitOptions)
            },
            addTime = {
                triggerHaptic()
                onAction(QuestionEvent.AddTime)
            },
            share = {
                triggerHaptic()
                captureGameScreenshot(
                    view,
                    gameBannerEnabled = false
                )?.let { bitmap ->
                    saveScreenshotToInternalStorage(bitmap, context)?.let { file ->
                        shareScreenshotPreferWhatsApp(
                            file,
                            context,
                            shareQuestionTextText
                        )
                    } ?: run {
                        Toast.makeText(
                            context,
                            errorSavingScreenShot,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } ?: run {
                    Toast.makeText(
                        context,
                        errorCapturingScreenShot,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            splitEnabled = gameUiState.hideWrongs == null,
            addTimeEnabled = timerCount != null,
            splitCost = splitCost,
            addTimeCost = addTimeCost
        )
        Spacer(modifier = Modifier.height(12.dp))
    }


    if (restartConfirmDialog) {
        RestartGameDialog(
            onCancel = {
                triggerHaptic()
                restartConfirmDialog = false
            },
            onConfirmRestart = {
                triggerHaptic()
                onAction(QuestionEvent.StartOver)
                restartConfirmDialog = false
            }
        )
    }
    if (gameControls) {
        GameControlsSheet(
            soundEnabled = gameUiState.isSoundEnabled,
            hapticEnabled = gameUiState.isHapticEnabled,
            showCorrectEnabled = gameUiState.isShowCorrectEnabled,
            onShowCorrectToggle = {
                onAction(QuestionEvent.ToggleShowCorrect(it))
            },
            onSoundToggle = {
                onAction(QuestionEvent.ToggleSound(it))
            },
            onHapticToggle = {
                onAction(QuestionEvent.ToggleHaptic(it))
            },
            onReportClick = {
                reportQuestion = true
                onAction(QuestionEvent.Pause)
                gameControls = false
            },
            onClose = {
                gameControls = false
            }
        )
    }
    if (reportQuestion) {
        ReportQuestionSheet(
            onDismiss = {
                onAction(QuestionEvent.Resume)
                reportQuestion = false
            },
            onSubmit = { reason, details ->
                reportQuestion(reason, details)
                onAction(QuestionEvent.Resume)
                reportQuestion = false
            }
        )
    }
}

@Composable
fun QuizTimer(
    timeLeft: Int,          // seconds left
    totalTime: Int,         // total seconds
    modifier: Modifier = Modifier
) {
    val progress = timeLeft / totalTime.toFloat()

    // 🎨 Color based on urgency
    val ringColor = when {
        timeLeft <= 3 -> TimerCriticalColor // red
        timeLeft <= 5 -> TimerWarningColor // amber
        else -> TimerNormalColor
    }

    // ⚡ Pulse animation when low time
    val scale by animateFloatAsState(
        targetValue = if (timeLeft <= 5) 1.1f else 1f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "timer-scale"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(CardColor),
        contentAlignment = Alignment.Center
    ) {

        // 🔄 Progress Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = ringColor.copy(alpha = 0.85f),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // ⏱️ Time Text
        Text(
            text = timeLeft.toString(),
            color = TimerTextColor,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
