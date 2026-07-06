package com.techuntried.accountsbasics2.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.RubikRegular

@Composable
fun ScoreRatingCard(modifier: Modifier = Modifier, onRateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.score_rating_title),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.score_rating_des),
            fontSize = 14.sp,
            fontFamily = RubikRegular,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.score_rate_us),
            fontSize = 14.sp,
            fontFamily = RubikRegular,
            color = Color.Black,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .clickable {
                    onRateClick()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

    }
}