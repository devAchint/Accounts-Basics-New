package com.techuntried.accountsbasics2.ui.level

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.ads.BannerAdCard
import com.techuntried.accountsbasics2.domain.model.level.LevelState
import com.techuntried.accountsbasics2.ui.home.SuggestionTip
import com.techuntried.accountsbasics2.usecases.LogEventType
import kotlinx.coroutines.launch

@Composable
fun LevelContent(
    modifier: Modifier = Modifier,
    levelUiState: LevelUiState.Success,
    showTopics: Boolean,
    bannerAdUnit: String?,
    openRules: (levelId: Int) -> Unit,
    showSuggestionSheet: () -> Unit,
    showLevelLockedDialog: () -> Unit,
    logEvent: (LogEventType) -> Unit,
    showLevelLockedSheet: (levelId: Int) -> Unit
) {
    val listState = rememberLazyListState()

    val targetIndex = remember(levelUiState.gameLevels) {
        val unlocked = levelUiState.gameLevels.indexOfLast {
            it.levelState == LevelState.Unlocked
        }
        if (unlocked != -1) unlocked
        else levelUiState.gameLevels.indexOfLast {
            it.levelState == LevelState.Completed
        }
    }

    val arrowDirectionDown by remember(listState, targetIndex) {
        derivedStateOf {
            if (targetIndex == -1) return@derivedStateOf false

            listState.firstVisibleItemIndex < targetIndex
        }
    }


    val isTargetVisible by remember(listState, targetIndex) {
        derivedStateOf {
            if (targetIndex == -1) return@derivedStateOf false

            listState.layoutInfo.visibleItemsInfo
                .any { it.index == targetIndex }
        }
    }


    LaunchedEffect(targetIndex) {
        if (targetIndex == -1) return@LaunchedEffect

        if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return@LaunchedEffect

        val visibleItems = listState.layoutInfo.visibleItemsInfo
        val firstVisible = visibleItems.first().index
        val lastVisible = visibleItems.last().index

        if (targetIndex == firstVisible) return@LaunchedEffect

        val jumpIndex = (targetIndex - 10).coerceAtLeast(0)
        listState.scrollToItem(jumpIndex)
        listState.animateScrollToItem(targetIndex)
    }


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
                itemsIndexed(levelUiState.gameLevels) { index, level ->
                    GameLevelCard(
                        level = level,
                        isFirst = index == 0,
                        isLast = index == levelUiState.gameLevels.lastIndex,
                        showTopics = showTopics,
                        onClick = {
                            if (level.levelState == LevelState.Locked) {
                                val levelsUnlocked =
                                    levelUiState.levelsCompleted + 1
                                if (level.levelId <= levelsUnlocked + 2) {
                                    showLevelLockedSheet(level.levelId)
                                } else {
                                    showLevelLockedDialog()
                                }
                            } else {
                                openRules(level.levelId)
                            }
                        }
                    )
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

            ScrollButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isTargetVisible = isTargetVisible,
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }
                },
                arrowDirectionDown = arrowDirectionDown
            )
        }
        bannerAdUnit?.let {
            BannerAdCard(bannerAdUnit = it, logEvent = logEvent)
        }
    }
}