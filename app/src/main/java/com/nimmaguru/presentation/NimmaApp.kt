package com.nimmaguru.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nimmaguru.domain.model.UserRole
import com.nimmaguru.presentation.auth.AuthNavHost
import com.nimmaguru.presentation.shell.GuruShell
import com.nimmaguru.presentation.shell.StudentShell

@Composable
fun NimmaApp(appViewModel: AppViewModel = hiltViewModel()) {
    val user by appViewModel.user.collectAsStateWithLifecycle()
    val locale by appViewModel.locale.collectAsStateWithLifecycle()

    LaunchedEffect(locale) {
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
            androidx.core.os.LocaleListCompat.forLanguageTags(locale),
        )
    }

    val currentUser = user
    when {
        currentUser == null -> AuthNavHost()
        currentUser.role == UserRole.GURU -> GuruShell()
        else -> StudentShell()
    }
}
