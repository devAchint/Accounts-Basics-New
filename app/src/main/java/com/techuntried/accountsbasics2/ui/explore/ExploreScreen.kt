package com.techuntried.accountsbasics2.ui.explore

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.InputHintColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.AppShapes
import com.techuntried.accountsbasics2.utils.Grade
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun ExploreScreenRoot(
    modifier: Modifier = Modifier,
    openSearchData: (query: String) -> Unit,
    openGameLevel: (LevelArgs) -> Unit,
    onMoreCategoriesClick: (section: String,grade:List<Int>) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: ExploreViewModel = hiltViewModel()
    val exploreUiState = viewModel.exploreUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("Explore"))
    }

    LaunchedEffect(exploreUiState.message) {
        exploreUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    ExploreScreen(
        exploreUiState = exploreUiState,
        refresh = viewModel::fetchData,
        openSearchData = openSearchData,
        onMoreCategoriesClick = onMoreCategoriesClick,
        updateSection = viewModel::updateSection,
        updateGrades = viewModel::updateGrade,
        reportIssue = {
            viewModel.uploadSuggestion(comment = "Explore Screen didn't Load")
        },
        openGameLevel = { categoryId, categoryName, showTopic ->
            openGameLevel(
                LevelArgs(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    showTopic = showTopic
                )
            )
        }
    )
}


@Composable
fun ExploreScreen(
    exploreUiState: ExploreUiState,
    refresh: () -> Unit = {},
    openSearchData: (query: String) -> Unit = {},
    updateSection: (String) -> Unit,
    updateGrades: (Set<Int>) -> Unit,
    onMoreCategoriesClick: (section: String,grade:List<Int>) -> Unit = { _,_ -> },
    openGameLevel: (categoryId: Int, categoryName: String, showTopic: Boolean) -> Unit = { _, _, _ -> },
    reportIssue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (exploreUiState is ExploreUiState.Success) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DemoSearchBar(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { openSearchData("") }
                )

                MultiGradeSelector(
                    selectedGrades = exploreUiState.selectedGrades,
                    onSelectionChange = updateGrades,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            when (exploreUiState) {
                is ExploreUiState.Error -> {
                    ErrorMessageView(
                        modifier = Modifier.align(Alignment.Center),
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(exploreUiState.message),
                        description = getErrorMessageDescription(exploreUiState.message),
                        actionButton = "Try Again",
                        action = refresh
                    )
                }

                ExploreUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is ExploreUiState.Success -> {
                    ExploreContent(
                        exploreUiState = exploreUiState,
                        onQuizCategoryClick = openGameLevel,
                        onMoreCategoriesClick = onMoreCategoriesClick,
                        updateSection = updateSection,
                        reportIssue = reportIssue
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoSearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(30.dp))
            .debouncedClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(AppIcons.Search),
            contentDescription = "",
            tint = Color.Black,
            modifier = Modifier
                .size(36.dp)
                .padding(6.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            if (text.isEmpty()) {
                Text(
                    text = "Search Subjects & Topics",
                    color = InputHintColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun MultiGradeSelector(
    selectedGrades: Set<Int>,
    onSelectionChange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val allGrades = Grade.entries

    Box(modifier) {

        Icon(
            painter = painterResource(AppIcons.Filter),
            contentDescription = "Filter Grades",
            tint = Color.Black,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, BorderColor, CircleShape)
                .clickable { isExpanded = true }
                .padding(12.dp)
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(Color.White)
        ) {

            // 🔹 ALL toggle
            DropdownMenuItem(
                text = {
                    Text(
                        "All",
                        color = MainText,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                onClick = {
                    onSelectionChange(emptySet())
                },
                trailingIcon = {
                    if (selectedGrades.isEmpty()) {
                        Icon(
                            painter = painterResource(AppIcons.Check),
                            contentDescription = null,
                            tint = MainText
                        )
                    }
                }
            )

            HorizontalDivider(color = BorderColor)

            // 🔹 Individual grades
            allGrades.map { it.gradeNumber }.forEach { grade ->
                val isSelected = grade in selectedGrades

                DropdownMenuItem(
                    text = {
                        Text(
                            "Grade $grade",
                            color = MainText,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    onClick = {
                        val updated = selectedGrades.toMutableSet()
                        if (isSelected) updated.remove(grade)
                        else updated.add(grade)
                        onSelectionChange(updated)
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(AppIcons.Check),
                                contentDescription = null,
                                tint = MainText
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SectionText(
    modifier: Modifier = Modifier,
    item: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val textColor = if (selected) Color.White else MainText
    val bgColor = if (selected) MainText else Color.Transparent
    val borderColor = if (selected) MainText else BorderColor

    Row(
        modifier = Modifier
            .clip(AppShapes.Round20)
            .background(bgColor)
            .border(1.dp, borderColor, AppShapes.Round20)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )
    }
}
