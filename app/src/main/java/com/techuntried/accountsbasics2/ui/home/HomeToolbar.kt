package com.techuntried.accountsbasics2.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
        "Pick a subject and let's begin!",
        "Boost your knowledge today!",
        "What challenge are you up for?",
        "Choose a category to get started!",
        "Let's learn something new!",
        "Which quiz excites you today?"
    )

    val randomSubtitle = rememberSaveable { subtitleOptions.random() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            AnimatedVisibility(
                visible = username != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "Hi ${username ?: "Learner"}!",
                        color = MainText,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = randomSubtitle,
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
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
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}