package com.techuntried.accountsbasics2.ui.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.theme.AverageAccuracyColor
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.ExcellentAccuracyColor
import com.techuntried.accountsbasics2.ui.theme.GoodAccuracyColor
import com.techuntried.accountsbasics2.ui.theme.LowAccuracyColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.ProgressColor
import com.techuntried.accountsbasics2.ui.theme.ProgressTrackColor
import com.techuntried.accountsbasics2.ui.theme.RubikMedium
import com.techuntried.accountsbasics2.ui.theme.RubikRegular
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.Spacer
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun ProgressScreenRoot(
    modifier: Modifier = Modifier,
    openQuizSection: () -> Unit,
    openGameLevel: (levelArgs: LevelArgs) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProgressViewModel = hiltViewModel()
    val progressUiState = viewModel.progressUiState.collectAsStateWithLifecycle().value


    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("Progress"))
    }

    ProgressScreen(
        progressUiState = progressUiState,
        updateSort = viewModel::updateSort,
        play = openQuizSection,
        openGameLevel = openGameLevel
    )
}


@Composable
fun ProgressScreen(
    progressUiState: ProgressUiState,
    play: () -> Unit = {},
    updateSort: (sort: ProgressSortOption) -> Unit = {},
    openGameLevel: (levelArgs: LevelArgs) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        CommonToolbar(
            title = stringResource(R.string.progress),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            when (progressUiState) {
                is ProgressUiState.Error -> {
                    ErrorMessageView(
                        modifier = Modifier.align(Alignment.Center),
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(progressUiState.message),
                        description = getErrorMessageDescription(progressUiState.message),
                        actionButton = null,
                    )
                }

                ProgressUiState.Loading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                    )
                }

                is ProgressUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (progressUiState.isEmpty()) {
                            ErrorMessageView(
                                modifier = Modifier.align(Alignment.Center),
                                icon = AppIcons.EmptyProgressIcon,
                                errorTitle = stringResource(R.string.empty_progress_title),
                                description = stringResource(R.string.empty_progress_description),
                                actionButton = stringResource(R.string.play_now),
                                action = play
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        AccuracyCard(
                                            accuracy = progressUiState.accuracy.toFloat()
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            StatsCardSmall(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(),
                                                value = progressUiState.questionsAttempted.toString(),
                                                label = if (progressUiState.questionsAttempted == 1)
                                                    stringResource(R.string.question_attempted)
                                                else stringResource(R.string.questions_attempted),
                                                icon = AppIcons.QuestionAttempted
                                            )
                                            StatsCardSmall(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(),
                                                value = progressUiState.categoriesPlayed.toString(),
                                                label = if (progressUiState.categoriesPlayed == 1)
                                                    stringResource(R.string.category_played)
                                                else stringResource(R.string.categories_played),
                                                icon = AppIcons.LevelsCompleted
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.category_performance),
                                                color = Color.Black,
                                                maxLines = 1,
                                                style = MaterialTheme.typography.headlineSmall,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .weight(1f)
                                            )

                                            SortSelector(
                                                selectedSort = progressUiState.selectedSortOption,
                                                onSortChange = updateSort
                                            )
                                        }
                                    }
                                }

                                items(progressUiState.progressList) {
                                    CategoryProgressCard(categoryWithProgress = it) {
                                        openGameLevel(
                                            LevelArgs(
                                                categoryId = it.category.categoryId,
                                                categoryName = it.category.categoryName,
                                                showTopic = it.category.showTopics
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AccuracyCard(
    modifier: Modifier = Modifier,
    accuracy: Float = 0f
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.overall_accuracy),
                    color = MainText,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$accuracy%",
                    color = accuracy.accuracyColor(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 44.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = accuracy.accuracyMessage(),
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            CircleProgressWithIcon(
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp),
                progressColor = accuracy.accuracyColor(),
                backgroundColor = ProgressTrackColor,
                percentage = accuracy
            )
        }
    }
}

@Composable
fun CircleProgressWithIcon(
    modifier: Modifier = Modifier,
    percentage: Float = 50f,
    strokeWidth: Dp = 8.dp,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val sweepAngle = 360 * (percentage.coerceIn(0f, 100f) / 100f)

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }


        Icon(
            painter = painterResource(AppIcons.Progress),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )

    }
}


