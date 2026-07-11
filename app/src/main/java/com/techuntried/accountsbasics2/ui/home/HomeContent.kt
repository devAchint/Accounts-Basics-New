package com.techuntried.accountsbasics2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.ProgressColor
import com.techuntried.accountsbasics2.ui.theme.ProgressTrackColor
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.utils.Grade
import com.techuntried.accountsbasics2.utils.Spacer
import com.techuntried.accountsbasics2.utils.debouncedClickable

@Composable
fun HomeContent(
    homeUiState: HomeUiState.Success,
    openChooseGrade: () -> Unit,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onSectionMoreClick: (section: String) -> Unit,
    onSuggestClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item(span = { GridItemSpan(2) }) {
            YourGradeCard(
                grade = homeUiState.userGrade ?: Grade.GRADE_5, onClick = openChooseGrade
            )
        }

        if (homeUiState.lastPlayedCategory != null) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Continue Learning",
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(10.dp)
                    HomeRecentItemCard(
                        categoryWithProgressModel = homeUiState.lastPlayedCategory
                    ) {
                        onQuizCategoryClick(
                            homeUiState.lastPlayedCategory.category.categoryId,
                            homeUiState.lastPlayedCategory.category.categoryName,
                            homeUiState.lastPlayedCategory.category.showTopics
                        )
                    }
                }

            }
        }
        if (!homeUiState.sectionCategories.isNullOrEmpty()) {
            items(homeUiState.sectionCategories, span = { GridItemSpan(2) }) {
                QuizSectionCard(
                    modifier = Modifier.padding(bottom = 10.dp),
                    section = it,
                    onQuizCategoryClick = onQuizCategoryClick,
                    onAllCategoriesClick = {
                        onSectionMoreClick(it.title)
                    }
                )
            }
        }

        item(span = { GridItemSpan(2) }) {
            if (homeUiState.sectionCategories?.isNotEmpty() == true) {
                SuggestionTip(
                    modifier = Modifier
                        .padding(top = 16.dp),
                ) {
                    onSuggestClick()
                }
            }
        }
    }
}

@Composable
fun HomeRecentItemCard(
    modifier: Modifier = Modifier,
    categoryWithProgressModel: CategoryWithProgressModel,
    onClick: () -> Unit
) {
    val category = categoryWithProgressModel.category
    val progress = categoryWithProgressModel.progress
    val bgColor = category.bgColor?.let { Color(category.bgColor.toColorInt()) } ?: Color.White

    Column(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(Color.White)
            .debouncedClickable { onClick() }
            .padding(12.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = categoryWithProgressModel.category.imageUrl,
                placeholder = painterResource(R.drawable.image_placeholder),
                error = painterResource(R.drawable.image_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = category.categoryName,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (progress.levelsPlayed == 1) {
                        "${progress.levelsPlayed} Level Completed"
                    } else {
                        "${progress.levelsPlayed} Levels Completed"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText,
                    maxLines = 1
                )
                Spacer(4.dp)
                Text(
                    text = "Grade ${category.course}",
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ProgressTrackColor) // Track
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(
                            (progress.progressPercentage / 100f).coerceIn(0f, 1f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(ProgressColor)
                )
            }
            Spacer(8.dp)
            Text(
                text = "${progress.progressPercentage.toInt()}%",
                maxLines = 1,
                color = MainText,
                style = MaterialTheme.typography.titleSmall
            )
        }

    }
}
