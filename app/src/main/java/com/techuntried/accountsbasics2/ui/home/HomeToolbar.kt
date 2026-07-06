package com.techuntried.accountsbasics2.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.ui.commons.BalanceLayout
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText

@Composable
fun HomeToolbar(
    modifier: Modifier = Modifier,
    username: String?,
    coins: Int,
    showCoinsSheet: () -> Unit
) {
    val subtitleOptions = listOf(
        "What would you like to play?",
        "Ready for a quick quiz?",
        "Pick a topic and let's begin!",
        "Boost your knowledge today!",
        "What challenge are you up for?",
        "Choose a category to get started!",
        "Let's learn something new!",
        "Which quiz excites you today?"
    )

    val randomSubtitle = rememberSaveable { subtitleOptions.random() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(horizontal = 10.dp, vertical = 8.dp), // ⭐ add this
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(start = 6.dp, end = 16.dp)
                .weight(1f)
        ) {
            AnimatedVisibility(
                visible = username != null,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }
                ) + fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "Hi ${username}!",
                        color = MainText,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = randomSubtitle,
                        color = SecondaryText,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (username == null) {
                Text(
                    text = "Home",
                    color = MainText,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
            }

        }

        BalanceLayout(
            onClick = showCoinsSheet,
            balance = coins,
            modifier = Modifier
                .padding(end = 6.dp)
                .align(Alignment.CenterVertically)
        )

    }
}