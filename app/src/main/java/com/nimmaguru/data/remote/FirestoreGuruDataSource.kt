package com.nimmaguru.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.nimmaguru.data.mapper.toAppUser
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.model.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreGuruDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    fun observeAllGurus(): Flow<List<AppUser>> = callbackFlow {
        var registration: ListenerRegistration? = null
        val query: Query = firestore.collection(FirestoreUserDataSource.COLLECTION_USERS)
            .whereEqualTo("role", UserRole.GURU.name)
        registration = query.addSnapshotListener { snap, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { it.toAppUser() }.orEmpty()
            trySend(list)
        }
        awaitClose { registration?.remove() }
    }
}
