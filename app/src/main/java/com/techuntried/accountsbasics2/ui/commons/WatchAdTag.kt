package com.techuntried.accountsbasics2.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.R

@Composable
fun WatchAdTag(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val bgColor = if (enabled)
        Brush.verticalGradient(
            listOf(Color(0xFFFFD166), Color(0xFFFFB703))
        )
    else
        Brush.verticalGradient(
            listOf(Color(0xFFB0ADC9), Color(0xFF8E8AAE))
        )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.watch_ad_icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(13.dp)
        )

        Spacer(Modifier.width(2.dp))

        Text(
            text = "Watch Ad",
            color = Color(0xFF3A2E00),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
