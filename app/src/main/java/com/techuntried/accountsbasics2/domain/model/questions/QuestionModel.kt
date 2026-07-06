package com.techuntried.accountsbasics2.domain.model.questions

import com.techuntried.accountsbasics2.ui.game.OptionType


data class QuestionModel(
    val categoryId: Int,
    val levelId: Int,
    val correctOptionId: Int,
    val options: List<GameOption>,
    val questionId: Int,
    val questionText: String
)

data class GameOption(
    val optionId: Int,
    val optionText: String,
    val optionType: OptionType = OptionType.Unselected
)

data class GameQuestionModel(
    val correctOptionId: Int,
    val options: List<GameOption>,
    val questionId: Int,
    val questionText: String
)