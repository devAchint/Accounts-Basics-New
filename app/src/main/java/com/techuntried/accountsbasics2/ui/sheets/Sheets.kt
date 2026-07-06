package com.techuntried.accountsbasics2.ui.sheets


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonDropdownInput
import com.techuntried.accountsbasics2.ui.commons.CommonTextInput
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.InputBackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestCategorySheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var suggestedCategory by remember { mutableStateOf("") }
    var isCategoryValid by remember { mutableStateOf<Boolean?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundColor,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Suggest a category!",
                color = MainText,
                style = MaterialTheme.typography.headlineLarge
            )
            CommonTextInput(
                value = suggestedCategory,
                onValueChange = { suggestedCategory = it },
                placeHolder = stringResource(R.string.suggest_category_hint),
                isError = isCategoryValid == false,
                errorMsg = stringResource(R.string.suggest_category_constraint),
                imeAction = ImeAction.Done
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    backgroundColor = CardColor,
                    contentColor = Color.Black
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.width(8.dp))
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Submit",
                    backgroundColor = PrimaryColor,
                    contentColor = Color.White
                ) {
                    isCategoryValid = suggestedCategory.length >= 3
                    if (isCategoryValid == true) {
                        onSubmit(suggestedCategory)
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionSheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var suggestedCategory by remember { mutableStateOf("") }
    var isCategoryValid by remember { mutableStateOf<Boolean?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundColor,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Share your suggestion",
                color = MainText,
                style = MaterialTheme.typography.headlineLarge
            )
            CommonTextInput(
                value = suggestedCategory,
                onValueChange = { suggestedCategory = it },
                placeHolder = stringResource(R.string.suggest_hint),
                isError = isCategoryValid == false,
                errorMsg = stringResource(R.string.suggestion_constraint),
                imeAction = ImeAction.Done
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    backgroundColor = CardColor,
                    contentColor = Color.Black
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.width(8.dp))
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Submit",
                    backgroundColor = PrimaryColor,
                    contentColor = Color.White
                ) {
                    isCategoryValid = suggestedCategory.length >= 8
                    if (isCategoryValid == true) {
                        onSubmit(suggestedCategory)
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBackSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.exit_confirm_title),
                color = MainText,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = stringResource(R.string.exit_confirm_description),
                color = SecondaryText,
                style = MaterialTheme.typography.bodyLarge
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    backgroundColor = CardColor,
                    contentColor = Color.Black
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.width(8.dp))
                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Submit",
                ) {
                    onConfirm()
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameControlsSheet(
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    showCorrectEnabled: Boolean,
    onShowCorrectToggle: (Boolean) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticToggle: (Boolean) -> Unit,
    onReportClick: () -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ControlSwitchRow(
                title = "Sound Effects",
                checked = soundEnabled,
                onCheckedChange = onSoundToggle
            )

            ControlSwitchRow(
                title = "Haptics",
                checked = hapticEnabled,
                onCheckedChange = onHapticToggle
            )

            ControlSwitchRow(
                title = "Show Correct Answers",
                checked = showCorrectEnabled,
                onCheckedChange = onShowCorrectToggle
            )

            ControlActionRow(
                title = "Report Issue",
                onClick = onReportClick
            )

            CommonButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Close",
                backgroundColor = CardColor,
                contentColor = Color.Black
            ) {
                onClose()
            }
        }
    }
}

@Composable
private fun ControlSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MainText,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge
        )

        Switch(
            checked = checked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF9E9E9E)
            ),
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ControlActionRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge
        )

        Icon(
            painter = painterResource(R.drawable.right_icon),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportQuestionSheet(
    onDismiss: () -> Unit,
    onSubmit: (reason: String, details: String?) -> Unit
) {

    val options = arrayOf(
        "Select a reason",
        "Incorrect Information",
        "Offensive Content",
        "Spam or Irrelevant",
        "Other (Please Specify)"
    )

    var selectedReason by remember { mutableStateOf(options[0]) }
    var otherText by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf<Boolean?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundColor,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Report Question",
                color =MainText,
                style = MaterialTheme.typography.headlineLarge
            )

            CommonDropdownInput(
                value = selectedReason,
                onValueChange = {
                    selectedReason = it
                },
                placeHolder = "Select a reason",
                options = options.toList(),
                isError = isValid == false && selectedReason == options[0],
                backgroundColor = InputBackgroundColor,
            )


            // ✏️ Other reason input (conditional)
            if (selectedReason == "Other (Please Specify)") {
                CommonTextInput(
                    value = otherText,
                    onValueChange = { otherText = it },
                    placeHolder = "Please describe the issue",
                    isError = isValid == false && otherText.length < 5,
                    errorMsg = "Please provide more details",
                    imeAction = ImeAction.Done
                )
            }

            // 🟦 Buttons
            Row(modifier = Modifier.fillMaxWidth()) {

                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    backgroundColor = CardColor,
                    contentColor = Color.Black
                ) {
                    onDismiss()
                }

                Spacer(modifier = Modifier.width(8.dp))

                CommonButton(
                    modifier = Modifier.weight(1f),
                    text = "Submit",
                ) {
                    isValid = when {
                        selectedReason == options[0] -> false
                        selectedReason == "Other (Please Specify)" && otherText.length < 5 -> false
                        else -> true
                    }

                    if (isValid == true) {
                        onSubmit(
                            selectedReason,
                            if (selectedReason == "Other (Please Specify)") otherText else null
                        )
                    }
                }
            }
        }
    }
}
