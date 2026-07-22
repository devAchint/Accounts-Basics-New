package com.techuntried.accountsbasics2.ui.home

import android.R.attr.textColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.utils.debouncedClickable

@Composable
fun HomeSubjectItemCard(
    modifier: Modifier = Modifier,
    subjectModel: SubjectModel,
    onClick: () -> Unit
) {
    val bgColor = try {
        subjectModel.bgColor?.let { Color(it.toColorInt()) } ?: Color.White
    } catch (e: Exception) {
        Color.White
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(bgColor)
            .border(1.dp, BorderColor.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
            .debouncedClickable { onClick() }
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = subjectModel.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MainText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Not started · ${subjectModel.chapters} levels",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }

                // Icon Square
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = subjectModel.imageUrl,
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.image_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            val tagText = subjectModel.tag ?: if (subjectModel.featured) "Exam Focus" else "Core"
            Spacer(modifier = Modifier.height(14.dp))

            // Dark Tag Chip
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = tagText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}