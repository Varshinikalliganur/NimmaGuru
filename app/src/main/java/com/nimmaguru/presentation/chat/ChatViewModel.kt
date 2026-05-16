package com.nimmaguru.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimmaguru.data.local.LocalePreferences
import com.nimmaguru.domain.repository.ChatRepository
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
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    localePreferences: LocalePreferences,
) : ViewModel() {

    private val sessionId = "main"

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    val locale = localePreferences.localeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "en",
    )

    val messages = chatRepository.observeMessages(sessionId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onInputChange(value: String) = _input.update { value }

    fun sendMessage() {
        val text = _input.value
        if (text.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(sessionId, text, locale.value)
            _input.value = ""
        }
    }
}
