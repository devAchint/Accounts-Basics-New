package com.techuntried.accountsbasics2.ui.commons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.utils.rememberDebouncedClick

@Composable
fun CommonButtonWithImage(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    icon: Int,
    iconTint: Color = Color.Unspecified,
    backgroundColor: Color = Color.White,
    textColor: Color = BackgroundColor,
    disabledContainerColor: Color = Color.Transparent,
    disabledTextColor: Color = Color.White,
    border: BorderStroke? = null,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit,
) {
    Button(
        onClick = rememberDebouncedClick { onClick() },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledTextColor,
            contentColor = textColor
        ),
        border = border
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}