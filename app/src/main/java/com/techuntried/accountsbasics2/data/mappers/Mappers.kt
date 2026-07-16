package com.techuntried.accountsbasics2.data.mappers

import com.techuntried.accountsbasics2.domain.model.CategoryProgressModel
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.domain.model.content.LearnContentApiResponse
import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectApiResponse
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryWithProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity
import com.techuntried.accountsbasics2.domain.model.level.ChapterApiResponse
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.model.content.Option
import com.techuntried.accountsbasics2.domain.model.content.QuestionApiResponse
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity
import com.techuntried.accountsbasics2.domain.model.questions.GameOption
import com.techuntried.accountsbasics2.domain.model.questions.GameQuestionModel
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.ui.game.OptionType



fun CategoryWithProgressEntity.asCategoryWithProgressModel(): CategoryWithProgressModel {

    val category = SubjectModel(
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
        chapters = category.levels,
        course = category.course
    )

    return CategoryWithProgressModel(
        category = category,
        progress = progress?.asCategoryProgressModel(category.chapters) ?: CategoryProgressModel(
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


fun SubjectApiResponse.asCategoryEntity(): SubjectEntity {
    return SubjectEntity(
        categoryId = id,
        categoryName = name,
        categoryImage = imageUrl,
        isFeatured = featured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        levels = chapters,
        tag = tag,
        course = courseId,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight
    )
}

fun SubjectApiResponse.asSubjectModel(): SubjectModel {
    return SubjectModel(
        categoryId = id,
        categoryName = name,
        imageUrl = imageUrl,
        featured = featured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        chapters = chapters,
        tag = tag,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight,
        course = courseId
    )
}

fun SubjectEntity.asSubjectModel(): SubjectModel {
    return SubjectModel(
        categoryId = categoryId,
        categoryName = categoryName,
        course = course,
        imageUrl = categoryImage,
        featured = isFeatured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        chapters = levels,
        tag = tag,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight
    )
}


fun ChapterApiResponse.asChapterEntity(): ChapterEntity {
    return ChapterEntity(
        subjectId = subjectId,
        chapterId = chapterId,
        name = name,
        module = module,
        type = type
    )
}

fun ChapterEntity.asChapterModel(): ChapterModel {
    return ChapterModel(
        subjectId = subjectId,
        chapterId = chapterId,
        name = name,
        module = module,
        type = type
    )
}

fun QuestionApiResponse.asQuestionEntity(): QuestionEntity {
    return QuestionEntity(
        questionId = questionId,
        questionText = questionText,
        options = options,
        correctOptionId = correctOptionId,
        levelId = chapterId,
        categoryId = subjectId
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
        optionId = id,
        optionText = text,
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

fun LearnContentApiResponse.asLearnContentEntity(): LearnContentEntity {
    return LearnContentEntity(
        contentId = contentId,
        subjectId = subjectId,
        chapterId = chapterId,
        page = page,
        title = title,
        content = content
    )
}

fun LearnContentEntity.asLearnContentModel(): LearnContentModel {
    return LearnContentModel(
        contentId = contentId,
        subjectId = subjectId,
        chapterId = chapterId,
        page = page,
        title = title,
        content = content
    )
}