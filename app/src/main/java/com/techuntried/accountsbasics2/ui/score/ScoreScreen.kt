package com.techuntried.accountsbasics2.ui.score

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.showInterstitialAd
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import com.techuntried.accountsbasics2.ui.commons.CoinsSheet
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.navigation.RuleArgs
import com.techuntried.accountsbasics2.ui.navigation.ScoreArgs
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.captureScreenshot
import com.techuntried.accountsbasics2.utils.findActivity
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle
import com.techuntried.accountsbasics2.utils.saveScreenshotToInternalStorage
import com.techuntried.accountsbasics2.utils.shareScreenshot

@Composable
fun ScoreScreenRoot(
    modifier: Modifier = Modifier,
    args: ScoreArgs,
    navigateToRule: (ruleArgs: RuleArgs) -> Unit,
    onBackPress: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: ScoreViewModel = hiltViewModel()
    val scoreUiState = viewModel.scoreScreenUiState.collectAsStateWithLifecycle().value
    val coins = viewModel.coinsState.collectAsStateWithLifecycle().value
    val config = viewModel.config.collectAsStateWithLifecycle().value

    val showInterstitial = remember {
        config.shouldShowInterstitial()
    }

    val nativeAdUnit = remember(config, showInterstitial) {
        if (showInterstitial) null else config.testOrRealNativeAdUnit()
    }

    val rewardedAdUnit = remember(config) {
        config.testOrRealRewardedAdUnit()
    }

    LaunchedEffect(scoreUiState.message) {
        scoreUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    LaunchedEffect(Unit) {
        if (showInterstitial) {
            (context.findActivity())?.let { activity ->
                config.testOrRealInterstitialAdUnit()?.let { adUnit ->
                    showInterstitialAd(
                        adUnit = adUnit,
                        activity = activity,
                        logEvent = viewModel::logEvent,
                        onAdShown = {
                            config.recordInterstitialShown()
                        }
                    )
                }
            }
        }else{
            config.recordInterstitialShown()
        }
    }

    ScoreScreen(
        scoreScreenUiState = scoreUiState,
        coins = coins,
        nativeAdUnit = nativeAdUnit,
        rewardedAdUnit = rewardedAdUnit,
        logEvent = viewModel::logEvent,
        isPracticeType = args.isPracticeType,
        onAction = { action ->
            val finalAction =
                if (action is ScoreActions.SubmitLevelRating) {
                    action.copy(
                        ratingText = "Category ${args.subjectId} Level ${args.chapterId}\n${action.ratingText}"
                    )
                } else {
                    action
                }
            viewModel.onAction(finalAction)
        },
        playNextLevel = {
            navigateToRule(
                RuleArgs(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId + 1
                )
            )
        },
        playAgain = {
            navigateToRule(
                RuleArgs(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId
                )
            )
        },
        onBackPress = onBackPress,
    )
}


@Composable
fun ScoreScreen(
    scoreScreenUiState: ScoreScreenUiState,
    nativeAdUnit: String? = null,
    rewardedAdUnit: String? = null,
    isPracticeType:Boolean,
    coins: Int?,
    logEvent: (LogEventType) -> Unit,
    onAction: (ScoreActions) -> Unit = {},
    playNextLevel: () -> Unit = {},
    playAgain: () -> Unit = {},
    onBackPress: () -> Unit = {}
) {

    var questionsReviewSheetVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val shareScoreText = stringResource(R.string.share_score_text)
    val errorSavingScreenShot = stringResource(R.string.error_saving_screenshot)
    val errorCapturingScreenShot = stringResource(R.string.error_capture_screenshot)

    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
        CoinsSheet(
            balance = coins ?: 0,
            onDismiss = { showCoinsSheet = false },
            rewardedAdUnit = rewardedAdUnit,
            onAddCoins = {
                onAction(ScoreActions.AddCoins(50))
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    )
    {
        // Top Toolbar
        CommonToolbar(
            title = "",
            isNavigationIcon = true,
            navigationIcon = R.drawable.close_icon,
            onNavigationClick = onBackPress,
            isBalance = true,
            balance = coins,
            onBalanceClick = {
                coins?.let {
                    showCoinsSheet = true
                }
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (scoreScreenUiState) {
                is ScoreScreenUiState.Error -> {
                    ErrorMessageView(
                        modifier = Modifier.align(Alignment.Center),
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(scoreScreenUiState.message),
                        description = getErrorMessageDescription(scoreScreenUiState.message),
                        actionButton = "Try Again",
                        action = { onAction(ScoreActions.Refresh) }
                    )
                }

                ScoreScreenUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is ScoreScreenUiState.Success -> {
                    ScoreContent(
                        scoreScreenUiState = scoreScreenUiState,
                        isCategory = true,
                        isPracticeType =isPracticeType ,
                        nativeAdUnit = nativeAdUnit,
                        showQuestionReviewSheet = { questionsReviewSheetVisibility = true },
                        onAction = onAction,
                        playAgain = playAgain,
                        playNext = playNextLevel,
                        onBack = onBackPress,
                        logEvent = logEvent,
                        onShareClick = {
                            captureScreenshot(view)?.let { bitmap ->
                                saveScreenshotToInternalStorage(bitmap, context)?.let { file ->
                                    shareScreenshot(
                                        file,
                                        context,
                                        shareScoreText
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
                        }
                    )
                }
            }

            if (scoreScreenUiState.actionLoading) {
                CommonCircularProgress(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }

    if (questionsReviewSheetVisibility && !(scoreScreenUiState as? ScoreScreenUiState.Success)?.review.isNullOrEmpty()) {
        QuestionsReviewBottomSheet(
            review = scoreScreenUiState.review,
            onDismiss = {
                questionsReviewSheetVisibility = false
            }
        )
    }
}



@Composable
fun ChapterRatingsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How was this level?",
            color = MainText,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(R.drawable.bad_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
            )
            Image(
                painter = painterResource(R.drawable.good_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
            )
            Image(
                painter = painterResource(R.drawable.excellent_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsReviewBottomSheet(
    review: List<QuestionReviewModel>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = BackgroundColor
    ) {
        QuestionsReviewContent(review) { onDismiss() }
    }
}

@Composable
fun QuestionsReviewContent(
    review: List<QuestionReviewModel>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Review Answers",
            color = MainText,
            style = MaterialTheme.typography.headlineSmall
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            review.forEachIndexed { index, model ->
                QuestionReviewItem(questionNumber = index + 1, review = model)
                if (review.lastIndex != index) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        CommonButton(
            text = "OK",
            shape = RoundedCornerShape(30.dp),
        ) {
            onDismiss()
        }
    }
}


@Composable
fun QuestionReviewItem(questionNumber: Int, review: QuestionReviewModel) {
    val isCorrect = review.answer == review.correctAnswer
    val correctOptionColor = Color(0xFF22C55E)
    val wrongOptionColor = Color(0xFFEF4444)
    val titleBgColor =
        if (isCorrect) correctOptionColor else wrongOptionColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Q$questionNumber",
                style = MaterialTheme.typography.titleSmall,
                color = MainText
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isCorrect) "Correct" else "Wrong",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(titleBgColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text(
            text = review.question,
            color = MainText,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = buildAnnotatedString {
                append("Your answer: ")
                withStyle(style = SpanStyle(color = titleBgColor)) {
                    append(review.answer)
                }
            },
            color = SecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        if (!isCorrect) {
            Text(
                text = buildAnnotatedString {
                    append("Correct answer: ")
                    withStyle(style = SpanStyle(color = correctOptionColor)) {
                        append(review.correctAnswer)
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

