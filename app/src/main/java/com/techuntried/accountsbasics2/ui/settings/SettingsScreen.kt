package com.techuntried.accountsbasics2.ui.settings

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.ump.UserMessagingPlatform
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.dialog.UsernameDialog
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.findActivity
import com.techuntried.accountsbasics2.utils.openNotificationSettings

@Composable
fun SettingsScreenRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    openFeedback: () -> Unit = {},
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState = viewModel.settingsUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("Settings"))
    }

    SettingsScreen(
        settingsUiState = settingsUiState,
        onBack = onBack,
        openFeedback = openFeedback,
        updatePreference = viewModel::updateUserPreference
    )
}


@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onBack: () -> Unit = {},
    openFeedback: () -> Unit = {},
    updatePreference: (PreferenceType) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    var usernameDialog by remember { mutableStateOf(false) }
    val youSetting = listOf(
        SettingItemUi(
            title = stringResource(R.string.change_name),
            icon = AppIcons.ProfileFilled,
            summary = settingsUiState.username ?: "",
            isSummaryShown = settingsUiState.username != null,
            onClick = {
                usernameDialog = true
            }
        )
    )
    val preferences = listOfNotNull(
        SettingItemUi(
            title = stringResource(R.string.sounds),
            icon = R.drawable.sound_on_icon,
            switchShown = true,
            switchChecked = settingsUiState.isSoundEnabled,
            onCheckedChange = {
                updatePreference(PreferenceType.Sound(it))
            }
        ),
        SettingItemUi(
            title = stringResource(R.string.haptics),
            icon = R.drawable.haptic_icon,
            switchShown = true,
            switchChecked = settingsUiState.isHapticEnabled,
            onCheckedChange = {
                updatePreference(PreferenceType.Haptic(it))
            }
        ),
        SettingItemUi(
            title = "Show Correct Answers",
            icon = R.drawable.check_circle_icon,
            switchShown = true,
            switchChecked = settingsUiState.isShowCorrectEnabled,
            onCheckedChange = {
                updatePreference(PreferenceType.ShowCorrect(it))
            }
        ),
        SettingItemUi(
            title = "Timer",
            icon = R.drawable.timer_icon,
            switchShown = true,
            switchChecked = settingsUiState.isTimerEnabled,
            onCheckedChange = {
                updatePreference(PreferenceType.Timer(it))
            }
        ),
        if (settingsUiState.dataPreferencesVisible) {
            SettingItemUi(
                title = stringResource(R.string.privacy_settings),
                icon = R.drawable.privacy_policy_icon,
                onClick = {
                    activity?.let {
                        UserMessagingPlatform.showPrivacyOptionsForm(it) { error ->
                            Log.d("MYDEBUG", "error=${error?.message}")
                        }
                    }
                }
            )
        } else null
    )

    val shareAppText = stringResource(R.string.share_app_txt)
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)
    val appLink = stringResource(R.string.app_link)

    val moreList = listOf(
        SettingItemUi(
            title = stringResource(R.string.shareApp),
            icon = R.drawable.share_icon,
            onClick = {
                Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, shareAppText)
                    type = "text/plain"
                    context.startActivity(Intent.createChooser(this, null))
                }
            }
        ),
        SettingItemUi(
            title = stringResource(R.string.rate_us),
            icon = R.drawable.rate_icon,
            onClick = {
                Intent(Intent.ACTION_VIEW).apply {
                    data = appLink.toUri()
                    context.startActivity(this)
                }
            }
        ),
        SettingItemUi(
            title = stringResource(R.string.feedback),
            icon = R.drawable.feedback_icon,
            onClick = openFeedback
        ),
        SettingItemUi(
            title = stringResource(R.string.privacyPolicy),
            icon = R.drawable.privacy_policy_icon,
            onClick = {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, privacyPolicyUrl.toUri())
            }
        ),
    )

    var permissionGranted by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                permissionGranted = false
            }
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        CommonToolbar(
            title = "Settings",
            onNavigationClick = onBack
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            if (!permissionGranted) {
                NotificationCard(
                    onAllowClick = {
                        openNotificationSettings(context = context)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            GroupedProfileCard("You", youSetting)
            Spacer(modifier = Modifier.height(10.dp))
            GroupedProfileCard("Preferences", preferences)
            Spacer(modifier = Modifier.height(10.dp))
            GroupedProfileCard("More", moreList)
            Spacer(modifier = Modifier.height(32.dp))
            //update version before publishing
            Text(
                text = "App Version 1.0",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = SecondaryText,
                style = MaterialTheme.typography.labelMedium
            )
        }

    }

    if (usernameDialog) {
        UsernameDialog(
            currentUserName = settingsUiState.username ?: "",
            onCancel = { usernameDialog = false },
            onChange = {
                updatePreference(PreferenceType.Name(it))
                usernameDialog = false
            }
        )
    }

}


data class SettingItemUi(
    val title: String,
    val icon: Int,
    val isSummaryShown: Boolean = false,
    val summary: String = "",
    val switchShown: Boolean = false,
    val switchChecked: Boolean = true,
    val onCheckedChange: (Boolean) -> Unit = {},
    val onClick: () -> Unit = {}
)

@Composable
fun GroupedProfileCard(header: String, items: List<SettingItemUi>) {
    Column {
        Text(
            text = header,
            color = MainText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
        ) {
            items.forEachIndexed { index, item ->
                val roundedClip = if (items.size == 1) {
                    RoundedClip.Both
                } else {
                    when (index) {
                        0 -> RoundedClip.Top
                        items.size - 1 -> RoundedClip.Bottom
                        else -> RoundedClip.Not
                    }
                }

                ProfileItem(
                    iconId = item.icon,
                    title = item.title,
                    summary = item.summary.takeIf { item.isSummaryShown },
                    roundedClip = roundedClip,
                    switchShown = item.switchShown,
                    switchChecked = item.switchChecked,
                    click = item.onClick,
                    onCheckedChange = item.onCheckedChange
                )
                if (index != items.size - 1)
                    HorizontalDivider(color = BorderColor)


            }
        }
    }

}

@Preview
@Composable
fun ProfileItem(
    iconId: Int = R.drawable.haptic_icon,
    title: String = "Demo",
    summary: String? = null,
    roundedClip: RoundedClip = RoundedClip.Top,
    switchShown: Boolean = true,
    switchChecked: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
    click: () -> Unit = {}
) {
    val roundedCornerShape = when (roundedClip) {
        RoundedClip.Top -> {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        }

        RoundedClip.Bottom -> {
            RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        }

        RoundedClip.Both -> {
            RoundedCornerShape(16.dp)
        }

        else -> {
            RoundedCornerShape(0.dp)
        }
    }
    val paddingValues = if (switchShown) {
        PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
    } else {
        PaddingValues(16.dp)
    }
    Column(
        modifier = Modifier
            .clip(roundedCornerShape)
            .background(Color.White)
            .clickable(enabled = !switchShown) {
                click()
            }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = MainText,
                modifier = Modifier.size(24.dp)

            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    color = MainText,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            summary?.let {
                Text(
                    text = it, color = SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
            if (switchShown) {
                SettingSwitch(isChecked = switchChecked) { checked ->
                    onCheckedChange(checked)
                }
            } else {
                Icon(
                    painter = painterResource(AppIcons.RightArrow),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    }

}


enum class RoundedClip {
    Top, Bottom, Both, Not
}

@Composable
fun SettingSwitch(
    isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = Modifier.scale(.75f),
        checked = isChecked,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = PrimaryColor,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFF9E9E9E)
        ),
        onCheckedChange = onCheckedChange
    )
}

@Preview
@Composable
fun NotificationCard(modifier: Modifier = Modifier, onAllowClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enable push notifications to stay updated on new quizzes, challenges, and tips.",
                color = MainText,
                style = MaterialTheme.typography.bodyLarge
            )
            CommonButton(
                text = "Allow notifications",
                onClick = onAllowClick,
            )
        }
    }
}
