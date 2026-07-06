package com.techuntried.accountsbasics2.ui.commons

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.utils.debouncedClickable

@Composable
fun AppUpdateCard(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val appLink = stringResource(R.string.app_link)

    Row(
        modifier = modifier
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
            .background(PrimaryColor)
            .clickable(enabled = false){}
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        CommonButtonWrap(
            text = "Update",
            backgroundColor = Color.White,
            textColor = Color.Black,
            height = 36.dp,
            textSize = 14.sp,
            shape = RoundedCornerShape(4.dp)
        ) {
            try {
                Intent(Intent.ACTION_VIEW).apply {
                    data = appLink.toUri()
                    context.startActivity(this)
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.close_icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .debouncedClickable {
                    onDismiss()
                }
                .padding(2.dp)
        )
    }
}