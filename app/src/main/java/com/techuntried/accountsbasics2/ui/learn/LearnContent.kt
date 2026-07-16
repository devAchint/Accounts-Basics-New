package com.techuntried.accountsbasics2.ui.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.content.ContentItem
import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.SelectColor
import com.techuntried.accountsbasics2.utils.captureScreenshot
import com.techuntried.accountsbasics2.utils.rememberDebouncedClick
import com.techuntried.accountsbasics2.utils.saveScreenshotToInternalStorage
import com.techuntried.accountsbasics2.utils.shareScreenshot

@Composable
fun LearnContentPage(question: LearnContentModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
//        Text(
//            text = "Question ${question.questionNumber}",
//            style = MaterialTheme.typography.titleMedium
//        )
        //Spacer(modifier = Modifier.height(8.dp))
        question.content.forEach { item ->
            when (item) {
                is ContentItem.Heading -> HeadingItem(item)
                is ContentItem.SubHeading -> SubHeadingItem(item)
                is ContentItem.Paragraph -> ParagraphItem(item)
                is ContentItem.BulletList -> BulletListItem(item)
                is ContentItem.Note -> NoteItem(item)
                is ContentItem.Tip -> TipItem(item)
                is ContentItem.Image -> ImageItem(item)
                is ContentItem.Table -> TableItem(item)
                is ContentItem.Quote -> QuoteItem(item)
                is ContentItem.Summary -> SummaryItem(item)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QuestionScreenTopBar(
//    onBackClick: () -> Unit,
//    onReportClick: (questionNumber: Int) -> Unit,
//    title: String,
//    questionNumber: Int,
//    sheetVisibility: (Boolean) -> Unit
//) {
//    val context = LocalContext.current
//    var showMenu by remember {
//        mutableStateOf(false)
//    }
//    val view = LocalView.current
//    TopAppBar(
//        title = {
//            Column(
//                modifier = Modifier.clickable(
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }) {
//                    sheetVisibility(
//                        true
//                    )
//                }) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "Question $questionNumber",
//                        maxLines = 1,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Icon(
//                        painter = painterResource(id = R.drawable.expand_down_icon),
//                        contentDescription = "Back",
//                        tint = Color.Black,
//                        modifier = Modifier
//                            .size(24.dp)
//
//                    )
//                }
//                Text(
//                    text = title,
//                    maxLines = 1,
//                    style = MaterialTheme.typography.labelMedium
//                )
//            }
//
//        },
//        navigationIcon = {
//            IconButton(onClick = rememberDebouncedClick {
//                onBackClick()
//            }) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = ""
//                )
//            }
//        },
//        actions = {
//            IconButton(onClick = { showMenu = !showMenu }) {
//                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
//            }
//            DropdownMenu(
//                expanded = showMenu,
//                onDismissRequest = { showMenu = false },
//                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
//            ) {
//                DropdownMenuItem(
//                    text = { Text(text = "Report") },
//                    onClick = {
//                        showMenu = false
//                        onReportClick(questionNumber)
//                    })
//                DropdownMenuItem(
//                    text = { Text(text = "Share screenshot") },
//                    onClick = {
//                        showMenu = false
//                        captureScreenshot(view)?.let { bitmap ->
//                            saveScreenshotToInternalStorage(bitmap, context)?.let { file ->
//                                shareScreenshot(file, context)
//                            }
//                        }
//                    })
//            }
//        }
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsSheet(
    sheetVisibility: (Boolean) -> Unit,
    currentIndex: Int,
    questions: List<QuestionModel>,
    onSheetItemClick: (questionIndex: Int) -> Unit,
    chapterName: String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            sheetVisibility(false)
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = chapterName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Questions ${questions.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f))
            {
                itemsIndexed(questions) { index, item ->
                    QuestionSheetItem(
                        question = item,
                        currentIndex == index
                    ) {
                        onSheetItemClick(index)
                        sheetVisibility(false)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
//            CircularButton(
//                modifier = Modifier
//                    .padding(top = 16.dp, bottom = 16.dp)
//                    .fillMaxWidth()
//                    .height(50.dp),
//                text = "Close"
//            ) {
//                sheetVisibility(false)
//            }
        }
    }
}

@Composable
fun QuestionSheetItem(
    question: QuestionModel,
    selectedQuestion: Boolean,
    onClick: (questionNumber: Int) -> Unit
) {
    val borderColor = if (selectedQuestion) {
        SelectColor
    } else {
        CardColor
    }
    val backgroundColor = if (selectedQuestion) {
        SelectColor
    } else {
        Color.White
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick(question.questionId) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Q${question.questionId}. ")
        Text(text = question.questionText, minLines = 2, maxLines = 2)
    }
}