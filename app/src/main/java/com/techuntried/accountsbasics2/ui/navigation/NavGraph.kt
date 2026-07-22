package com.techuntried.accountsbasics2.ui.navigation

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
import com.techuntried.accountsbasics2.ui.rules.RulesScreenRoot
import com.techuntried.accountsbasics2.ui.score.ScoreScreenRoot
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
                navigateToRules = { ruleArgs ->
                    navController.navigate(
                        ruleArgs.toRuleScreenRoute()
                    )
                }
            )
        }

        composable<Routes.RulesScreenRoute> {
            val args = it.toRoute<Routes.RulesScreenRoute>()
            val ruleArgs = args.toRuleArgs()
            RulesScreenRoot(
                args = ruleArgs,
                onBackClick = {
                    navController.navigateUp()
                },
                navigateToQuestionsOrLearn = { questionArgs ->
                    if (args.isPracticeType) {
                        navController.navigate(questionArgs.toQuestionsScreenRoute()) {
                            popUpTo<Routes.RulesScreenRoute> {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(
                            Routes.LearnScreenRoute(
                                subjectId = args.subjectId,
                                chapterId = args.chapterId
                            )
                        )
                    }
                }
            )
        }


        composable<Routes.QuestionsScreenRoute> {
            val args = it.toRoute<Routes.QuestionsScreenRoute>()
            val questionsArgs = args.toQuestionArgs()
            QuestionsScreenRoot(
                args = questionsArgs,
                onBack = {
                    navController.navigateUp()
                },
                navigateToScore = { scoreArgs ->
                    navController.navigate(scoreArgs.toScoreScreenRoute()) {
                        popUpTo(Routes.HomeScreenRoute) {
                            inclusive = false
                        }
                    }
                }
            )
        }


        composable<Routes.ScoreScreenRoute>(
            typeMap = mapOf(
                typeOf<List<QuestionReviewModel>>() to serializableType<List<QuestionReviewModel>>()
            )
        ) {
            val args = it.toRoute<Routes.ScoreScreenRoute>()
            val scoreArgs = args.toScoreArgs()
            ScoreScreenRoot(
                args = scoreArgs,
                navigateToRule = { ruleArgs ->
                    navController.navigate(
                        ruleArgs.toRuleScreenRoute()
                    ) {
                        popUpTo<Routes.ScoreScreenRoute> {
                            inclusive = true
                        }
                    }
                },
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
                    val scoreArgs = ScoreArgs(
                        subjectId = args.subjectId,
                        chapterId = args.chapterId,
                        isPracticeType = false,
                        correctAnswers = 0,
                        totalQuestions = 0,
                        questionReview = emptyList()
                    )
                    navController.navigate(scoreArgs.toScoreScreenRoute()) {
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

                },
                practiceQuestion = {subjectId, chapterId, questionId ->

                }
            )
        }
    }
}