package com.techuntried.accountsbasics2.data.mappers

import com.techuntried.accountsbasics2.domain.model.SubjectProgressModel
import com.techuntried.accountsbasics2.domain.model.SubjectWithProgressModel
import com.techuntried.accountsbasics2.domain.model.content.LearnContentApiResponse
import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.domain.model.content.Option
import com.techuntried.accountsbasics2.domain.model.content.QuestionApiResponse
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity
import com.techuntried.accountsbasics2.domain.model.entities.MistakeEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectWithProgressEntity
import com.techuntried.accountsbasics2.domain.model.level.ChapterApiResponse
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.model.questions.GameOption
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectApiResponse
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.ui.improve.MistakeItem
import com.techuntried.accountsbasics2.ui.questions.OptionType
import com.techuntried.accountsbasics2.utils.formatTimestamp


fun SubjectWithProgressEntity.asCategoryWithProgressModel(): SubjectWithProgressModel {

    val category = SubjectModel(
        subjectId = subject.subjectId,
        name = subject.name,
        featured = subject.isFeatured,
        imageUrl = subject.imageUrl,
        bgColor = subject.bgColor,
        active = subject.active,
        showTopics = subject.showTopics,
        section = subject.section,
        tag = subject.tag,
        weight = subject.weight,
        sectionWeight = subject.sectionWeight,
        featuredWeight = subject.featuredWeight,
        chapters = subject.chapters,
        course = subject.course
    )

    return SubjectWithProgressModel(
        subject = category,
        progress = progress?.asCategoryProgressModel(category.chapters) ?: SubjectProgressModel(
            subjectId = category.subjectId
        )
    )
}

fun SubjectProgressEntity.asCategoryProgressModel(totalLevels: Int): SubjectProgressModel {

    val progress = if (totalLevels > 0) {
        (chaptersCompleted.toFloat() / totalLevels) * 100
    } else 0f

    val totalAnswers = correctAnswered + wrongAnswered
    val accuracy = if (totalAnswers > 0) {
        (correctAnswered.toFloat() / totalAnswers) * 100f
    } else 0f

    return SubjectProgressModel(
        subjectId = subjectId,
        chaptersCompleted = chaptersCompleted,
        correctAnswered = correctAnswered,
        wrongAnswered = wrongAnswered,
        lastPlayedTime = lastPlayedTime,
        progressPercentage = progress,
        accuracy = accuracy
    )
}


fun SubjectApiResponse.asSubjectEntity(): SubjectEntity {
    return SubjectEntity(
        subjectId = id,
        name = name,
        imageUrl = imageUrl,
        isFeatured = featured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        chapters = chapters,
        tag = tag,
        course = courseId,
        showTopics = showTopics,
        featuredWeight = featuredWeight,
        sectionWeight = sectionWeight
    )
}

fun SubjectApiResponse.asSubjectModel(): SubjectModel {
    return SubjectModel(
        subjectId = id,
        name = name,
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
        subjectId = subjectId,
        name = name,
        course = course,
        imageUrl = imageUrl,
        featured = isFeatured,
        bgColor = bgColor,
        active = active,
        weight = weight,
        section = section,
        chapters = chapters,
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
        chapterId = chapterId,
        subjectId = subjectId,
        explanation = explanation
    )
}

fun QuestionEntity.asQuestionModel(): QuestionModel {
    return QuestionModel(
        questionId = questionId,
        questionText = questionText,
        options = options.map { it.asGameOption() },
        correctOptionId = correctOptionId,
        chapterId = chapterId,
        subjectId = subjectId,
        explanation = explanation
    )
}

fun Option.asGameOption(): GameOption {
    return GameOption(
        optionId = id,
        optionText = text,
        optionType = OptionType.Unselected
    )
}
//
//fun QuestionModel.asGameQuestion(): GameQuestionModel {
//    return GameQuestionModel(
//        correctOptionId = correctOptionId,
//        options = options,
//        questionId = questionId,
//        questionText = questionText,
//        explanation = explanation
//    )
//}

fun MistakeEntity.asQuestion(): QuestionModel {
    return QuestionModel(
        correctOptionId = correctOptionId,
        options = options.map { it.asGameOption() },
        questionId = questionId,
        questionText = questionText,
        explanation = explanation,
        subjectId = subjectId,
        chapterId = chapterId,
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

fun GameOption.asOption(): Option {
    return Option(
        id = optionId,
        text = optionText
    )
}

fun QuestionModel.asMistakeEntity(
    subjectId: Int,
    chapterId: Int,
    userAnswer: String?,
    fixed: Boolean
): MistakeEntity {
    return MistakeEntity(
        subjectId = subjectId,
        chapterId = chapterId,
        questionId = questionId,
        correctOptionId = correctOptionId,
        questionText = questionText,
        options = options.map { it.asOption() },
        answeredTimeInMillis = System.currentTimeMillis(),
        userAnswer = userAnswer,
        explanation = explanation,
        fixed = fixed,
    )
}

fun MistakeEntity.asMistakeItem(id:Int,subject: String): MistakeItem{
    return MistakeItem(
        id=id,
        subjectId = subjectId,
        chapterId = chapterId,
        questionId = questionId,
        subject = subject,
        date = formatTimestamp(answeredTimeInMillis),
        questionText = questionText,
        yourAnswer = userAnswer,
        correctAnswer = "",
        explanation = explanation,
        isFixed = fixed
    )
}