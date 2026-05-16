package com.nimmaguru.presentation.guru

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nimmaguru.R

@Composable
fun GuruHomeScreen(viewModel: GuruHomeViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    var displayName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var subjects by rememberSaveable { mutableStateOf("") }
    var years by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(user?.id) {
        val u = user ?: return@LaunchedEffect
        displayName = u.displayName.orEmpty()
        phone = u.phone.orEmpty()
        city = u.city.orEmpty()
        subjects = u.subjects.joinToString(", ")
        years = u.yearsExperience?.toString().orEmpty()
        bio = u.bio.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.guru_dashboard_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.guru_dashboard_body),
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(stringResource(R.string.display_name)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text(stringResource(R.string.city)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = subjects,
            onValueChange = { subjects = it },
            label = { Text(stringResource(R.string.subjects_hint)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = years,
            onValueChange = { years = it },
            label = { Text(stringResource(R.string.years_experience)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text(stringResource(R.string.bio)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.saveTeachingProfile(displayName, phone, city, subjects, years, bio)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(stringResource(R.string.save_profile))
        }
    }
}
