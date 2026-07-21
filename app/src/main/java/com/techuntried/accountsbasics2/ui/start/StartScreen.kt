package com.techuntried.accountsbasics2.ui.start

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText

@Composable
fun StartScreenRoot(
    modifier: Modifier = Modifier,
    openNotificationPermission:()->Unit
) {
    StartScreen(
        openNotificationPermission = openNotificationPermission
    )
}


@Composable
fun StartScreen(
    openNotificationPermission:()->Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .background(BackgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.ill),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
            )
            Text(
                text = "Play & Learn Science",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                color = MainText,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Learn science the fun way with quizzes in Physics, Chemistry & Biology.",
                textAlign = TextAlign.Center,
                color = MainText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

        }
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CommonButton(
                text = "Get Started",
            ) {
                openNotificationPermission()
            }
            Spacer(modifier = Modifier.height(12.dp))

            val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    text = "By continuing you provide an agreement to our ",
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Privacy Policy.",
                    color = MainText,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            val builder = CustomTabsIntent.Builder()
                            val customTabsIntent = builder.build()
                            customTabsIntent.launchUrl(context, privacyPolicyUrl.toUri())
                        }
                )
            }
        }
    }

}



