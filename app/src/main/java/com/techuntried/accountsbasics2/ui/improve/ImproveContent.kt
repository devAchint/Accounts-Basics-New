package com.techuntried.accountsbasics2.ui.improve

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.ads.BannerAdCard
import com.techuntried.accountsbasics2.domain.model.entities.WrongQuestionEntity
import com.techuntried.accountsbasics2.ui.home.SuggestionTip
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ImproveContent(
    modifier: Modifier = Modifier,
    improveUiState: ImproveUiState.Success,
    bannerAdUnit: String?,
    showSuggestionSheet: () -> Unit,
    logEvent: (LogEventType) -> Unit,
) {
    val listState = rememberLazyListState()


    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val groupedLevels = remember(improveUiState.wrongQuestions) {
                improveUiState.wrongQuestions.groupBy { it.subjectId }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 60.dp
                ),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                groupedLevels.forEach { (module, levels) ->

//                    item {
//                        ModuleCard(
//                            title = "Module $module",
//                            subtitle = "${levels.count { it.type == "learn" }} Chapters"
//                        )
//                    }

                    itemsIndexed(levels) { index, question ->
                        WrongQuestionItem(
                            questionNumber = index+1,
                            question = question
                        )
                    }
                }
                item {

                    SuggestionTip(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        text = "Have a suggestion?"
                    ) {
                        showSuggestionSheet()
                    }

                }
            }
        }
        bannerAdUnit?.let {
            BannerAdCard(bannerAdUnit = it, logEvent = logEvent)
        }
    }
}

@Composable
fun WrongQuestionItem(questionNumber: Int, question: WrongQuestionEntity) {
    val correctOptionColor = Color(0xFF22C55E)
    val wrongOptionColor = Color(0xFFEF4444)
    val titleBgColor = wrongOptionColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Q$questionNumber",
                style = MaterialTheme.typography.titleSmall,
                color = MainText
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text =  "Wrong",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(titleBgColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text(
            text = question.questionText,
            color = MainText,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = buildAnnotatedString {
                append("Your answer: ")
                withStyle(style = SpanStyle(color = titleBgColor)) {
                    append("")
                }
            },
            color = SecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}