package com.nimmaguru.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {

    val user = authRepository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null as AppUser?,
    )
}
