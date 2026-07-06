package com.techuntried.accountsbasics2.ui.commons

import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingSheet(
    onDismiss: () -> Unit,
    submitRatingLessThanFour: (rating: Int) -> Unit
) {
    val context = LocalContext.current
    var ratingState by remember {
        mutableIntStateOf(0)
    }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = ratingTitle(ratingState),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = MainText,
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close_circle_icon),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(28.dp),
                        tint = MainText
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ratingDescription(ratingState),
                color = SecondaryText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            RatingBar(ratingState = ratingState, onRatingStateChanged = { ratingState = it })
            Spacer(modifier = Modifier.height(16.dp))
            val appLink = stringResource(id = R.string.app_link)
            CommonButton(
                enabled = ratingState!=0,
                onClick = {
                    onDismiss()
                    if (ratingState >= 4) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(appLink)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        submitRatingLessThanFour(ratingState)
                    }
                },
                text = "Rate"
            )
        }

    }
}

fun ratingDescription(ratingState: Int): String {
    return when (ratingState) {
        1 -> {
            "Sorry it didn’t meet your expectations."
        }

        2 -> {
            "We’ll work on making this better."
        }

        3 -> {
            "Thanks for your feedback."
        }

        4 -> {
            "Glad you liked the experience."
        }

        5 -> {
            "Awesome! Thanks for the support!"
        }

        else -> {
            "We’d love your feedback. Please rate us!"
        }
    }
}

fun ratingTitle(ratingState: Int): String {
    return when (ratingState) {
        1 -> {
            "Oops!"
        }

        2 -> {
            "Sorry"
        }

        3 -> {
            "Average"
        }

        4 -> {
            "Good"
        }

        5 -> {
            "Excellent"
        }

        else -> {
            "Thanks for your support!"
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    ratingState: Int,
    onRatingStateChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .padding(8.dp)
    ) {
        for (i in 1..5) {
            Image(
                painter = painterResource(id = if (i <= ratingState) R.drawable.filled_star else R.drawable.unfilled_star),
                contentDescription = "",
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                onRatingStateChanged(i)
                            }
                        }
                        true
                    }
            )

        }
    }
}