package com.techuntried.accountsbasics2.utils

import androidx.compose.ui.graphics.Color
import com.techuntried.accountsbasics2.ui.questions.OptionType
import com.techuntried.accountsbasics2.ui.theme.CorrectOptionBorderColor
import com.techuntried.accountsbasics2.ui.theme.CorrectOptionColor
import com.techuntried.accountsbasics2.ui.theme.CorrectOptionTextColor
import com.techuntried.accountsbasics2.ui.theme.DisabledOptionBorderColor
import com.techuntried.accountsbasics2.ui.theme.DisabledOptionColor
import com.techuntried.accountsbasics2.ui.theme.DisabledOptionTextColor
import com.techuntried.accountsbasics2.ui.theme.SelectedOptionColor
import com.techuntried.accountsbasics2.ui.theme.SelectedOptionTextColor
import com.techuntried.accountsbasics2.ui.theme.UnselectedOptionBorderColor
import com.techuntried.accountsbasics2.ui.theme.UnselectedOptionColor
import com.techuntried.accountsbasics2.ui.theme.UnselectedOptionTextColor
import com.techuntried.accountsbasics2.ui.theme.WrongOptionBorderColor
import com.techuntried.accountsbasics2.ui.theme.WrongOptionColor
import com.techuntried.accountsbasics2.ui.theme.WrongOptionTextColor

fun OptionType.bgColor(): Color {
    return when (this) {
        OptionType.Unselected -> UnselectedOptionColor
        OptionType.Selected -> SelectedOptionColor
        OptionType.Disabled -> DisabledOptionColor
        OptionType.Correct -> CorrectOptionColor
        OptionType.Wrong -> WrongOptionColor
    }
}


fun OptionType.borderColor(): Color {
    return when (this) {
        OptionType.Unselected -> UnselectedOptionBorderColor
        OptionType.Selected -> SelectedOptionColor
        OptionType.Disabled -> DisabledOptionBorderColor
        OptionType.Correct -> CorrectOptionBorderColor
        OptionType.Wrong -> WrongOptionBorderColor
    }
}

fun OptionType.textColor(): Color {
    return when (this) {
        OptionType.Unselected -> UnselectedOptionTextColor
        OptionType.Selected -> SelectedOptionTextColor
        OptionType.Disabled -> DisabledOptionTextColor
        OptionType.Correct -> CorrectOptionTextColor
        OptionType.Wrong -> WrongOptionTextColor
    }
}

