package com.techuntried.accountsbasics2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import com.techuntried.accountsbasics2.ui.chooseCourse.ChooseCourseScreenRoot
import com.techuntried.accountsbasics2.ui.explore.ExploreScreenRoot
import com.techuntried.accountsbasics2.ui.feedback.FeedbackScreenRoot
import com.techuntried.accountsbasics2.ui.game.GameScreenRoot
import com.techuntried.accountsbasics2.ui.home.HomeScreenRoot
import com.techuntried.accountsbasics2.ui.chapter.ChaptersScreenRoot
import com.techuntried.accountsbasics2.ui.notificationPermission.NotificationPermissionScreenRoot
import com.techuntried.accountsbasics2.ui.progress.ProgressScreenRoot
import com.techuntried.accountsbasics2.ui.rules.RulesScreenRoot
import com.techuntried.accountsbasics2.ui.score.ScoreScreenRoot
import com.techuntried.accountsbasics2.ui.searchData.SearchDataScreenRoot
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
                openChooseGrade = {
                    navController.navigate(Routes.ChooseGradeScreenRoute)
                },
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
                openChooseGrade = {
                    navController.navigate(Routes.ChooseGradeScreenRoute)
                },
                openFeedback = {
                    navController.navigate(Routes.FeedbackScreenRoute)
                }
            )
        }


        composable<Routes.StartScreenRoute> {
            StartScreenRoot(
                openChooseGrade = {
                    navController.navigate(Routes.ChooseGradeScreenRoute)
                }
            )
        }

        composable<Routes.ChooseGradeScreenRoute> {
            ChooseCourseScreenRoot(
                isFirstTime = isFirstTime,
                openNotificationRequest = {
                    navController.navigate(Routes.NotificationPermissionScreenRoute)
                },
                openHome = {
                    navController.navigate(Routes.HomeScreenRoute){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateBack = {
                    navController.navigateUp()
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

        composable<Routes.LevelScreenRoute> {
            val args = it.toRoute<Routes.LevelScreenRoute>()
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
                navigateToGame = { gameArgs ->
                    navController.navigate(gameArgs.toGameScreenRoute()) {
                        popUpTo<Routes.RulesScreenRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }


        composable<Routes.GameScreenRoute> {
            val args = it.toRoute<Routes.GameScreenRoute>()
            val gameArgs = args.toGameArgs()
            GameScreenRoot(
                args = gameArgs,
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
                openGameLevel = { levelArgs ->
                    navController.navigate(levelArgs.toLevelScreenRoute())
                }
            )
        }


        composable<Routes.ExploreScreenRoute> {
            ExploreScreenRoot(
                openGameLevel = { levelArgs ->
                    navController.navigate(levelArgs.toLevelScreenRoute())
                },
                openSearchData = {
                    navController.navigate(Routes.SearchDataScreenRoute)
                },
                onMoreCategoriesClick = { section ,grades->
                    navController.navigate(
                        Routes.SectionCategoriesScreenRoute(
                            section = section,
                            grades = grades
                        )
                    )
                }
            )
        }


        composable<Routes.SearchDataScreenRoute> {
            SearchDataScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                openGameLevel = {
                    navController.navigate(it.toLevelScreenRoute())
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
    }
}