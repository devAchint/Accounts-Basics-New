package com.techuntried.accountsbasics2.ui.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.domain.model.content.ContentItem
import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.ui.commons.CommonButton

@Composable
fun LearnContentPage(
    question: LearnContentModel,
    isLast: Boolean,
    onFinishClicked: () -> Unit,
    onContinueClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

        CommonButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            text = if (isLast) "Finish" else "Continue"
        ) {
            if (isLast) onFinishClicked() else onContinueClicked()
        }

    }
}