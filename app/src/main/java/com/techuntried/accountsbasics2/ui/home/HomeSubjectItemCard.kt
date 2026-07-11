package com.techuntried.accountsbasics2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.SecondaryText
import com.techuntried.accountsbasics2.utils.debouncedClickable

@Composable
fun HomeSubjectItemCard(
    modifier: Modifier = Modifier, subjectModel: SubjectModel, onClick: () -> Unit
) {
    val bgColor = try {
        subjectModel.bgColor?.let { Color(it.toColorInt()) } ?: Color.White
    } catch (e: Exception) {
        Color.White
    }

    ConstraintLayout(
        modifier = modifier
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .debouncedClickable { onClick() }) {
        val (textRef,gradeRef, tagRef, imageRef) = createRefs()

        // Text at top/start
        Text(
            text = subjectModel.categoryName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MainText,
            modifier = Modifier.constrainAs(textRef) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = "Grade ${subjectModel.course}",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            color = SecondaryText,
            modifier = Modifier.constrainAs(gradeRef) {
                top.linkTo(textRef.bottom, margin = 8.dp)
                start.linkTo(textRef.start,)
            }
        )

        subjectModel.tag?.let {
            Text(
                text = subjectModel.tag,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0XFF333333))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .constrainAs(tagRef) {
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    }
            )
        }


        // Image at bottom/end with offset

        val density = LocalDensity.current
        val imageSizePx = remember(density) {
            with(density) { 120.dp.roundToPx() }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(subjectModel.imageUrl)
                .size(imageSizePx) // 👈 density-aware downsampling
                .scale(Scale.FILL) // matches ContentScale.Crop
                .allowHardware(true).memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED).build(),
            placeholder = painterResource(R.drawable.image_placeholder),
            error = painterResource(R.drawable.image_placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .constrainAs(imageRef) {
                    end.linkTo(parent.end, margin = (-16).dp)
                    bottom.linkTo(parent.bottom, margin = (-16).dp)
                }

        )
    }
}