package com.techuntried.accountsbasics2.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.ToolbarContentColor
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.rememberDebouncedClick

@Composable
fun GameToolbar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: Int = R.drawable.back_icon,
    onNavigationClick: () -> Unit = {},
    onGameControlsClick: () -> Unit = {},
    actions: List<ToolbarAction> = emptyList(),
    balance: Int? = null,
    contentColor: Color = ToolbarContentColor,
    onBalanceClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Standard toolbar height
            .padding(horizontal = 10.dp)
    ) {
        // Back icon (left)

        IconButton(
            onClick = rememberDebouncedClick { onNavigationClick() }, modifier = Modifier
                .padding(end = 4.dp)
                .align(Alignment.CenterVertically)
                .size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = navigationIcon),
                contentDescription = "Back",
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)

            )
        }


        // Title (center)
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1
        )

        // Right icon

        actions.forEach { action ->
            IconButton(onClick = action.onClick) {
                Icon(
                    painter = painterResource(action.icon),
                    contentDescription = action.contentDescription,
                    tint = contentColor
                )
            }
        }



        BalanceLayout(
            onClick = onBalanceClick,
            balance = balance,
            modifier = Modifier
                .padding(end = 6.dp)
                .align(Alignment.CenterVertically)
        )

        Box(
            modifier = Modifier
                .padding(end = 6.dp)
                .align(Alignment.CenterVertically)
                .clip(CircleShape)
                .background(PrimaryColor)
                .debouncedClickable { onGameControlsClick() }
                .padding(horizontal = 5.dp, vertical = 5.dp)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu_icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), color = BorderColor)
    }
}
