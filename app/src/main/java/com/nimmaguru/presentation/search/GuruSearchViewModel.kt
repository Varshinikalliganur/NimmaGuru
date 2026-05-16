package com.nimmaguru.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimmaguru.domain.GuruFilterMatcher
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.GuruFilter
import com.nimmaguru.domain.repository.GuruRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class GuruSearchUiState(
    val query: String = "",
    val city: String = "",
    val subject: String = "",
    val minExperience: String = "",
    val gurus: List<AppUser> = emptyList(),
)

@HiltViewModel
class GuruSearchViewModel @Inject constructor(
    guruRepository: GuruRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val city = MutableStateFlow("")
    private val subject = MutableStateFlow("")
    private val minExperience = MutableStateFlow("")

    val uiState = combine(
        guruRepository.observeGurus(),
        query,
        city,
        subject,
        minExperience,
    ) { list, q, c, s, me ->
        val filter = GuruFilter(
            query = q,
            city = c.ifBlank { null },
            subject = s.ifBlank { null },
            minExperience = me.toIntOrNull(),
        )
        GuruSearchUiState(
            query = q,
            city = c,
            subject = s,
            minExperience = me,
            gurus = GuruFilterMatcher.filterList(list, filter),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GuruSearchUiState(),
    )

    fun setQuery(value: String) = query.update { value }
    fun setCity(value: String) = city.update { value }
    fun setSubject(value: String) = subject.update { value }
    fun setMinExperience(value: String) = minExperience.update { value }
}
