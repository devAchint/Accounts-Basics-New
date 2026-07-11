package com.techuntried.accountsbasics2.ui.searchData

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonCircularProgress
import com.techuntried.accountsbasics2.ui.commons.ErrorMessageView
import com.techuntried.accountsbasics2.ui.home.HomeSubjectItemCard
import com.techuntried.accountsbasics2.ui.navigation.LevelArgs
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.InputHintColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.getErrorMessageDescription
import com.techuntried.accountsbasics2.utils.getErrorMessageTitle

@Composable
fun SearchDataScreenRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    openGameLevel: (LevelArgs) -> Unit
) {
    val context = LocalContext.current
    val viewModel: SearchDataViewModel = hiltViewModel()
    val searchDataUiState = viewModel.searchDataUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("SearchData"))
    }


    LaunchedEffect(searchDataUiState.message) {
        searchDataUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    SearchDataScreen(
        searchDataUiState = searchDataUiState,
        onSearch = viewModel::onSearch,
        updateFocus = viewModel::updateFocus,
        clearSearch = viewModel::clearSearch,
        updateSearchQuery = viewModel::updateQuery,
        onBack = onBack,
        openGameLevel = openGameLevel
    )
}


@Composable
fun SearchDataScreen(
    searchDataUiState: SearchDataUiState,
    updateFocus: (Boolean) -> Unit = {},
    openGameLevel: (LevelArgs) -> Unit,
    refresh: () -> Unit = {},
    updateSearchQuery: (TextFieldValue) -> Unit = {},
    onSearch: (TextFieldValue) -> Unit = {},
    clearSearch: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchDataUiState) {
        when (searchDataUiState) {
            is SearchDataUiState.Success -> {
                if (searchDataUiState.hasFocus) {
                    focusRequester.requestFocus()
                } else {
                    focusManager.clearFocus()
                }
            }

            else -> focusManager.clearFocus()
        }
    }


    BackHandler {
        if (searchDataUiState is SearchDataUiState.Success && (!searchDataUiState.searchResults.isNullOrEmpty() && searchDataUiState.hasFocus)){
            updateFocus(false)
        }else{
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            val hasFocus =
                searchDataUiState is SearchDataUiState.Success &&
                        searchDataUiState.hasFocus

            val searchQuery =
                if (searchDataUiState is SearchDataUiState.Success)
                    searchDataUiState.searchQuery
                else
                    TextFieldValue("")

            SimpleSearchBar(
                modifier = Modifier.fillMaxWidth(),
                hasFocus = hasFocus,
                focusRequester = focusRequester,
                updateFocus = updateFocus,
                searchQuery = searchQuery,
                updateSearchQuery = updateSearchQuery,
                clearSearch = clearSearch,
                onBack = {
                    if (searchDataUiState is SearchDataUiState.Success && (!searchDataUiState.searchResults.isNullOrEmpty() && searchDataUiState.hasFocus)){
                        updateFocus(false)
                    }else{
                        onBack()
                    }
                },
                onSearch = onSearch
            )

            HorizontalDivider(color = BorderColor)

            // 👇 Only body changes
            when (searchDataUiState) {
                SearchDataUiState.EmptyQuery -> {
                    ErrorMessageView(
                        icon = R.drawable.empty_progress_icon,
                        errorTitle = "Please type something to search",
                        actionButton = "Start Typing",
                        action = { updateFocus(true) },
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }

                SearchDataUiState.InitialLoading -> {
                    CommonCircularProgress(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        color = Color.Black
                    )
                }

                is SearchDataUiState.Error -> {
                    ErrorMessageView(
                        icon = R.drawable.error_icon_1,
                        errorTitle = getErrorMessageTitle(searchDataUiState.message),
                        description = getErrorMessageDescription(searchDataUiState.message),
                        actionButton = "Try Again",
                        action = refresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }

                is SearchDataUiState.Success -> {
                    SearchContent(
                        state = searchDataUiState,
                        onSearch = onSearch,
                        onBack = onBack,
                        openGameLevel = openGameLevel,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchDataUiState.Success,
    onSearch: (TextFieldValue) -> Unit,
    onBack: () -> Unit,
    openGameLevel: (LevelArgs) -> Unit
) {
    if (state.hasFocus) {
        SearchHintsList(state, onSearch = onSearch)
    } else {
        SearchResults(state, onBack = onBack, openGameLevel = openGameLevel)
    }
}


@Composable
fun SearchHintsList(state: SearchDataUiState.Success, onSearch: (TextFieldValue) -> Unit) {
    LazyColumn {
        items(state.searchHints) { hint ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSearch(
                            TextFieldValue(
                                text = hint.substringBefore("•").trim(),
                                selection = TextRange(hint.length)
                            )
                        )
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = hint,
                    color = Color.Black,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp)
                )
            }

        }
    }
}

@Composable
fun SearchResults(
    state: SearchDataUiState.Success,
    onBack: () -> Unit,
    openGameLevel: (LevelArgs) -> Unit
) {
    if (state.searchResults != null)
        if (state.searchResultsEmpty()) {
            ErrorMessageView(
                icon = R.drawable.empty_search_icon,
                errorTitle = "No results found",
                description = "Try searching for something else or browse popular quizzes.",
                actionButton = "Browse Quizzes",
                action = onBack,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
            ) {
                items(state.searchResults) {
                    HomeSubjectItemCard(subjectModel = it) {
                        openGameLevel(
                            LevelArgs(
                                categoryId = it.categoryId,
                                categoryName = it.categoryName,
                                showTopic = it.showTopics
                            )
                        )
                    }
                }
            }
        }
}

@Composable
fun SimpleSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: TextFieldValue,
    focusRequester: FocusRequester,
    hasFocus: Boolean,
    updateFocus: (Boolean) -> Unit,
    updateSearchQuery: (TextFieldValue) -> Unit,
    onSearch: (TextFieldValue) -> Unit,
    clearSearch: () -> Unit = {},
    onBack: () -> Unit = {},
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(end = 16.dp, start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.back_icon_v1),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(36.dp)
                    .padding(6.dp)
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            if (searchQuery.text.isEmpty()) {
                Text(
                    text = "Search Subjects & Topics",
                    color = InputHintColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                )
            }
            BasicTextField(
                value = searchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged({ updateFocus(it.hasFocus) }),
                onValueChange = {
                    val newText = it.text.take(50)
                    updateSearchQuery(it.copy(text = newText))
                },
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(searchQuery.copy(selection = TextRange(searchQuery.text.length)))
                    }
                ),
                cursorBrush = SolidColor(Color.Black)
            )
        }
        if (hasFocus && searchQuery.text.isNotEmpty()) {
            IconButton(
                onClick = clearSearch
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_icon),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                )
            }
        } else if (!hasFocus && searchQuery.text.isNotEmpty()) {
            IconButton(
                onClick = { updateFocus(true) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                )
            }
        }
    }
}
//@Preview
//@Composable
//fun SearchDataScreenPreview() {
//    SearchDataScreen(SearchDataUiState())
//}

