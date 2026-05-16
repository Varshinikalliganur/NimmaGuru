package com.nimmaguru.domain.repository

import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(uid: String): Flow<AppUser?>
    suspend fun ensureUserDocument(uid: String, email: String?, displayName: String?): Result<Unit>
    suspend fun updateRole(uid: String, role: UserRole): Result<Unit>
    suspend fun updateProfile(
        uid: String,
        displayName: String?,
        phone: String?,
        city: String?,
        subjects: List<String>,
        yearsExperience: Int?,
        bio: String?,
    ): Result<Unit>
    suspend fun updateLanguagePreference(uid: String, code: String): Result<Unit>
}
