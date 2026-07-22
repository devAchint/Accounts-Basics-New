package com.techuntried.accountsbasics2.ui.navigation

import ads_mobile_sdk.su
import com.techuntried.accountsbasics2.ui.improve.ImproveScreenRoot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import com.techuntried.accountsbasics2.ui.chapter.ChaptersScreenRoot
import com.techuntried.accountsbasics2.ui.feedback.FeedbackScreenRoot
import com.techuntried.accountsbasics2.ui.questions.QuestionsScreenRoot
import com.techuntried.accountsbasics2.ui.home.HomeScreenRoot
import com.techuntried.accountsbasics2.ui.learn.LearnScreenRoot
import com.techuntried.accountsbasics2.ui.notificationPermission.NotificationPermissionScreenRoot
import com.techuntried.accountsbasics2.ui.progress.ProgressScreenRoot
import com.techuntried.accountsbasics2.ui.questions.QuestionsTarget
import com.techuntried.accountsbasics2.ui.rules.RulesScreenRoot
import com.techuntried.accountsbasics2.ui.score.ScoreScreenRoot
import com.techuntried.accountsbasics2.ui.score.ScoreTarget
import com.techuntried.accountsbasics2.ui.sectionCategories.SectionCategoriesScreenRoot
import com.techuntried.accountsbasics2.ui.settings.SettingsScreenRoot
import com.techuntried.accountsbasics2.ui.start.StartScreenRoot
import com.techuntried.accountsbasics2.utils.enterTransition
import com.techuntried.accountsbasics2.utils.exitTransition
import com.techuntried.accountsbasics2.utils.popEnterTransition
import com.techuntried.accountsbasics2.utils.popExitTransition
import kotlin.reflect.typeOf

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isFirstTime: Boolean
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() },
        startDestination = if (isFirstTime) Routes.StartScreenRoute else Routes.HomeScreenRoute,
    ) {

        composable<Routes.HomeScreenRoute> {
            HomeScreenRoot(
                openLevels = { levelArgs ->
                    navController.navigate(
                        levelArgs.toLevelScreenRoute()
                    )
                },
                onMoreCategoriesClick = { section, grade ->
                    navController.navigate(
                        Routes.SectionCategoriesScreenRoute(
                            section = section,
                            grades = grade?.let { listOf(it) }
                        )
                    )
                }
            )
        }

        composable<Routes.SettingsScreenRoute> {
            SettingsScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                openFeedback = {
                    navController.navigate(Routes.FeedbackScreenRoute)
                }
            )
        }


        composable<Routes.StartScreenRoute> {
            StartScreenRoot(
                openNotificationPermission = {
                    navController.navigate(Routes.NotificationPermissionScreenRoute)
                }
            )
        }


        composable<Routes.NotificationPermissionScreenRoute> {
            NotificationPermissionScreenRoot(
                onContinue = {
                    navController.navigate(Routes.HomeScreenRoute) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routes.ChaptersScreenRoute> {
            val args = it.toRoute<Routes.ChaptersScreenRoute>()
            val levelArgs = args.toLevelArgs()
            ChaptersScreenRoot(
                args = levelArgs,
                onBack = {
                    navController.navigateUp()
                },
                navigateToRules = { chapterId ->
                    navController.navigate(
                        Routes.RulesScreenRoute(subjectId = args.subjectId,chapterId)
                    )
                }
            )
        }

        composable<Routes.RulesScreenRoute> {
            val args = it.toRoute<Routes.RulesScreenRoute>()
            RulesScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                navigateToQuestionsOrLearn = { timerCount,isLearnType ->
                    if (isLearnType) {
                        navController.navigate(
                            Routes.LearnScreenRoute(
                                subjectId = args.subjectId,
                                chapterId = args.chapterId
                            )
                        )
                    } else {
                        val subjectQuestionRoute = Routes.SubjectQuestionsScreenRoute(
                            subjectId = args.subjectId,
                            chapterId = args.chapterId,
                            timerCount = timerCount
                        )
                        navController.navigate(subjectQuestionRoute) {
                            popUpTo<Routes.RulesScreenRoute> {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }


        composable<Routes.SubjectQuestionsScreenRoute> {
            val args = it.toRoute<Routes.SubjectQuestionsScreenRoute>()
            QuestionsScreenRoot(
                questionsTarget = QuestionsTarget.Subject(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId,
                    timerCount = args.timerCount
                ),
                onBack = {
                    navController.navigateUp()
                },
                timerCount = args.timerCount,
                navigateToScore = { correctAnswers, totalQuestions, questionsReview ->
                    val subjectScoreRoute = Routes.SubjectScoreScreenRoute(
                        subjectId = args.subjectId,
                        chapterId = args.chapterId,
                        correctAnswers = correctAnswers,
                        totalQuestions = totalQuestions,
                        questionReview = questionsReview
                    )
                    navController.navigate(subjectScoreRoute) {
                        popUpTo(Routes.HomeScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable<Routes.PracticeQuestionScreenRoute> {
            val args = it.toRoute<Routes.PracticeQuestionScreenRoute>()
            QuestionsScreenRoot(
                questionsTarget = QuestionsTarget.PracticeQuestion(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId,
                    questionId = args.questionId
                ),
                timerCount = null,
                onBack = {
                    navController.navigateUp()
                },
                navigateToScore = { correctAnswers, totalQuestions, questionsReview ->
                    val practiceScoreRoute = Routes.PracticeScoreScreenRoute(
                        subjectId = args.subjectId,
                        chapterId = args.chapterId,
                        correctAnswers = correctAnswers,
                        totalQuestions = totalQuestions,
                        questionReview = questionsReview
                    )
                    navController.navigate(practiceScoreRoute) {
                        popUpTo(Routes.HomeScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable<Routes.PracticeAllQuestionsScreenRoute> {
            QuestionsScreenRoot(
                questionsTarget = QuestionsTarget.PracticeAllQuestions,
                onBack = {
                    navController.navigateUp()
                },
                timerCount = null,
                navigateToScore = { correctAnswers, totalQuestions, questionsReview ->
                    val practiceAllScoreRoute = Routes.PracticeAllScoreScreenRoute(
                        correctAnswers = correctAnswers,
                        totalQuestions = totalQuestions,
                        questionReview = questionsReview
                    )
                    navController.navigate(practiceAllScoreRoute) {
                        popUpTo(Routes.HomeScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }


        composable<Routes.SubjectScoreScreenRoute>(
            typeMap = mapOf(
                typeOf<List<QuestionReviewModel>>() to serializableType<List<QuestionReviewModel>>()
            )
        ) {
            val args = it.toRoute<Routes.SubjectScoreScreenRoute>()
            ScoreScreenRoot(
                scoreTarget = ScoreTarget.Subject(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId,
                    correctAnswers = args.correctAnswers,
                    totalQuestions = args.totalQuestions,
                    questionReview = args.questionReview
                ),
                navigateToRule = { subjectId,chapterId->
                    val ruleScreenRoute = Routes.RulesScreenRoute(subjectId = subjectId, chapterId = chapterId)
                    navController.navigate(ruleScreenRoute ) {
                        popUpTo<Routes.SubjectScoreScreenRoute> {
                            inclusive = true
                        }
                    }
                },
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.LearnScoreScreenRoute> {
            val args = it.toRoute<Routes.LearnScoreScreenRoute>()
            ScoreScreenRoot(
                scoreTarget = ScoreTarget.Learn(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId,
                ),
                navigateToRule = { subjectId,chapterId->
                    val ruleScreenRoute = Routes.RulesScreenRoute(subjectId = subjectId, chapterId = chapterId)
                    navController.navigate(ruleScreenRoute) {
                        popUpTo<Routes.LearnScoreScreenRoute> {
                            inclusive = true
                        }
                    }
                },
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.PracticeScoreScreenRoute>(
            typeMap = mapOf(
                typeOf<List<QuestionReviewModel>>() to serializableType<List<QuestionReviewModel>>()
            )
        ) {
            val args = it.toRoute<Routes.PracticeScoreScreenRoute>()
            ScoreScreenRoot(
                scoreTarget = ScoreTarget.PracticeQuestion(
                    subjectId = args.subjectId,
                    chapterId = args.chapterId,
                    correctAnswers = args.correctAnswers,
                    totalQuestions = args.totalQuestions,
                    questionReview = args.questionReview
                ),
                navigateToRule = { _,_->},
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.PracticeAllScoreScreenRoute>(
            typeMap = mapOf(
                typeOf<List<QuestionReviewModel>>() to serializableType<List<QuestionReviewModel>>()
            )
        ) {
            val args = it.toRoute<Routes.PracticeAllScoreScreenRoute>()
            ScoreScreenRoot(
                scoreTarget = ScoreTarget.PracticeAllQuestions(
                    correctAnswers = args.correctAnswers,
                    totalQuestions = args.totalQuestions,
                    questionReview = args.questionReview
                ),
                navigateToRule = { _,_->},
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }

        composable<Routes.FeedbackScreenRoute> {
            FeedbackScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }


        composable<Routes.ProgressScreenRoute> {
            ProgressScreenRoot(
                openQuizSection = {},
                openChapters = { levelArgs ->
                    navController.navigate(levelArgs.toLevelScreenRoute())
                }
            )
        }


        composable<Routes.SectionCategoriesScreenRoute> {
            val args = it.toRoute<Routes.SectionCategoriesScreenRoute>()
            SectionCategoriesScreenRoot(
                section = args.section,
                onBack = {
                    navController.navigateUp()
                },
                openGameLevel = { levelArgs ->
                    navController.navigate(levelArgs.toLevelScreenRoute())
                }
            )
        }


        composable<Routes.LearnScreenRoute> {
            val args = it.toRoute<Routes.LearnScreenRoute>()
            LearnScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                onFinish = {
                    val learnScoreRoute = Routes.LearnScoreScreenRoute(
                        subjectId = args.subjectId,
                        chapterId = args.chapterId
                    )
                    navController.navigate(learnScoreRoute) {
                        popUpTo(Routes.HomeScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }


        composable<Routes.ImproveScreenRoute> {
            ImproveScreenRoot(
                practiceAll = {
                    val practiceAllQuestionsScreenRoute = Routes.PracticeAllQuestionsScreenRoute
                    navController.navigate(practiceAllQuestionsScreenRoute) {
                        popUpTo(Routes.ImproveScreenRoute) {
                            inclusive = false
                        }
                    }
                },
                practiceQuestion = { subjectId, chapterId, questionId ->
                    val practiceQuestionScreenRoute = Routes.PracticeQuestionScreenRoute(
                        subjectId = subjectId,
                        chapterId = chapterId,
                        questionId = questionId
                    )
                    navController.navigate(practiceQuestionScreenRoute) {
                        popUpTo(Routes.ImproveScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}