@Composable
fun CategoryProgressCard(
    categoryWithProgress: CategoryWithProgressModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val bgColor = try {
        if (!categoryWithProgress.category.bgColor.isNullOrEmpty()) Color(categoryWithProgress.category.bgColor.toColorInt()) else Color.White
    } catch (e: Exception) {
        Color.White
    }

    val density = LocalDensity.current
    val imageSizePx = with(density) { 80.dp.roundToPx() }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .debouncedClickable { onClick() }
            .padding(16.dp),
    ) {

        // TOP ROW
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            // LEFT CONTENT
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                // ICON BOX
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(categoryWithProgress.category.imageUrl)
                            .size(imageSizePx) // 👈 density-aware downsampling
                            .scale(Scale.FILL) // matches ContentScale.Crop
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.image_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = categoryWithProgress.category.categoryName,
                        color = MainText,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MainText,
                                        fontFamily = RubikMedium
                                    )
                                ) {
                                    append("${categoryWithProgress.progress.levelsPlayed}/${categoryWithProgress.category.chapters}")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = SecondaryText,
                                        fontFamily = RubikRegular
                                    )
                                ) {
                                    append(" levels")
                                }
                            },
                            style = MaterialTheme.typography.labelMedium
                        )


                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${categoryWithProgress.progress.accuracy.toInt()}% accuracy",
                            style = MaterialTheme.typography.labelMedium,
                            color = categoryWithProgress.progress.accuracy.accuracyColor()
                        )
                    }
                    Spacer(4.dp)
                    Text(
                        text = "Grade ${categoryWithProgress.category.course}",
                        color = SecondaryText,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            // RIGHT PERCENT
            Text(
                text = "${categoryWithProgress.progress.progressPercentage.toInt()}%",
                color = MainText,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        val animatedProgress by animateFloatAsState(
            targetValue = (categoryWithProgress.progress.progressPercentage / 100).toFloat(),
            animationSpec = tween(durationMillis = 500) // adjust speed here
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ProgressTrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ProgressColor)
            )
        }

    }
}

@Composable
fun SortSelector(
    selectedSort: ProgressSortOption,
    onSortChange: (ProgressSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val allSorts = ProgressSortOption.entries

    Box(modifier) {

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable { isExpanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedSort.title,
                fontSize = 14.sp,
                color = MainText,
                style = MaterialTheme.typography.labelMedium
            )

            Icon(
                painter = painterResource(R.drawable.expand_down_icon),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(Color.White)
        ) {

            allSorts.forEach { sort ->
                val isSelected = sort == selectedSort

                DropdownMenuItem(
                    text = {
                        Text(
                            sort.title,
                            color = MainText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    onClick = {
                        onSortChange(sort)
                        isExpanded = false
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(AppIcons.Check),
                                tint = Color.Black,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun StatsCardSmall(
    value: String,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Color.White
            )
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ICON
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // TEXT
            Column {
                Text(
                    text = value,
                    color = MainText,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = label,
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatsCardLarge(
    value: String,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Color.White
            )
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {

            // ICON
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // TEXT
            Column {
                Text(
                    text = value.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = label,
                    color = Color.Black.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}


fun Float.accuracyColor(): Color {
    return when {
        this < 40f -> LowAccuracyColor
        this < 60f -> AverageAccuracyColor
        this < 80f -> GoodAccuracyColor
        else -> ExcellentAccuracyColor
    }
}

private val lowAccuracyMessages = listOf(
    "Don't worry — keep practicing 🌱",
    "Everyone starts somewhere 💡",
    "Practice makes progress 📘",
    "Learning takes time — keep going!"
)

private val averageAccuracyMessages = listOf(
    "You're improving! Keep going 💪",
    "Nice progress — stay consistent 👍",
    "You're on the right track 🚀",
    "Keep practicing to level up!"
)

private val goodAccuracyMessages = listOf(
    "Good job! Aim for 80%+ 🎯",
    "Strong performance — keep pushing 🔥",
    "You're doing well — almost there!",
    "Great focus, keep it up 💪"
)

private val excellentAccuracyMessages = listOf(
    "Excellent accuracy! 🔥",
    "Outstanding work — impressive! 🌟",
    "Quiz master in action 🧠",
    "Nearly perfect — amazing job! 🚀"
)

fun Float.accuracyMessage(): String {
    return when {
        this < 40f -> lowAccuracyMessages.random()
        this < 60f -> averageAccuracyMessages.random()
        this < 80f -> goodAccuracyMessages.random()
        else -> excellentAccuracyMessages.random()
    }
}
