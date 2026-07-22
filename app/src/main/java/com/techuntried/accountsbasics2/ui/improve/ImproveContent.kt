package com.techuntried.accountsbasics2.ui.improve

import ads_mobile_sdk.qu
import android.util.StatsLog.logEvent
import com.techuntried.accountsbasics2.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.ads.BannerAdCard
import com.techuntried.accountsbasics2.ui.home.SuggestionTip
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType

@Composable
fun ImproveContent(
    modifier: Modifier = Modifier,
    uiState: ImproveUiState.Success,
    bannerAdUnit: String?,
    onAction: (ImproveActions) -> Unit,
    showSuggestionSheet: () -> Unit,
    logEvent: (LogEventType) -> Unit,
    practiceAll: () -> Unit,
    practiceQuestion: (subjectId: Int, chapterId: Int, questionId: Int) -> Unit
) {
    val filteredMistakes = if (uiState.selectedSubject.equals(
            "All Subjects",
            ignoreCase = true
        ) || uiState.selectedSubject.equals("All", ignoreCase = true)
    ) {
        uiState.mistakeItems
    } else {
        uiState.mistakeItems.filter {
            it.subject.equals(
                uiState.selectedSubject,
                ignoreCase = true
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // Header Subtitle
                item {
                    SubtitleSection(toReviewCount = uiState.toReviewCount)
                }

                // Stats Strip Section with Intrinsic Max Height for strict card height consistency
                item {
                    StatsStripSection(
                        toReview = uiState.toReviewCount,
                        fixedThisWeek = uiState.fixedThisWeekCount,
                        subjectsAffected = uiState.subjectsAffectedCount
                    )
                }

                // Practice All CTA Banner
                item {
                    PracticeAllCtaCard(
                        totalQuestions = uiState.toReviewCount,
                        onPracticeAll = practiceAll
                    )
                }

                // Subject Filter Chips
                item {
                    FilterChipsSection(
                        subjects = uiState.subjects,
                        selectedSubject = uiState.selectedSubject,
                        onSubjectSelect = { onAction(ImproveActions.SelectSubject(it)) }
                    )
                }

                // Section Label
                item {
                    SectionLabel(text = "Recently missed")
                }

                // Question Mistake Cards
                items(filteredMistakes, key = { it.id }) { item ->
                    QuestionMistakeCard(
                        question = item,
                        onShowWhyClick = { onAction(ImproveActions.ShowExplanation(item)) },
                        onPracticeAgainClick = {
                            practiceQuestion(
                                item.subjectId,
                                item.chapterId,
                                item.questionId
                            )
                        }
                    )
                }

                // Suggestion Pill
                item {
                    Spacer(modifier = Modifier.height(10.dp))
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
private fun SubtitleSection(toReviewCount: Int) {
    Text(
        text = buildAnnotatedString {
            append("You have ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MainText)) {
                append("$toReviewCount mistakes")
            }
            append(" waiting to be mastered")
        },
        style = MaterialTheme.typography.bodyLarge,
        color = SecondaryText,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp)
    )
}

@Composable
private fun StatsStripSection(
    toReview: Int,
    fixedThisWeek: Int,
    subjectsAffected: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max) // Ensures equal heights across all cards
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = toReview.toString(),
            valueColor = Color(0xFFDC2626),
            label = "To review"
        )
        StatCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = fixedThisWeek.toString(),
            valueColor = MainText,
            label = "Fixed this week"
        )
        StatCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = subjectsAffected.toString(),
            valueColor = MainText,
            label = "Subjects affected"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    valueColor: Color,
    label: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFAFAFB))
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun PracticeAllCtaCard(
    totalQuestions: Int,
    onPracticeAll: () -> Unit
) {
    val estimatedTime = (totalQuestions * 3).coerceAtLeast(2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111214))
            .clickable { onPracticeAll() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Red play icon container
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDC2626)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play_icon_2),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Practice all mistakes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "$totalQuestions questions · ~$estimatedTime min",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Arrow button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "›",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111214),
                    modifier = Modifier.offset(y = (-1).dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChipsSection(
    subjects: List<SubjectChipData>,
    selectedSubject: String,
    onSubjectSelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subjects) { chip ->
            val isActive = chip.name.equals(selectedSubject, ignoreCase = true)
            val bgColor = if (isActive) Color(0xFF111214) else Color.White
            val borderColor = if (isActive) Color(0xFF111214) else BorderColor
            val textColor = if (isActive) Color.White else MainText

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bgColor)
                    .border(1.dp, borderColor, CircleShape)
                    .clickable { onSubjectSelect(chip.name) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(chip.name)
                        append(" ")
                        withStyle(style = SpanStyle(color = textColor.copy(alpha = 0.6f))) {
                            append(chip.count.toString())
                        }
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = SecondaryText,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun QuestionMistakeCard(
    question: MistakeItem,
    onShowWhyClick: () -> Unit,
    onPracticeAgainClick: () -> Unit
) {
    val correctOptionColor = Color(0xFF22C55E)
    val wrongOptionColor = Color(0xFFEF4444)
    val titleBgColor =
        if (question.isFixed) correctOptionColor else wrongOptionColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Card Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Q ${question.id.plus(1)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MainText
                    )

                    // Red Wrong Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(titleBgColor)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (question.isFixed) "Fixed" else "Wrong",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Subject Tag
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = question.subject,
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryText
                        )
                    }
                }

                Text(
                    text = question.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium,
                color = MainText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ONLY YOUR ANSWER
            AnswerRow(
                label = "Your answer",
                value = question.yourAnswer.toString()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Show Why Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .clickable { onShowWhyClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Show why",
                        style = MaterialTheme.typography.titleSmall,
                        color = MainText
                    )
                }

                // Practice Again Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111214))
                        .clickable { onPracticeAgainClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Practice again",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFEF2F2))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Red Icon Circle ('X')
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color(0xFFDC2626)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(9.dp)) {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626),
                letterSpacing = 0.3.sp
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MainText
            )
        }
    }
}

@Composable
private fun SuggestionPill(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { onClick() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Canvas(modifier = Modifier.size(16.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.5f, 0f)
                    cubicTo(
                        size.width * 0.2f, 0f,
                        0f, size.height * 0.25f,
                        0f, size.height * 0.5f
                    )
                    cubicTo(
                        0f, size.height * 0.7f,
                        size.width * 0.25f, size.height * 0.85f,
                        size.width * 0.35f, size.height * 0.95f
                    )
                    lineTo(size.width * 0.65f, size.height * 0.95f)
                    cubicTo(
                        size.width * 0.75f, size.height * 0.85f,
                        size.width, size.height * 0.7f,
                        size.width, size.height * 0.5f
                    )
                    cubicTo(
                        size.width, size.height * 0.25f,
                        size.width * 0.8f, 0f,
                        size.width * 0.5f, 0f
                    )
                }
                drawPath(
                    path = path,
                    color = Color(0xFF374151),
                    style = Stroke(width = 1.6.dp.toPx())
                )
            }

            Text(
                text = "Have a suggestion?",
                style = MaterialTheme.typography.titleSmall,
                color = MainText
            )
        }
    }
}