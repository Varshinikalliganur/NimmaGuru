package com.nimmaguru.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import java.util.Date

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toAppUser(): AppUser? {
    val uid = id.ifEmpty { return null }
    val role = UserRole.fromString(getString("role"))
    val subjects = (get("subjects") as? List<*>)?.mapNotNull { it as? String }.orEmpty()
    val years = getLong("yearsExperience")?.toInt()
    val createdAt = getTimestamp("createdAt")?.toDate()?.time
    return AppUser(
        id = uid,
        email = getString("email"),
        displayName = getString("displayName"),
        phone = getString("phone"),
        role = role,
        city = getString("city"),
        subjects = subjects,
        yearsExperience = years,
        bio = getString("bio"),
        photoUrl = getString("photoUrl"),
        languagePreference = getString("languagePreference"),
        createdAtMillis = createdAt,
    )
}

fun AppUser.toFirestoreMap(): Map<String, Any?> = mapOf(
    "email" to email,
    "displayName" to displayName,
    "phone" to phone,
    "role" to role.name,
    "city" to city,
    "subjects" to subjects,
    "yearsExperience" to yearsExperience,
    "bio" to bio,
    "photoUrl" to photoUrl,
    "languagePreference" to languagePreference,
    "createdAt" to (createdAtMillis?.let { Timestamp(Date(it)) } ?: Timestamp.now()),
)
