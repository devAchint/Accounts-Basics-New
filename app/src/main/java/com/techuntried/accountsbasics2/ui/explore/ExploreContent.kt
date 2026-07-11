package com.techuntried.accountsbasics2.ui.explore

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.home.HomeSubjectItemCard
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.AppShapes

@Composable
fun ExploreContent(
    modifier: Modifier = Modifier,
    exploreUiState: ExploreUiState.Success,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onMoreCategoriesClick: (section: String,grade:List<Int>) -> Unit,
    updateSection: (String) -> Unit,
    reportIssue:()->Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            SectionsCard(
                selectedSection = exploreUiState.selectedSection,
                sections = exploreUiState.sections
            ) {
                updateSection(it)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (exploreUiState.isEmpty()) {
                val isFiltersSelected = exploreUiState.selectedSection != "All" || exploreUiState.selectedGrades.isNotEmpty()
                val description = if (!isFiltersSelected){
                    "Something went wrong on our end — not yours."
                }else{
                    "No content available for the selected filters. Try a different grade or section."
                }
                ErrorMessageView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = AppIcons.EmptyProgressIcon,
                    errorTitle = "Content unavailable",
                    description = description,
                    actionButton = "Report Issue".takeIf { !isFiltersSelected },
                    action = {
                        if (!isFiltersSelected) reportIssue()
                    }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (exploreUiState.selectedSection == "All") {
                        items(exploreUiState.data, span = { GridItemSpan(2) }) {
                            ExploreCategoriesCard(
                                modifier = Modifier.padding(bottom = 10.dp),
                                exploreSection = it,
                                onQuizCategoryClick = onQuizCategoryClick,
                                onAllCategoriesClick = {
                                    onMoreCategoriesClick(
                                        it.title,
                                        exploreUiState.selectedGrades.toList()
                                    )
                                }
                            )
                        }
                    } else {
                        itemsIndexed(exploreUiState.data.first().categories) { index, item ->
                            val modifier = Modifier.padding(
                                end = if (index % 2 == 1) 16.dp else 0.dp,
                                start = if (index % 2 == 0) 16.dp else 0.dp
                            )
                            HomeSubjectItemCard(
                                modifier = modifier.fillMaxWidth(),
                                subjectModel = item,
                                onClick = {
                                    onQuizCategoryClick(
                                        item.categoryId,
                                        item.categoryName,
                                        item.showTopics
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreCategoriesCard(
    modifier: Modifier = Modifier,
    exploreSection: ExploreSectionModel,
    onQuizCategoryClick: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit,
    onAllCategoriesClick: () -> Unit
) {
    exploreSection.categories.let { categories ->
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exploreSection.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MainText
                )

                Row(
                    modifier = Modifier
                        .clip(AppShapes.Round20)
                        .background(CardColor)
                        .clickable { onAllCategoriesClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.titleSmall,
                        color = MainText
                    )

                    Icon(
                        painter = painterResource(R.drawable.right_arrow_icon),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories.take(6)) { category ->
                    HomeSubjectItemCard(
                        modifier = Modifier.width(180.dp),
                        subjectModel = category,
                        onClick = {
                            onQuizCategoryClick(
                                category.categoryId,
                                category.categoryName,
                                category.showTopics
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionsCard(
    modifier: Modifier = Modifier,
    selectedSection: String,
    sections: List<String>,
    onSectionClick: (section: String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sections) {
            SectionText(
                item = it,
                selected = selectedSection == it,
                onClick = {
                    onSectionClick(it)
                }
            )
        }
    }
}