package com.techuntried.accountsbasics2.data.mappers

import com.techuntried.accountsbasics2.domain.model.CategoryProgressModel
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.domain.model.category.CategoryApiResponse
import com.techuntried.accountsbasics2.domain.model.category.CategoryModel
import com.techuntried.accountsbasics2.domain.model.entities.CategoryEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryWithProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.LevelEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity
import com.techuntried.accountsbasics2.domain.model.level.LevelApiResponse
import com.techuntried.accountsbasics2.domain.model.level.LevelModel
import com.techuntried.accountsbasics2.domain.model.question.Option
import com.techuntried.accountsbasics2.domain.model.question.QuestionApiResponse
import com.techuntried.accountsbasics2.domain.model.questions.GameOption
import com.techuntried.accountsbasics2.domain.model.questions.GameQuestionModel
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.ui.game.OptionType



fun CategoryWithProgressEntity.asCategoryWithProgressModel(): CategoryWithProgressModel {

    val category = CategoryModel(
        categoryId = category.categoryId,
        categoryName = category.categoryName,
        featured = category.isFeatured,
        imageUrl = category.categoryImage,
        bgColor = category.bgColor,
        active = category.active,
        showTopics = category.showTopics,
        section = category.section,
        tag = category.tag,
        weight = category.weight,
        sectionWeight = category.sectionWeight,
        featuredWeight = category.featuredWeight,
        levels = category.levels,
        grade = category.grade
    )

    return CategoryWithProgressModel(
        category = category,
        progress = progress?.asCategoryProgressModel(category.levels) ?: CategoryProgressModel(
            categoryId = category.categoryId
        )
    )
}

fun CategoryProgressEntity.asCategoryProgressModel(totalLevels: Int): CategoryProgressModel {

    val progress = if (totalLevels > 0) {
        (levelsPlayed.toFloat() / totalLevels) * 100
    } else 0f

    val totalAnswers = correctAnswered + wrongAnswered
    val accuracy = if (totalAnswers > 0) {
        (correctAnswered.toFloat() / totalAnswers) * 100f
    } else 0f

    return CategoryProgressModel(
        categoryId = categoryId,
        levelsPlayed = levelsPlayed,
        correctAnswered = correctAnswered,
        wrongAnswered = wrongAnswered,
        lastPlayedTime = lastPlayedTime,
        progressPercentage = progress,
        accuracy = accuracy
    )
}


fun CategoryApiResponse.asCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = categoryId,
        categoryName = categoryName,
        categoryImage = categoryImage,
        isFeatured = isFeatured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        levels = levels,
        tag = tag,
        grade = grades.firstOrNull() ?: 5,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight
    )
}

fun CategoryApiResponse.asCategoryModel(): CategoryModel {
    return CategoryModel(
        categoryId = categoryId,
        categoryName = categoryName,
        imageUrl = categoryImage,
        featured = isFeatured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        levels = levels,
        tag = tag,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight,
        grade = grades.firstOrNull() ?: 5
    )
}

fun CategoryEntity.asCategoryModel(): CategoryModel {
    return CategoryModel(
        categoryId = categoryId,
        categoryName = categoryName,
        grade = grade,
        imageUrl = categoryImage,
        featured = isFeatured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        levels = levels,
        tag = tag,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight
    )
}


fun LevelApiResponse.asLevelEntity(): LevelEntity {
    return LevelEntity(
        categoryId = categoryId,
        levelId = levelId,
        levelName = levelName,
        questions = questions,
        topic = topic
    )
}

fun LevelEntity.asLevelModel(): LevelModel {
    return LevelModel(
        categoryId = categoryId,
        levelId = levelId,
        levelName = levelName,
        questions = questions,
        topic = topic
    )
}

fun QuestionApiResponse.asQuestionEntity(): QuestionEntity {
    return QuestionEntity(
        questionId = questionId,
        questionText = questionText,
        options = options,
        correctOptionId = correctOptionId,
        levelId = levelId,
        categoryId = categoryId
    )
}

fun QuestionEntity.asQuestionModel(): QuestionModel {
    return QuestionModel(
        questionId = questionId,
        questionText = questionText,
        options = options.map { it.asGameOption() },
        correctOptionId = correctOptionId,
        levelId = levelId,
        categoryId = categoryId
    )
}

fun Option.asGameOption(): GameOption {
    return GameOption(
        optionId = optionId,
        optionText = optionText,
        optionType = OptionType.Unselected
    )
}

fun QuestionModel.asGameQuestion(): GameQuestionModel {
    return GameQuestionModel(
        correctOptionId = correctOptionId,
        options = options,
        questionId = questionId,
        questionText = questionText
    )
}