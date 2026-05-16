package com.nimmaguru.domain.repository

import com.nimmaguru.domain.model.AppUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AppUser?>

    suspend fun signInWithEmail(email: String, password: String): Result<AppUser>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<AppUser>
    suspend fun signInWithGoogle(idToken: String): Result<AppUser>
    suspend fun signOut()
    fun currentUserId(): String?
}
