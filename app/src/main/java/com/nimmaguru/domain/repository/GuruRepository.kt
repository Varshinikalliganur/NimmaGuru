package com.nimmaguru.domain.repository

import com.nimmaguru.domain.model.AppUser
import kotlinx.coroutines.flow.Flow

interface GuruRepository {
    fun observeGurus(): Flow<List<AppUser>>
}
