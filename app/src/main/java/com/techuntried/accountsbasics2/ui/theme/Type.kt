package com.techuntried.accountsbasics2.ui.theme

import com.techuntried.accountsbasics2.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val RubikRegular = FontFamily(
    Font(R.font.rubik_regular_400)
)

val RubikMedium = FontFamily(
    Font(R.font.rubik_medium_500)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 16.sp,
        lineHeight = 1.3.em
    ),
    headlineLarge = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 24.sp,
        lineHeight = 1.3.em
    ),
    headlineMedium = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 22.sp,
        lineHeight = 1.3.em
    ),
    headlineSmall = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 20.sp,
        lineHeight = 1.3.em
    ),
    titleLarge = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 18.sp,
        lineHeight = 1.3.em
    ),
    titleMedium = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 16.sp,
        lineHeight = 1.3.em
    ),
    titleSmall = TextStyle(
        fontFamily = RubikMedium,
        fontSize = 14.sp,
        lineHeight = 1.3.em
    ),
    labelLarge = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 16.sp,
        lineHeight = 1.3.em
    ),
    labelMedium = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 14.sp,
        lineHeight = 1.3.em
    ),
    labelSmall = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 12.sp,
        lineHeight = 1.3.em
    ),
    bodyMedium = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 14.sp,
        lineHeight = 1.3.em
    ),
    bodySmall = TextStyle(
        fontFamily = RubikRegular,
        fontSize = 12.sp,
        lineHeight = 1.3.em
    )
)
