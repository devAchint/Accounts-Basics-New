package com.techuntried.accountsbasics2.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.SubjectWithProgressModel
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.ProgressTrackColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.utils.debouncedClickable

@Composable
fun HomeContent(
    homeUiState: HomeUiState.Success,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onSectionMoreClick: (section: String) -> Unit,
    onSuggestClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Continue Learning Card
        if (homeUiState.lastPlayedSubject != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SectionHeaderTitle(title = "Continue Learning")
                    Spacer(modifier = Modifier.height(10.dp))
                    HomeRecentItemCard(
                        subjectWithProgressModel = homeUiState.lastPlayedSubject,
                        onClick = {
                            onQuizCategoryClick(
                                homeUiState.lastPlayedSubject.subject.subjectId,
                                homeUiState.lastPlayedSubject.subject.name,
                                homeUiState.lastPlayedSubject.subject.showTopics
                            )
                        }
                    )
                }
            }
        }

        // Subject Sections
        if (!homeUiState.sectionCategories.isNullOrEmpty()) {
            homeUiState.sectionCategories.forEachIndexed { index, section ->
                item {
                    SectionHeaderWithLink(
                        title = section.title,
                        onSeeAllClick = { onSectionMoreClick(section.title) }
                    )
                }

                items(section.subjects) { subject ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HomeSubjectItemCard(
                            modifier = Modifier.fillMaxWidth(),
                            subjectModel = subject,
                            onClick = {
                                onQuizCategoryClick(
                                    subject.subjectId,
                                    subject.name,
                                    subject.showTopics
                                )
                            }
                        )
                    }
                }
            }
        }

        // Bottom Suggestion Tip
        item {
            Spacer(modifier = Modifier.height(10.dp))
            if (homeUiState.sectionCategories?.isNotEmpty() == true) {
                SuggestionTip(
                    modifier = Modifier
                        .padding(top = 28.dp),
                ) {
                    onSuggestClick()
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MainText
    )
}

@Composable
private fun SectionHeaderWithLink(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MainText
        )
    }
}

@Composable
fun HomeRecentItemCard(
    modifier: Modifier = Modifier,
    subjectWithProgressModel: SubjectWithProgressModel,
    onClick: () -> Unit
) {
    val category = subjectWithProgressModel.subject
    val progress = subjectWithProgressModel.progress
    val bgColor = try {
        category.bgColor?.let { Color(category.bgColor.toColorInt()) } ?: Color.White
    } catch (e: Exception) {
        Color.White
    }

    val isMastered = progress.progressPercentage >= 100f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.5.dp, BorderColor, RoundedCornerShape(22.dp))
            .debouncedClickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Subject Icon Box
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = category.imageUrl,
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.image_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MainText,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${progress.chaptersCompleted} of ${category.chapters} levels completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Chip
                    if (isMastered) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFECFDF5))
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Canvas(modifier = Modifier.size(10.dp)) {
                                    val path = Path().apply {
                                        moveTo(size.width * 0.2f, size.height * 0.5f)
                                        lineTo(size.width * 0.45f, size.height * 0.75f)
                                        lineTo(size.width * 0.85f, size.height * 0.25f)
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color(0xFF16A34A),
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }
                                Text(
                                    text = "Mastered",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "In Progress",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(9.dp)
                        .clip(CircleShape)
                        .background(ProgressTrackColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                (progress.progressPercentage / 100f).coerceIn(0f, 1f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                )
                            )
                    )
                }

                Text(
                    text = "${progress.progressPercentage.toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    color = MainText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Resume Button

            CommonButton(
                text = if (isMastered) "Review ${category.name}" else "Resume Quiz",
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFF111214),
                contentColor = Color.White
            ){
                onClick()
            }
        }
    }
}