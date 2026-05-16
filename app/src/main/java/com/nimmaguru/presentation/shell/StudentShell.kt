package com.nimmaguru.presentation.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nimmaguru.R
import com.nimmaguru.presentation.chat.ChatScreen
import com.nimmaguru.presentation.search.GuruSearchScreen
import com.nimmaguru.presentation.settings.SettingsScreen
import com.nimmaguru.presentation.student.StudentHomeScreen

private object StudentTabs {
    const val HOME = "student_tab_home"
    const val SEARCH = "student_tab_search"
    const val CHAT = "student_tab_chat"
    const val SETTINGS = "student_tab_settings"
}

@Composable
fun StudentShell() {
    val navController = rememberNavController()
    val items = listOf(
        Triple(StudentTabs.HOME, stringResource(R.string.nav_home), Icons.Default.Home),
        Triple(StudentTabs.SEARCH, stringResource(R.string.nav_find_guru), Icons.Default.PersonSearch),
        Triple(StudentTabs.CHAT, stringResource(R.string.nav_ai_chat), Icons.Default.Chat),
        Triple(StudentTabs.SETTINGS, stringResource(R.string.nav_settings), Icons.Default.Settings),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { (route, label, icon) ->
                    val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = StudentTabs.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(StudentTabs.HOME) { StudentHomeScreen() }
            composable(StudentTabs.SEARCH) { GuruSearchScreen() }
            composable(StudentTabs.CHAT) { ChatScreen() }
            composable(StudentTabs.SETTINGS) { SettingsScreen() }
        }
    }
}
