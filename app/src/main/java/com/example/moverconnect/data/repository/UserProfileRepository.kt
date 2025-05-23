package com.example.moverconnect.data.repository

import android.util.Log
import com.example.moverconnect.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserProfileRepository private constructor() {
    private val TAG = "UserProfileRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userProfilesCollection = firestore.collection("user_profiles")

    companion object {
        @Volatile
        private var instance: UserProfileRepository? = null

        fun getInstance(): UserProfileRepository {
            return instance ?: synchronized(this) {
                instance ?: UserProfileRepository().also { instance = it }
            }
        }
    }

    suspend fun getUserProfile(): Result<UserProfile?> = try {
        val currentUser = auth.currentUser
        Log.d(TAG, "Getting profile for user: ${currentUser?.email}, UID: ${currentUser?.uid}")
        
        val userId = currentUser?.uid ?: throw Exception("User not authenticated")
        
        val document = userProfilesCollection.document(userId)
            .get()
            .await()

        if (!document.exists()) {
            Log.d(TAG, "No existing profile found, creating new one")
            // Create a new profile document if it doesn't exist
            val newProfile = UserProfile(
                userId = userId,
                fullName = currentUser.displayName ?: "",
                email = currentUser.email ?: "",
                phoneNumber = currentUser.phoneNumber ?: ""
            )
            userProfilesCollection.document(userId)
                .set(newProfile)
                .await()
            Result.success(newProfile)
        } else {
            Log.d(TAG, "Found existing profile")
            Result.success(document.toObject(UserProfile::class.java))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting profile", e)
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> = try {
        val currentUser = auth.currentUser
        Log.d(TAG, "Updating profile for user: ${currentUser?.email}, UID: ${currentUser?.uid}")
        
        val userId = currentUser?.uid ?: throw Exception("User not authenticated")
        
        val updatedProfile = profile.copy(
            userId = userId,
            updatedAt = System.currentTimeMillis()
        )

        userProfilesCollection.document(userId)
            .set(updatedProfile)
            .await()

        Result.success(updatedProfile)
    } catch (e: Exception) {
        Log.e(TAG, "Error updating profile", e)
        e.printStackTrace()
        Result.failure(e)
    }
} 