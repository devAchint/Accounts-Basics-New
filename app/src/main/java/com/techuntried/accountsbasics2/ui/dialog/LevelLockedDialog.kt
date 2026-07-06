package com.techuntried.accountsbasics2.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ads.showRewardedAd
import com.techuntried.accountsbasics2.ui.commons.CommonButtonWithImage
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.CancelButtonColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.findActivity

@Composable
fun LevelLockedDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    logEvent: (LogEventType) -> Unit = {},
    unlockCoins:Int,
    rewardedAdUnit: String? = null,
    useCoins: () -> Unit,
    onAdRewardEarned: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    Dialog(onDismissRequest = onDismiss) {

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(PrimaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.lock_icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Level Locked",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Complete previous levels\nor unlock instantly.",
                        color = Color.Black.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))
                    CommonButtonWithImage(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Unlock Level",
                        icon = R.drawable.watch_ad_icon,
                        iconTint = Color.White,
                        backgroundColor = PrimaryColor,
                        textColor = Color.White,
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        if (rewardedAdUnit == null) return@CommonButtonWithImage
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
                                onRewardEarned = onAdRewardEarned
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    CommonButtonWithImage(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Use $unlockCoins Coins",
                        icon = R.drawable.coin,
                        backgroundColor = CancelButtonColor,
                        textColor = Color.Black,
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(30.dp)

                    ) {
                        useCoins()
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No Thanks",
                        color = Color.Black.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
            }
        }
    }
}