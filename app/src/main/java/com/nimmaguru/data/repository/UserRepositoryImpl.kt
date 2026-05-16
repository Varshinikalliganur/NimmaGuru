package com.nimmaguru.data.repository

import com.nimmaguru.data.remote.FirestoreUserDataSource
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import com.nimmaguru.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: FirestoreUserDataSource,
) : UserRepository {

    override fun observeUser(uid: String): Flow<AppUser?> = userDataSource.observeUser(uid)

    override suspend fun ensureUserDocument(uid: String, email: String?, displayName: String?): Result<Unit> =
        runCatching {
            userDataSource.ensureUserDocument(uid, email, displayName)
        }

    override suspend fun updateRole(uid: String, role: UserRole): Result<Unit> = runCatching {
        userDataSource.updateRole(uid, role)
    }

    override suspend fun updateProfile(
        uid: String,
        displayName: String?,
        phone: String?,
        city: String?,
        subjects: List<String>,
        yearsExperience: Int?,
        bio: String?,
    ): Result<Unit> = runCatching {
        userDataSource.updateProfile(uid, displayName, phone, city, subjects, yearsExperience, bio)
    }

    override suspend fun updateLanguagePreference(uid: String, code: String): Result<Unit> = runCatching {
        userDataSource.updateLanguagePreference(uid, code)
    }
}
