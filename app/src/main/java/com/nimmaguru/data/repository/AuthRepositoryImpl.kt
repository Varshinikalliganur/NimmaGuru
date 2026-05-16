package com.nimmaguru.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.nimmaguru.data.mapper.toAppUser
import com.nimmaguru.data.remote.FirebaseAuthDataSource
import com.nimmaguru.data.remote.FirestoreUserDataSource
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.repository.AuthRepository
import com.nimmaguru.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authDataSource: FirebaseAuthDataSource,
    private val userRepository: UserRepository,
) : AuthRepository {

    override val authState: Flow<AppUser?> = callbackFlow {
        var docListener: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            docListener?.remove()
            docListener = null
            val uid = auth.currentUser?.uid
            if (uid == null) {
                trySend(null)
            } else {
                docListener = firestore.collection(FirestoreUserDataSource.COLLECTION_USERS)
                    .document(uid)
                    .addSnapshotListener { snap, _ ->
                        trySend(snap?.takeIf { it.exists() }?.toAppUser())
                    }
            }
        }
        firebaseAuth.addAuthStateListener(authListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
            docListener?.remove()
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AppUser> = runCatching {
        val user = authDataSource.signInWithEmail(email, password)
        userRepository.ensureUserDocument(user.uid, user.email, user.displayName).getOrThrow()
        loadUser(user.uid)
    }

    override suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<AppUser> =
        runCatching {
            val user = authDataSource.registerWithEmail(email, password, displayName)
            userRepository.ensureUserDocument(user.uid, user.email, displayName).getOrThrow()
            loadUser(user.uid)
        }

    override suspend fun signInWithGoogle(idToken: String): Result<AppUser> = runCatching {
        val user = authDataSource.signInWithGoogle(idToken)
        userRepository.ensureUserDocument(user.uid, user.email, user.displayName).getOrThrow()
        loadUser(user.uid)
    }

    override suspend fun signOut() {
        authDataSource.signOut()
    }

    override fun currentUserId(): String? = authDataSource.currentUserId()

    private suspend fun loadUser(uid: String): AppUser {
        val doc = firestore.collection(FirestoreUserDataSource.COLLECTION_USERS)
            .document(uid)
            .get()
            .await()
        return doc.toAppUser() ?: error("User profile missing")
    }
}
