package com.nimmaguru.presentation.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nimmaguru.R
import com.nimmaguru.domain.model.UserRole

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val locale by viewModel.locale.collectAsStateWithLifecycle()
    val info by viewModel.infoMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    LaunchedEffect(info) {
        val message = info ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleLarge,
        )
        RowButtons(
            leftLabel = stringResource(R.string.language_english),
            rightLabel = stringResource(R.string.language_kannada),
            onLeft = { viewModel.setLocale("en") },
            onRight = { viewModel.setLocale("kn") },
            selectedIsRight = locale.startsWith("kn"),
        )
        Text(
            text = stringResource(R.string.settings_role),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.settings_role_body),
            style = MaterialTheme.typography.bodyLarge,
        )
        RowButtons(
            leftLabel = stringResource(R.string.role_student),
            rightLabel = stringResource(R.string.role_guru),
            onLeft = { viewModel.switchRole(UserRole.STUDENT) },
            onRight = { viewModel.switchRole(UserRole.GURU) },
            selectedIsRight = user?.role == UserRole.GURU,
        )
        Text(
            text = stringResource(R.string.settings_notifications),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.settings_notifications_body),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (notificationPermission != null) {
            Button(
                onClick = {
                    if (!notificationPermission.status.isGranted) {
                        notificationPermission.launchPermissionRequest()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    if (notificationPermission.status.isGranted) {
                        stringResource(R.string.notifications_enabled)
                    } else {
                        stringResource(R.string.enable_notifications)
                    },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { viewModel.signOut() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(stringResource(R.string.sign_out))
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun RowButtons(
    leftLabel: String,
    rightLabel: String,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    selectedIsRight: Boolean,
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (!selectedIsRight) {
            Button(
                onClick = onLeft,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text(leftLabel)
            }
            OutlinedButton(
                onClick = onRight,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text(rightLabel)
            }
        } else {
            OutlinedButton(
                onClick = onLeft,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text(leftLabel)
            }
            Button(
                onClick = onRight,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text(rightLabel)
            }
        }
    }
}
