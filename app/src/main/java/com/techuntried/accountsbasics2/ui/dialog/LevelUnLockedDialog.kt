package com.techuntried.accountsbasics2.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor

@Composable
fun LevelUnLockedDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    play: () -> Unit
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
                            painter = painterResource(R.drawable.unlock_icon),
                            contentDescription = null,
                            tint = BackgroundColor,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Level Unlocked!",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "You've successfully unlocked this level.",
                        color = Color.Black.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    InfoText()
                    Spacer(Modifier.height(20.dp))
                    CommonButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Play Now",
                        backgroundColor = PrimaryColor,
                        contentColor = Color.White
                    ) {
                        play()
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Cancel",
                        color = Color.Black.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoText(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryColor.copy(alpha = 0.2f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.info_icon),
            contentDescription = null,
            tint =  Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(R.string.level_unlocked_note),
            color = Color.Black.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}