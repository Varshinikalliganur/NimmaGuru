package com.nimmaguru.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimmaguru.data.local.LocalePreferences
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import com.nimmaguru.domain.repository.AuthRepository
import com.nimmaguru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val localePreferences: LocalePreferences,
) : ViewModel() {

    val user = authRepository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null as AppUser?,
    )

    val locale = localePreferences.localeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "en",
    )

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    fun setLocale(code: String) {
        viewModelScope.launch {
            localePreferences.setLocale(code)
            val uid = user.value?.id ?: authRepository.currentUserId()
            uid?.let { userRepository.updateLanguagePreference(it, code) }
        }
    }

    fun switchRole(role: UserRole) {
        val uid = user.value?.id ?: return
        viewModelScope.launch {
            userRepository.updateRole(uid, role).onSuccess {
                _infoMessage.value = null
            }.onFailure {
                _infoMessage.value = it.message
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearMessage() = _infoMessage.update { null }
}
