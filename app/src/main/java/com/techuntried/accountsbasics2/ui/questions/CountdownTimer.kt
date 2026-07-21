package com.techuntried.accountsbasics2.ui.questions

import com.techuntried.accountsbasics2.R
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.techuntried.accountsbasics2.ui.theme.MainText

@Composable
fun CountdownTimer(
    value: Int,
    modifier: Modifier = Modifier,
    isSoundEnabled:Boolean,
) {
    val scale = remember { Animatable(1f) }
    val soundPlayer = rememberSoundPlayer()
    val context = LocalContext.current

    LaunchedEffect(value) {
        soundPlayer.invoke(context, R.raw.tick, isSoundEnabled)
        
        scale.snapTo(0.78f)
        
        // Fast impact scale-up
        scale.animateTo(
            targetValue = 1.18f,
            animationSpec = tween(
                durationMillis = 180,
                easing = FastOutLinearInEasing // IMPORTANT
            )
        )

    }

    Text(
        text = value.toString(),
        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 96.sp),
        color = MainText,
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
    )
}