package com.techuntried.accountsbasics2.ui.rules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText

@Composable
fun RuleListContent(
    modifier: Modifier = Modifier,
    rulesScreenUiState: RulesScreenUiState.Success,
    onStart: () -> Unit
) {
    val firstRule = rulesScreenUiState.rules.firstOrNull()?.description
    val remainingRules = rulesScreenUiState.rules.drop(1)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            RuleImageCard(
                imageUrl = rulesScreenUiState.iconUrl,
                isChallenge = false,
                bgColor = rulesScreenUiState.bgColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = rulesScreenUiState.title ?: "-",
                color = MainText,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            RuleLevelUpgrade(level = rulesScreenUiState.chapterId, upgradeText = firstRule ?: "")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Rules",
                color = MainText,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            remainingRules.forEach {
                RuleListItem(
                    rule = it,
                    shouldCutTime =  rulesScreenUiState.timerCount == null
                )
            }
        }
        CommonButton(
            text = stringResource(R.string.start_now),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            onStart()
        }
    }

}

@Composable
fun RuleImageCard(
    modifier: Modifier = Modifier, isChallenge: Boolean, imageUrl: String?, bgColor: String?
) {
    val cardColor = if (isChallenge) {
        Color.White
    } else {
        try {
            if (!bgColor.isNullOrEmpty()) Color(bgColor.toColorInt()) else Color.White
        } catch (e: Exception) {
            Color.White
        }
    }

    Card(
        modifier = Modifier.size(180.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp,BorderColor),
        shape = RoundedCornerShape(50.dp)
    ) {

        val density = LocalDensity.current
        val imageSizePx = remember(density) {
            with(density) { 180.dp.roundToPx() }
        }

        val painter = if (isChallenge) {
            painterResource(id = R.drawable.trophy_1)
        } else {
            if (imageUrl.isNullOrEmpty()) {
                painterResource(id = R.drawable.image_placeholder)
            } else {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                        .size(imageSizePx).crossfade(true)
                        .placeholder(R.drawable.image_placeholder) // optional placeholder while loading
                        .error(R.drawable.image_placeholder)       // fallback on error
                        .build()
                )
            }
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isChallenge) 32.dp else 16.dp)
        )
    }
}

@Composable
fun RuleListItem(modifier: Modifier = Modifier, rule: RuleModel, shouldCutTime: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(rule.icon), contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .clip(CircleShape)
                .background(PrimaryColor)
                .size(36.dp)
                .padding(8.dp)

        )
        Spacer(modifier = Modifier.width(8.dp))
        val textDecoration = if (rule.title.contains(
                "Time Limit", ignoreCase = true
            ) && shouldCutTime
        ) TextDecoration.LineThrough else TextDecoration.None

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = rule.title,
                color = MainText,
                style = MaterialTheme.typography.titleSmall,
                textDecoration = textDecoration
            )
            Text(
                text = rule.description,
                color = SecondaryText,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = textDecoration
            )
        }
    }
}

@Composable
fun RuleLevelUpgrade(modifier: Modifier = Modifier, level: Int, upgradeText: String) {
    Column {
        Text(
            text = "Level",
            color = MainText,
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(PrimaryColor)
                .padding(6.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.toString(),
                    color = PrimaryColor,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = upgradeText,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}