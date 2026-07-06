package com.techuntried.accountsbasics2.ui.notificationPermission

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonTextButton
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.utils.Spacer
import com.techuntried.accountsbasics2.utils.findActivity
import com.techuntried.accountsbasics2.utils.openNotificationSettings

@Composable
fun NotificationPermissionScreenRoot(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: NotificationPermissionViewModel = hiltViewModel()
    val notificationPermissionUiState =
        viewModel.notificationPermissionUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(notificationPermissionUiState.message) {
        notificationPermissionUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    LaunchedEffect(notificationPermissionUiState.firstTimeFlagSaved) {
        if (notificationPermissionUiState.firstTimeFlagSaved) {
            onContinue()
        }
    }

    NotificationPermissionScreen(
        notificationPermissionUiState = notificationPermissionUiState,
        onContinue = {
            viewModel.saveFirstTime()
        }
    )
}


@Composable
fun NotificationPermissionScreen(
    notificationPermissionUiState: NotificationPermissionUiState,
    onContinue: () -> Unit = {}
) {
    val context = LocalContext.current
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                onContinue()
            }
        }

    val activity = context.findActivity()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Allow Notifications",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MainText,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(20.dp)
            Image(
                painter = painterResource(R.drawable.notification_illus),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
            )
            Spacer(20.dp)
            Text(
                text = "Would you like to allow reminders to help you stay consistent with learning and receive new updates?",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MainText,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Column {
            CommonButton(text = "Yes, please") {
                if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val shouldShowRationaleDialog = activity?.let {
                            shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } == true

                        if (shouldShowRationaleDialog) {
                            openNotificationSettings(context)
                        } else {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }else{
                        onContinue()
                    }
                } else {
                    onContinue()
                }
            }
            Spacer(value = 8.dp)
            CommonTextButton(
                text = "Not now",
                contentColor = Color.Black,
            ) {
                onContinue()
            }
        }
    }
}

@Preview
@Composable
fun NotificationPermissionScreenPreview() {
    NotificationPermissionScreen(NotificationPermissionUiState())
}