package com.example.moverconnect.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Pair<FirebaseUser, String>> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Get user type from Firestore
                val userType = getUserTypeFromFirestore(user.uid)
                if (userType != null) {
                    Result.success(Pair(user, userType))
                } else {
                    // If user type is not found, delete the user and throw an error
                    user.delete().await()
                    Result.failure(Exception("User type not found"))
                }
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithPhoneNumber(phoneNumber: String, password: String): Result<Pair<FirebaseUser, String>> {
        // For phone number login, we'll use email/password auth with a formatted email
        val email = "$phoneNumber@moverconnect.com"
        return signInWithEmailAndPassword(email, password)
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        fullName: String,
        userType: String
    ): Result<FirebaseUser> {
        return try {
            // First create the user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            // Then update the profile
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
            )?.await()
            
            // Finally, store user data in Firestore
            result.user?.let { user ->
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .set(mapOf(
                            "userType" to userType,
                            "fullName" to fullName,
                            "email" to email,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        ))
                        .await()
                } catch (e: Exception) {
                    // If Firestore write fails, delete the user and throw the error
                    user.delete().await()
                    throw e
                }
            }
            
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserTypeFromFirestore(uid: String?): String? {
        if (uid == null) return null
        return try {
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            document.getString("userType")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }
} 