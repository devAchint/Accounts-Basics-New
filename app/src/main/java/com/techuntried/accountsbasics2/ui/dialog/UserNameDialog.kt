package com.techuntried.accountsbasics2.ui.dialog

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonTextInput
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.CancelButtonColor
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor

@Preview
@Composable
fun UsernameDialog(
    currentUserName:String="",
    onCancel: () -> Unit = {},
    onChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf(currentUserName) }
    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundColor),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Change Name",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(Modifier.height(20.dp))

                    CommonTextInput(
                        value = username,
                        onValueChange = { username = it },
                        placeHolder = "Name",
                        maxLength = 20
                    )
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Cancel",
                            backgroundColor = CancelButtonColor,
                            contentColor = Color.Black
                        ) {
                            onCancel()
                        }

                        CommonButton(
                            modifier = Modifier.weight(1f),
                            text = "Change",
                        ) {
                            onChange(username)
                        }
                    }
                }
            }

            // 🔄 Restart Icon (Warning / Destructive)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, CircleShape)
                    .background(PrimaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.profile_filled),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
