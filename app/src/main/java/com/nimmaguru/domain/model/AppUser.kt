package com.nimmaguru.domain.model

data class AppUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val phone: String?,
    val role: UserRole,
    val city: String?,
    val subjects: List<String>,
    val yearsExperience: Int?,
    val bio: String?,
    val photoUrl: String?,
    val languagePreference: String?,
    val createdAtMillis: Long?,
)
