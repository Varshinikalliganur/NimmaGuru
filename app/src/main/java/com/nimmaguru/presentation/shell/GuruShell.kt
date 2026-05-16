package com.nimmaguru.presentation.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
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
import com.nimmaguru.presentation.guru.GuruHomeScreen
import com.nimmaguru.presentation.settings.SettingsScreen

private object GuruTabs {
    const val HOME = "guru_tab_home"
    const val CHAT = "guru_tab_chat"
    const val SETTINGS = "guru_tab_settings"
}

@Composable
fun GuruShell() {
    val navController = rememberNavController()
    val items = listOf(
        Triple(GuruTabs.HOME, stringResource(R.string.nav_home), Icons.Default.Home),
        Triple(GuruTabs.CHAT, stringResource(R.string.nav_ai_chat), Icons.Default.Chat),
        Triple(GuruTabs.SETTINGS, stringResource(R.string.nav_settings), Icons.Default.Settings),
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
            startDestination = GuruTabs.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(GuruTabs.HOME) { GuruHomeScreen() }
            composable(GuruTabs.CHAT) { ChatScreen() }
            composable(GuruTabs.SETTINGS) { SettingsScreen() }
        }
    }
}
