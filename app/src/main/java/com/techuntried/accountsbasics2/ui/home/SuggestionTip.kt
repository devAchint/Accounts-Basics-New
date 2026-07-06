package com.techuntried.accountsbasics2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.utils.Spacer

@Composable
fun SuggestionTip(
    modifier: Modifier = Modifier,
    text: String = "Suggest a category!",
    onClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(CardColor)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.hint_icon),
                contentDescription = null,
                tint = MainText,
                modifier = Modifier
                    .size(22.dp)

            )
            Spacer(6.dp)
            Text(
                textAlign = TextAlign.Center,
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MainText
            )
        }
    }
}