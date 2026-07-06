package com.techuntried.accountsbasics2.ui.commons

import android.widget.Toast
import com.techuntried.accountsbasics2.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.ads.showRewardedAd
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.findActivity


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CoinsSheet(
    modifier: Modifier = Modifier,
    balance: Int = 0,
    rewardedAdUnit: String? = null,
    onDismiss: () -> Unit = {},
    logEvent: (LogEventType) -> Unit = {},
    onAddCoins: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = { }
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFFDE7), Color.White)
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Drag handle
            Box(
                Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.4f))
            )


            // Coin icon with background

            Icon(
                painter = painterResource(R.drawable.coin),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp)
            )

            // Balance
            Text(
                text = "$balance",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
            )

            Text(
                text = "You have $balance Coins",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
            )

            Text(
                text = "Use coins to unlock levels faster and enjoy premium rewards!",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            // Button
            CommonButton(
                text = "+50 Coins – Watch Ad",
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (rewardedAdUnit == null) {
                    Toast.makeText(
                        context,
                        "Ad not ready. Try later.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@CommonButton
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
                        onRewardEarned = { onAddCoins() }
                    )
                }
            }
        }
    }
}
