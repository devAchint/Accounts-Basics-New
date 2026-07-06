package com.techuntried.accountsbasics2.ui.feedback

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.RatingBar
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.InputBackgroundColor
import com.techuntried.accountsbasics2.ui.theme.InputHintColor
import com.techuntried.accountsbasics2.ui.theme.InputTextColor
import com.techuntried.accountsbasics2.utils.AppIcons

@Composable
fun FeedbackScreenRoot(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: FeedbackViewModel = hiltViewModel()
    val feedbackUiState = viewModel.feedbackUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(feedbackUiState.message) {
        feedbackUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    FeedbackScreen(
        feedbackUiState = feedbackUiState,
        onBackClick = onBackClick,
        updateFeedback = viewModel::updateFeedback,
        submitFeedback = viewModel::submitFeedback,
        updateRating = viewModel::updateRating
    )
}


@Composable
fun FeedbackScreen(
    feedbackUiState: FeedbackUiState,
    onBackClick: () -> Unit = {},
    updateFeedback: (String) -> Unit,
    updateRating: (Int) -> Unit,
    submitFeedback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .imePadding()
    ) {
        CommonToolbar(
            title = "Feedback",
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = onBackClick
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Rating", color = Color.Black)
                RatingBar(
                    ratingState = feedbackUiState.rating,
                    onRatingStateChanged = updateRating
                )
                if (feedbackUiState.isRatingValid == false) {
                    Text(
                        text = "Please select a rating",
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tell us more", color = Color.Black)
                val interactionSource = remember { MutableInteractionSource() }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .indication(interactionSource, null),
                    value = feedbackUiState.feedback,
                    enabled = !feedbackUiState.isLoading,
                    onValueChange = {
                        if (it.length <= 500) {
                            updateFeedback(it)
                        } else {
                            updateFeedback(it.take(500))
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Share your thoughts, suggestions, or report an abuse",
                            fontSize = 14.sp,
                            color = InputHintColor
                        )
                    },
                    minLines = 6,
                    maxLines = 10,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                       focusedTextColor = InputTextColor,
                        unfocusedTextColor = InputTextColor,
                        disabledTextColor = InputTextColor,
                        focusedContainerColor = InputBackgroundColor,
                        unfocusedContainerColor = InputBackgroundColor,
                        disabledContainerColor = InputBackgroundColor,
                        focusedBorderColor = BorderColor,
                        disabledBorderColor = BorderColor,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = Color.Black
                    )
                )
                Text(
                    text = "${feedbackUiState.feedback.length}/500 characters",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (feedbackUiState.isFeedbackValid == false) {
                    Text(
                        text = "Feedback must be at least 10 char long",
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                }


            }
            Spacer(modifier = Modifier.height(16.dp))
            CommonButton(
                text = "Submit",
                isLoading = feedbackUiState.isLoading
            ) {
                submitFeedback()
            }
        }

    }
}