package com.techuntried.accountsbasics2.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.NativeAdManager
import com.techuntried.accountsbasics2.ads.NativeAdViewLarge
import com.techuntried.accountsbasics2.ads.showRewardedAd
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.TryAgainAdButton
import com.techuntried.accountsbasics2.ui.commons.TryAgainButton
import com.techuntried.accountsbasics2.ui.theme.CancelButtonColor
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.ui.theme.TimeUpColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.findActivity


@Composable
fun CommonInformationDialog(
    onDismiss: () -> Unit,
    title: String,
    description: String,
    buttonText: String = stringResource(R.string.ok)
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
            ) {

                // Title
                Text(
                    text = title,
                    color = MainText,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = description,
                    color = SecondaryText,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // OK button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = buttonText,
                            color = MainText,
                            modifier = Modifier,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun CorrectAnswerDialog(
    modifier: Modifier = Modifier,
    onNext: () -> Unit = {},
    adUnit: String? = null,
    logEvent: (LogEventType) -> Unit = {},
) {
    Dialog(onDismissRequest = {}) {
        Column {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = modifier
            ) {

                // Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(14.dp),
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 40.dp, bottom = 24.dp)
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Correct!",
                            color = MainText,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Great job! Keep going",
                            color = SecondaryText,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(20.dp))

                        CommonButton(
                            text = "Next",
                        ) {
                            onNext()
                        }
                    }
                }

                // 3D Icon (Popping out)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color(0xFF039f41), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(12.dp, CircleShape)
                            .background(Color(0xFF1ac564), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.check_icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            NativeAdViewLarge(
                modifier = Modifier
                    .padding(top = 16.dp),
                adUnit = adUnit,
                logEvent = logEvent,
            )
        }
    }
}


@Preview
@Composable
fun WrongAnswerDialog(
    modifier: Modifier = Modifier,
    correctAnswer: String? = null,
    onTryAgain: (isAdWatched: Boolean) -> Unit = {},
    onNext: () -> Unit = {},
    coinsAvailable: Boolean = true,
    tryAgainCost: Int = 0,
    rewardedAdUnit: String? = null,
    logEvent: (LogEventType) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        NativeAdManager.onWrongDialogShown()
    }

    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Oops!",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "That’s not correct",
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    correctAnswer?.let {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Correct Answer",
                            color = MainText.copy(alpha = .9f),
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp)
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = it,
                            color = Color(0xff4CAF50),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            lineHeight = 22.sp
                        )
                    }
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (coinsAvailable) {
                            TryAgainButton(
                                modifier = Modifier.weight(1f),
                                text = "Try Again",
                                backgroundColor = CardColor,
                                textColor = Color.Black,
                                shape = RoundedCornerShape(30.dp),
                                coinCost = tryAgainCost
                            ) {
                                onTryAgain(false)
                            }
                        } else {
                            TryAgainAdButton(
                                modifier = Modifier.weight(1f),
                                text = "Try Again",
                                backgroundColor = CardColor,
                                textColor = Color.Black,
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                if (rewardedAdUnit == null) {
                                    Toast.makeText(
                                        context,
                                        "Ad not ready. Try later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@TryAgainAdButton
                                }
                                activity?.let {
                                    showRewardedAd(
                                        adUnit = rewardedAdUnit,
                                        activity = activity,
                                        logEvent = {
                                            logEvent(it)
                                        },
                                        onAdFailed = {
                                            Toast.makeText(
                                                context,
                                                "Ad not ready. Try later.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onAdDismissed = { onTryAgain(true) }
                                    )
                                }
                            }
                        }

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Next",
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            onNext()
                        }

                    }
                }
            }

            // 3D Error Icon
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(12.dp, CircleShape)
                    .background(Color(0xFFd91b21), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color(0xFFFF6B6B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close_icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun TimeUpDialog(
    modifier: Modifier = Modifier,
    onTryAgain: (isAdWatched: Boolean) -> Unit = {},
    onNext: () -> Unit = {},
    coinsAvailable: Boolean = true,
    tryAgainCost: Int = 0,
    rewardedAdUnit: String? = null,
    logEvent: (LogEventType) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        NativeAdManager.onWrongDialogShown()
    }

    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Time’s Up!",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "You ran out of time",
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (coinsAvailable) {
                            TryAgainButton(
                                modifier = Modifier.weight(1f),
                                text = "Try Again",
                                backgroundColor = CardColor,
                                textColor = Color.Black,
                                shape = RoundedCornerShape(30.dp),
                                coinCost = tryAgainCost
                            ) {
                                onTryAgain(false)
                            }
                        } else {
                            TryAgainAdButton(
                                modifier = Modifier.weight(1f),
                                text = "Try Again",
                                backgroundColor = CardColor,
                                textColor = Color.Black,
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                if (rewardedAdUnit == null) {
                                    Toast.makeText(
                                        context,
                                        "Ad not ready. Try later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@TryAgainAdButton
                                }
                                activity?.let {
                                    showRewardedAd(
                                        adUnit = rewardedAdUnit,
                                        activity = activity,
                                        logEvent = {
                                            logEvent(it)
                                        },
                                        onAdFailed = {
                                            Toast.makeText(
                                                context,
                                                "Ad not ready. Try later.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onAdDismissed = { onTryAgain(true) }
                                    )
                                }
                            }
                        }

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Next",
                        ) {
                            onNext()
                        }

                    }
                }
            }

            // ⏰ 3D Clock Icon (Popping Out)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(12.dp, CircleShape)
                    .background(Color(0xFFE09A1A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color(0xFFFFB020), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.time_icon_filled),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ResumeGameDialog(
    onStartOver: () -> Unit = {},
    onResume: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Game Paused",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Do you want to resume the game?",
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Start Over",
                            backgroundColor = CardColor,
                            contentColor = Color.Black
                        ) {
                            onStartOver()
                        }

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Resume",
                        ) {
                            onResume()
                        }
                    }
                }
            }

            // ▶️ Resume Icon (Floating)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, CircleShape)
                    .background(PrimaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.play_icon_2),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun RestartGameDialog(
    onCancel: () -> Unit = {},
    onConfirmRestart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Restart Game?",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Your current progress will be lost.",
                        color = Color.Black.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Cancel",
                            backgroundColor = CancelButtonColor,
                            contentColor = Color.Black
                        ) {
                            onCancel()
                        }

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Restart",
                            backgroundColor = TimeUpColor, // destructive action
                            contentColor = Color.White
                        ) {
                            onConfirmRestart()
                        }
                    }
                }
            }

            // 🔄 Restart Icon (Warning / Destructive)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, CircleShape)
                    .background(TimeUpColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.refresh_icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

