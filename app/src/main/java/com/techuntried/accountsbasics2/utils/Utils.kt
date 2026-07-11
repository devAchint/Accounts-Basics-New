package com.techuntried.accountsbasics2.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
//import com.google.firebase.Firebase
//import com.google.firebase.messaging.messaging
import com.techuntried.accountsbasics2.ui.navigation.Routes
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.contains

fun captureScreenshot(view: View): Bitmap? {
    return try {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}

fun captureGameScreenshot(view: View, gameBannerEnabled: Boolean): Bitmap? {
    return try {
        val fullBitmap = createBitmap(view.width, view.height)

        val canvas = Canvas(fullBitmap)
        view.draw(canvas)

        val cutPx = dpToPx(60, view)
        val cutMultiplier = if (gameBannerEnabled) 2 else 1
        val croppedHeight = fullBitmap.height - (cutPx * cutMultiplier)
        if (croppedHeight <= 0) return null

        Bitmap.createBitmap(
            fullBitmap,
            0,              // x
            cutPx,          // y → skip top 60dp
            fullBitmap.width,
            croppedHeight   // height after top + bottom cut
        )
    } catch (e: Exception) {
        null
    }
}


fun dpToPx(dp: Int, view: View): Int {
    return (dp * view.resources.displayMetrics.density).toInt()
}

fun captureScrollScreenshot(view: ScrollView): Bitmap? {
    return try {
        val bitmap =
            Bitmap.createBitmap(view.width, view.getChildAt(0).height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun saveScreenshotToInternalStorage(bitmap: Bitmap, context: Context): File? {
    val fileName = "screenshot_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, fileName)
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun shareScreenshot(file: File, context: Context, message: String? = "") {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val share =
            Intent.createChooser(intent, null)

        context.startActivity(share)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No app available to share this content",
            Toast.LENGTH_SHORT
        ).show()
        e.printStackTrace()
    }
}

fun shareScreenshotPreferWhatsApp(
    file: File,
    context: Context,
    message: String? = ""
) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // 1️⃣ Try WhatsApp first
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (whatsappIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(whatsappIntent)
            return
        }

        // 2️⃣ Fallback to system share chooser
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, null))

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No app available to share this content",
            Toast.LENGTH_SHORT
        ).show()
        e.printStackTrace()
    }
}

fun getErrorMessageDescription(title: String? = null): String {
    Log.d("MYDEBUG", "Error $title")
    return when {
        title == null -> {
            "An unexpected error occurred. Please try again shortly."
        }

        title.contains("no network", ignoreCase = true) ||
                title.contains("no internet", ignoreCase = true)
            -> {
            "Please check your connection and try again."
        }

        else -> {
            "An unexpected error occurred. Please try again shortly."
        }
    }
}

fun getErrorMessageTitle(title: String? = null): String {
    Log.d("MYDEBUG", "Error $title")
    return when {
        title == null -> {
            "Oops! Something went wrong"
        }

        title.contains("no network", ignoreCase = true) ||
                title.contains("no internet", ignoreCase = true)
            -> {
            "No Network Connection"
        }

        else -> {
            "Oops! Something went wrong"
        }
    }
}


fun subscribeToTopic(topic: String) {
    val name = if (topic.contains(" ")) {
        topic.replace(" ", "_")
    } else topic

//    Firebase.messaging.subscribeToTopic(name)
//        .addOnCompleteListener { task ->
//            var msg = "Subscribed"
//            if (!task.isSuccessful) {
//                msg = "Subscribe failed ${task.exception}"
//            }
//            Log.d("MYDEBUG", msg)
//        }
}

fun Date.formatDate(): String {
    try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(this)
    } catch (e: Exception) {
        return ""
    }
}



fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        try {
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(fallbackIntent)
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
        }
    }

}

private val defaultEnterTransition = fadeIn(animationSpec = tween(700))
private val defaultExitTransition = fadeOut(animationSpec = tween(700))
private fun <T> AnimatedContentTransitionScope<NavBackStackEntry>.shouldShowTransition(
    show: () -> T,
    notShow: () -> T
): T {
    val noAnimationRoutes = listOf(
        Routes.HomeScreenRoute::class,
        Routes.ExploreScreenRoute::class,
        Routes.ProgressScreenRoute::class,
        Routes.SettingsScreenRoute::class,
    )

    val startDestination = initialState.destination
    val endDestination = targetState.destination

    val shouldDisableAnimation =
        noAnimationRoutes.any { startDestination.hasRoute(it) } ||
                noAnimationRoutes.any { endDestination.hasRoute(it) }

    return if (shouldDisableAnimation) {
        notShow()
    } else {
        show()
    }
}


fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
    return shouldShowTransition(
        show = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350)
            )
        },
        notShow = { defaultEnterTransition }
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
    return shouldShowTransition(
        show = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(350)
            )
        },
        notShow = {
            defaultExitTransition
        }
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
    return shouldShowTransition(
        show = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350)
            )
        },
        notShow = {
            defaultEnterTransition
        }
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
    return shouldShowTransition(
        show = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(350)
            )
        },
        notShow = {
            defaultExitTransition
        }
    )
}


@Composable
fun RowScope.Spacer(value: Dp) {
    Spacer(modifier = Modifier.width(value))
}

@Composable
fun ColumnScope.Spacer(value: Dp) {
    Spacer(modifier = Modifier.height(value))
}

inline fun Modifier.debouncedClickable(
    debounceIntervalMillis: Long = 500L,
    indicationEnabled: Boolean = true,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val interactionSource = remember { MutableInteractionSource() }

    val indication: Indication = ripple(
        bounded = false,
    )

    clickable(
        interactionSource = interactionSource,
        indication = indication.takeIf { indicationEnabled }
    ) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastClickTime) >= debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

@Composable
fun rememberDebouncedClick(
    debounceIntervalMillis: Long = 500L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceIntervalMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
