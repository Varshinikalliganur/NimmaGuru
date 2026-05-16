package com.nimmaguru.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.nimmaguru.data.mapper.toAppUser
import com.nimmaguru.data.mapper.toFirestoreMap
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private fun userDoc(uid: String) = firestore.collection(COLLECTION_USERS).document(uid)

    fun observeUser(uid: String): Flow<AppUser?> = callbackFlow {
        var registration: ListenerRegistration? = null
        registration = userDoc(uid).addSnapshotListener { snap, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snap?.takeIf { it.exists() }?.toAppUser())
        }
        awaitClose { registration?.remove() }
    }

    suspend fun ensureUserDocument(uid: String, email: String?, displayName: String?) {
        val ref = userDoc(uid)
        val snap = ref.get().await()
        if (!snap.exists()) {
            val initial = AppUser(
                id = uid,
                email = email,
                displayName = displayName,
                phone = null,
                role = UserRole.STUDENT,
                city = null,
                subjects = emptyList(),
                yearsExperience = null,
                bio = null,
                photoUrl = null,
                languagePreference = null,
                createdAtMillis = null,
            )
            ref.set(initial.toFirestoreMap(), SetOptions.merge()).await()
        } else {
            val updates = mutableMapOf<String, Any?>()
            if (email != null) updates["email"] = email
            if (displayName != null) updates["displayName"] = displayName
            if (updates.isNotEmpty()) {
                ref.set(updates, SetOptions.merge()).await()
            }
        }
    }

    suspend fun updateRole(uid: String, role: UserRole) {
        userDoc(uid).update("role", role.name).await()
    }

    suspend fun updateProfile(
        uid: String,
        displayName: String?,
        phone: String?,
        city: String?,
        subjects: List<String>,
        yearsExperience: Int?,
        bio: String?,
    ) {
        val updates = hashMapOf<String, Any>()
        displayName?.let { updates["displayName"] = it }
        phone?.let { updates["phone"] = it }
        city?.let { updates["city"] = it }
        updates["subjects"] = subjects
        yearsExperience?.let { updates["yearsExperience"] = it }
        bio?.let { updates["bio"] = it }
        userDoc(uid).set(updates, SetOptions.merge()).await()
    }

    suspend fun updateLanguagePreference(uid: String, code: String) {
        userDoc(uid).update("languagePreference", code).await()
    }

    suspend fun saveFcmToken(uid: String, token: String) {
        userDoc(uid).set(mapOf("fcmToken" to token), SetOptions.merge()).await()
    }

    companion object {
        const val COLLECTION_USERS = "users"
    }
}
