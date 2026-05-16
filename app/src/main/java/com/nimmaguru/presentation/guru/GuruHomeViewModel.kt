package com.nimmaguru.presentation.guru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.repository.AuthRepository
import com.nimmaguru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuruHomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val user = authRepository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null as AppUser?,
    )

    fun saveTeachingProfile(
        displayName: String,
        phone: String,
        city: String,
        subjectsCsv: String,
        years: String,
        bio: String,
    ) {
        val uid = user.value?.id ?: return
        viewModelScope.launch {
            val subjects = subjectsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            userRepository.updateProfile(
                uid = uid,
                displayName = displayName.ifBlank { null },
                phone = phone.ifBlank { null },
                city = city.ifBlank { null },
                subjects = subjects,
                yearsExperience = years.toIntOrNull(),
                bio = bio.ifBlank { null },
            )
        }
    }
}
