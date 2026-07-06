package com.techuntried.accountsbasics2.ui.chooseGrade

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.commons.CommonButton
import com.techuntried.accountsbasics2.ui.commons.CommonToolbar
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.AppIcons
import com.techuntried.accountsbasics2.utils.Grade
import com.techuntried.accountsbasics2.utils.Spacer

@Composable
fun ChooseGradeScreenRoot(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    openNotificationRequest: () -> Unit,
    openHome: () -> Unit,
    isFirstTime: Boolean = false
) {
    val context = LocalContext.current
    val viewModel: ChooseGradeViewModel = hiltViewModel()
    val chooseGradeUiState = viewModel.chooseGradeUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.logEvent(LogEventType.ScreenVisit("Choose Grade"))
    }

    LaunchedEffect(chooseGradeUiState.message) {
        chooseGradeUiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMsg()
        }
    }

    LaunchedEffect(chooseGradeUiState.gradeSaved) {
        if (chooseGradeUiState.gradeSaved) {
            if (isFirstTime) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    openNotificationRequest()
                }else{
                    openHome()
                }
            } else {
                navigateBack()
            }
            viewModel.clearNavigation()
        }
    }

    ChooseGradeScreen(
        chooseGradeUiState = chooseGradeUiState,
        saveGrade = viewModel::saveUserGrade,
        isFirstTime = isFirstTime,
        onBack = {
            navigateBack()
        }
    )
}


@Composable
private fun ChooseGradeScreen(
    chooseGradeUiState: ChooseGradeUiState,
    saveGrade: (Grade) -> Unit = {},
    isFirstTime: Boolean = false,
    onBack: () -> Unit
) {
    var selectedGrade by remember(chooseGradeUiState.currentGrade) {
        mutableStateOf(
            chooseGradeUiState.currentGrade
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        CommonToolbar(
            title = "Choose Your Grade",
            isNavigationIcon = true,
            navigationIcon = AppIcons.Back,
            onNavigationClick = onBack
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Select your current grade to get personalized content",
                    color = MainText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(16.dp)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Grade.entries) { grade ->
                        GradeChooserItem(
                            grade = grade,
                            selected = grade == selectedGrade,
                            onClick = {
                                selectedGrade = grade
                            }
                        )
                    }
                }

                CommonButton(text = "Continue", enabled = selectedGrade != null) {
                    selectedGrade?.let {
                        saveGrade(it)
                    }
                }
            }

            if (chooseGradeUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }


}

@Composable
private fun GradeChooserItem(
    modifier: Modifier = Modifier,
    grade: Grade,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundBg = if (selected) Color(0xff57cc02) else Color.White
    val borderColor = if (selected) Color(0xff57cc02) else BorderColor
    val textColor = if (selected) Color.White else MainText
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = grade.gradeName,
            color = textColor,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge
        )

        Icon(
            painter = painterResource(R.drawable.tick),
            contentDescription = "",
            modifier = Modifier
                .size(28.dp)
                .alpha(if (selected) 1f else 0f),
            tint = Color.Unspecified
        )

    }
}

