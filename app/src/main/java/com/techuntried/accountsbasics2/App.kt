package com.techuntried.accountsbasics2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.techuntried.accountsbasics2.ui.navigation.NavGraph
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.BottomBarActiveColor
import com.techuntried.accountsbasics2.ui.theme.BottomBarBackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BottomBarInActiveColor
import com.techuntried.accountsbasics2.utils.AppIcons

@Composable
fun App(modifier: Modifier = Modifier, isFirstTime: Boolean) {
    val navController = rememberNavController()

    val items = listOf(
        BottomBarItem(
            title = "Home",
            selectedIcon = AppIcons.HomeFilled,
            unSelectedIcon = AppIcons.HomeUnfilled,
            route = Routes.HomeScreenRoute
        ),
        BottomBarItem(
            title = "Improve",
            selectedIcon = AppIcons.Search,
            unSelectedIcon = AppIcons.Search,
            route = Routes.ImproveScreenRoute
        ),
        BottomBarItem(
            title = "Progress",
            selectedIcon = AppIcons.Progress,
            unSelectedIcon = AppIcons.Progress,
            route = Routes.ProgressScreenRoute
        ),
        BottomBarItem(
            title = "Settings",
            selectedIcon = AppIcons.SettingsFilled,
            unSelectedIcon = AppIcons.SettingsUnfilled,
            route = Routes.SettingsScreenRoute
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = items.indexOfFirst {
        currentDestination?.hasRoute(it.route::class) == true
    }
    val showBottomBar = items.any {
        currentDestination?.hasRoute(it.route::class) == true
    }

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),    // Slide in from bottom
                exit = slideOutVertically(targetOffsetY = { it })      // Slide out to bottom
            ) {
                BottomBar(
                    items = items,
                    selectedIndex = selectedIndex,
                    onClick = {
                        navController.navigate(items[it].route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(it),
            isFirstTime = isFirstTime
        )
    }
}


data class BottomBarItem(
    val title: String,
    val selectedIcon: Int,
    val unSelectedIcon: Int,
    val route: Routes
)

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomBarItem>,
    selectedIndex: Int, onClick: (Int) -> Unit
) {
    Box {
        NavigationBar(
            containerColor = BottomBarBackgroundColor,
            tonalElevation = 4.dp,
        ) {
            items.forEachIndexed { index, it ->
                val selected = selectedIndex == index
                val textColor = if (selected) BottomBarActiveColor else BottomBarInActiveColor
                val iconColor = if (selected) BottomBarActiveColor else BottomBarInActiveColor


                NavigationBarItem(
                    selected = selected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
                    onClick = { onClick(index) },
                    label = {
                        Text(
                            text = it.title,
                            color = textColor,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp)
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(if (selected) it.selectedIcon else it.unSelectedIcon),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = iconColor
                        )
                    }
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}