package com.techuntried.accountsbasics2.ui.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.techuntried.accountsbasics2.domain.model.content.ContentItem
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.CardColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText

@Composable
fun HeadingItem(item: ContentItem.Heading) {
    Text(
        text = item.text,
        style = MaterialTheme.typography.headlineSmall,
        color = MainText
    )
}

@Composable
fun SubHeadingItem(item: ContentItem.SubHeading) {
    Text(
        text = item.text,
        style = MaterialTheme.typography.titleMedium,
        color = MainText
    )
}

@Composable
fun ParagraphItem(item: ContentItem.Paragraph) {
    Text(
        text = item.text,
        style = MaterialTheme.typography.bodyLarge,
        color = MainText
    )
}

@Composable
fun BulletListItem(item: ContentItem.BulletList) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item.items.forEach {
            Row {
                Text("• ")
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun NoteItem(item: ContentItem.Note) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFFFF8E1),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text("📝 ")
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TipItem(item: ContentItem.Tip) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFE8F5E9),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text("💡 ")
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun QuoteItem(item: ContentItem.Quote) {
    Text(
        text = "\"${item.text}\"",
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic,
        color = SecondaryText,
        modifier = Modifier
            .border(
                2.dp,
                BorderColor,
                RoundedCornerShape(8.dp)
            )
            .padding(start = 12.dp)
    )
}
@Composable
fun SummaryItem(item: ContentItem.Summary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                CardColor,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ImageItem(item: ContentItem.Image) {

    AsyncImage(
        model = item.imageUrl,
        contentDescription = item.caption,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    )

    item.caption?.let {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = it,
            style = MaterialTheme.typography.labelMedium,
            color = SecondaryText
        )
    }
}

@Composable
fun TableItem(item: ContentItem.Table) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor)
    ) {

        Row(
            modifier = Modifier.background(CardColor)
        ) {
            item.headers.forEach {
                Text(
                    text = it,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        item.rows.forEach { row ->

            HorizontalDivider()

            Row {

                row.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}