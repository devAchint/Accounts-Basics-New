package com.techuntried.accountsbasics2.ui.improve

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CoinsSheet
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.sheets.SuggestCategorySheet
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun ImproveScreenRoot(
    modifier: Modifier = Modifier,
    practiceAll:()->Unit,
    practiceQuestion:(subjectId:Int,chapterId:Int,questionId: Int)-> Unit
) {
    val context = LocalContext.current
    val viewModel: ImproveViewModel = hiltViewModel()
    val improveUiState = viewModel.improveUiState.collectAsStateWithLifecycle().value
    val coinsState = viewModel.coinsState.collectAsStateWithLifecycle().value

    LaunchedEffect(improveUiState.message) {
        improveUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    ImproveScreen(
        improveUiState = improveUiState,
        coins = coinsState,
        practiceAll = practiceAll,
        practiceQuestion = practiceQuestion,
        rewardedAdUnit = null,
        onAction = viewModel::onAction,
    )
}

@Composable
fun ImproveScreen(
    improveUiState: ImproveUiState,
    coins: Int,
    rewardedAdUnit: String?,
    onAction: (ImproveActions) -> Unit,
    practiceAll:()->Unit,
    practiceQuestion:(subjectId:Int,chapterId:Int,questionId: Int)-> Unit
) {
    var showSuggestSheet by remember { mutableStateOf(false) }
    var showCoinsSheet by remember { mutableStateOf(false) }

    if (showCoinsSheet) {
        CoinsSheet(
            balance = coins,
            onDismiss = { showCoinsSheet = false },
            rewardedAdUnit = rewardedAdUnit,
            onAddCoins = {
                onAction(ImproveActions.AddCoin(50))
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // App's Standard Topbar
        CommonToolbar(
            title = "Improve",
            isBalance = true,
            balance = coins,
            onBalanceClick = { showCoinsSheet = true }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (improveUiState) {
                is ImproveUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(improveUiState.errorMessage),
                        description = getErrorMessageDescription(improveUiState.errorMessage),
                        actionButton = "Try Again",
                        action = {
                            onAction(ImproveActions.Refresh)
                        },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                ImproveUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is ImproveUiState.Success -> {
                    if (improveUiState.mistakeItems.isEmpty()){
                        ErrorMessageView(
                            icon = R.drawable.empty_progress_icon,
                            errorTitle = "No Improvements",
                            description = "Keep up the good work",
                            actionButton = null,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }else {
                        ImproveContent(
                            uiState = improveUiState,
                            practiceAll = practiceAll,
                            practiceQuestion = practiceQuestion,
                            bannerAdUnit = null,
                            onAction = onAction,
                            showSuggestionSheet = { showSuggestSheet = true },
                            logEvent = { onAction(ImproveActions.LogEvent(it)) }
                        )
                    }

                    // Show explanation bottom sheet if user taps "Show why"
                    improveUiState.activeExplanation?.let { item ->
                        ExplanationBottomSheet(
                            item = item,
                            onDismiss = { onAction(ImproveActions.ShowExplanation(null)) }
                        )
                    }
                }
            }

            if (improveUiState.actionLoading) {
                CommonCircularProgress(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }

    if (showSuggestSheet) {
        SuggestCategorySheet(
            onDismiss = { showSuggestSheet = false },
            onSubmit = { comment ->
                onAction(ImproveActions.UploadSuggestion(comment))
                showSuggestSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExplanationBottomSheet(
    item: MistakeItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "1",
                    style = MaterialTheme.typography.titleMedium,
                    color = MainText
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.subject,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.questionText,
                style = MaterialTheme.typography.titleLarge,
                color = MainText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0FDF4))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "CORRECT ANSWER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.correctAnswer,
                        style = MaterialTheme.typography.titleSmall,
                        color = MainText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EXPLANATION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MainText
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111214)),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Got it",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}