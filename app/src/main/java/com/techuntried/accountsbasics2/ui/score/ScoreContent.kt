package com.techuntried.accountsbasics2.ui.score

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.play.core.review.ReviewManagerFactory
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.NativeAdViewSmall
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonTextInput
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.Spacer
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.findActivity

@Composable
fun ScoreContent(
    modifier: Modifier = Modifier,
    scoreScreenUiState: ScoreScreenUiState.Success,
    isCategory: Boolean,
    scoreTarget: ScoreTarget,
    nativeAdUnit: String?,
    showQuestionReviewSheet: () -> Unit,
    logEvent: (LogEventType) -> Unit,
    onAction: (ScoreActions) -> Unit,
    onBack: () -> Unit,
    playNext: () -> Unit,
    playAgain: () -> Unit,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    var messageId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(scoreScreenUiState.score) {
        scoreScreenUiState.score.let { score ->
            messageId = if (score.isWon) {
                listOf(
                    R.string.win_msg_1,
                    R.string.win_msg_2,
                    R.string.win_msg_3
                ).random()
            } else {
                listOf(
                    R.string.lose_msg_1,
                    R.string.lose_msg_2,
                    R.string.lose_msg_3
                ).random()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            ScoreImageCard(
                isWon = scoreScreenUiState.score.isWon,
                category = scoreScreenUiState.score.title,
                levelName = scoreScreenUiState.score.description
            )
            messageId?.let { msgId ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(msgId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (scoreTarget !is ScoreTarget.Learn) {
                ScoreMetricsCard(
                    correct = scoreScreenUiState.score.correct,
                    total = scoreScreenUiState.score.totalQuestions,
                    accuracy = scoreScreenUiState.score.accuracy,
                    coins = scoreScreenUiState.score.coinsEarned,
                    attempted = scoreScreenUiState.review.size
                )
            }

            nativeAdUnit?.let {
                NativeAdViewSmall(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    adUnit = it,
                    logEvent = logEvent,
                )
            }

            if (scoreScreenUiState.review.isNotEmpty() && isCategory && scoreTarget !is ScoreTarget.Learn) {
                Spacer(modifier = Modifier.height(20.dp))
                QuestionReviewCard { showQuestionReviewSheet() }
            }

            LaunchedEffect(scoreScreenUiState.scoreRatingEnabled) {
                if (scoreScreenUiState.scoreRatingEnabled) {
                    activity?.let {
                        showReviewDialog(activity = it, logEvent = logEvent)
                        onAction(ScoreActions.DismissScoreRating)
                    }
                }
            }
//            if (scoreScreenUiState.scoreRatingEnabled) {
////                RatingSheet(
////                    onDismiss = {
////                        onAction(ScoreActions.DismissScoreRating)
////                    },
////                    submitRatingLessThanFour = {
////                        onAction(ScoreActions.SubmitAppRating("App Rating: $it"))
////                        onAction(ScoreActions.DismissScoreRating)
////                    }
////                )
//            }

            if (scoreScreenUiState.levelRatingEnabled) {
                Spacer(modifier = Modifier.height(20.dp))
                LevelRatingsCardWithFeedback(
                    dismissLevelRating = {
                        onAction(ScoreActions.DismissLevelRating)
                    },
                    submitLevelRating = { ratingText ->
                        onAction(ScoreActions.SubmitLevelRating(ratingText))
                    }
                )
            }

        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)

        ) {
            val buttonTextId = when (scoreScreenUiState.score.isWon) {
                true -> if (scoreScreenUiState.score.isLastChapter) R.string.home else R.string.next_level
                false -> R.string.try_again
            }
            CommonButton(
                modifier = Modifier.weight(1f),
                text = stringResource(buttonTextId),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    when (scoreScreenUiState.score.isWon) {
                        true -> if (scoreScreenUiState.score.isLastChapter) onBack() else playNext()
                        false -> playAgain()
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.share_icon),
                contentDescription = null,
                tint = BackgroundColor,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryColor)
                    .debouncedClickable {
                        onShareClick()
                    }
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun ScoreImageCard(
    modifier: Modifier = Modifier,
    isWon: Boolean,
    category: String,
    levelName: String
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title =
            if (isWon) stringResource(R.string.congratulations) else "Better Luck Next Time!"
        val icon = if (isWon) R.drawable.won else R.drawable.loss
        val level = if (isWon) "$levelName completed" else "$levelName failed"
        Text(
            text = title,
            color = MainText,
            style = MaterialTheme.typography.headlineMedium
        )
        Image(
            painter = painterResource(icon), contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(200.dp)
                .padding(12.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category,
            color = MainText,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(4.dp)
        Text(text = level, color = SecondaryText, style = MaterialTheme.typography.labelLarge)
    }
}


@Composable
fun QuestionReviewCard(
    modifier: Modifier = Modifier,
    onReviewClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Review Answers",
            style = MaterialTheme.typography.headlineSmall,
            color = MainText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check which questions you got right or wrong and see the correct answers.",
            color = SecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Check Answers",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier
                .clip(CircleShape)
                .background(PrimaryColor)
                .debouncedClickable { onReviewClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

    }
}

@Composable
fun ScoreMetricsCard(
    modifier: Modifier = Modifier,
    correct: Int,
    total: Int,
    attempted: Int,
    accuracy: Int,
    coins: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Text(
            text = stringResource(R.string.your_performance),
            color = MainText,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Attempted: $attempted / $total",
            color = SecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row {
            ScoreMetricItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.correct_square_icon,
                title = stringResource(R.string.score_metric_correct),
                value = correct.toString()
            )
            Spacer(modifier = Modifier.width(6.dp))
            ScoreMetricItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.wrong_square_icon,
                title = stringResource(R.string.score_metric_incorrect),
                value = "${attempted - correct}"
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row {
            ScoreMetricItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.target,
                title = stringResource(R.string.score_metric_accuracy),
                value = "$accuracy%"
            )
            Spacer(modifier = Modifier.width(6.dp))
            ScoreMetricItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.coin,
                title = stringResource(R.string.score_metric_coins_earned),
                value = coins.toString()
            )
        }
    }
}

@Composable
fun ScoreMetricItem(modifier: Modifier, icon: Int, title: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon), contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                color = SecondaryText,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            color = MainText,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun LevelRatingsCardWithFeedback(
    dismissLevelRating: () -> Unit,
    submitLevelRating: (ratingText: String) -> Unit
) {
    var selectedRating by remember { mutableStateOf<String?>(null) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How was this level?",
            color = MainText,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                "Bad" to R.drawable.bad_icon,
                "Good" to R.drawable.good_icon,
                "Excellent" to R.drawable.excellent_icon
            ).forEach { (label, icon) ->
                Image(
                    painter = painterResource(icon),
                    contentDescription = label,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            selectedRating = label
                            showFeedbackDialog = true
                        }
                )
            }
        }
    }

    if (showFeedbackDialog && selectedRating != null) {
        var selectedOption by remember { mutableStateOf<String?>(null) }
        var freeText by remember { mutableStateOf("") }
        val options = listOf("Fun", "Easy", "Challenging", "Rewarding")

        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Tell us more about your rating",
                    color = MainText,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    Text(
                        "What did you like most?",
                        color = MainText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOption = option }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedOption == option,
                                onClick = { selectedOption = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryColor
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                option,
                                color = SecondaryText,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    CommonTextInput(
                        value = freeText,
                        onValueChange = { freeText = it },
                        placeHolder = "Additional feedback (optional)"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedOption == null && freeText.isBlank()) {
                            dismissLevelRating()
                        } else {
                            val rating = buildString {
                                append("LevelRating: $selectedRating ($selectedOption)\n")
                                if (freeText.isNotBlank()) {
                                    append("Additional Feedback: $freeText\n")
                                }
                            }

                            submitLevelRating(rating)

                        }
                        showFeedbackDialog = false
                    },
                    modifier = Modifier
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Submit",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        )
    }
}


private fun showReviewDialog(activity: Activity, logEvent: (LogEventType) -> Unit) {
    try {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val reviewInfo = task.result

                manager.launchReviewFlow(activity, reviewInfo)
                    .addOnCompleteListener {
                        Log.d("MYDEBUG", "flow launched")
                        logEvent(LogEventType.ReviewFlowLaunched)
                    }

            } else {
                logEvent(
                    LogEventType.FeatureError(
                        featureName = "Review",
                        errorMessage = task.exception?.message ?: ""
                    )
                )
            }
        }

    } catch (e: Exception) {
        logEvent(
            LogEventType.FeatureError(
                featureName = "Review",
                errorMessage = e.message ?: ""
            )
        )
    }
}