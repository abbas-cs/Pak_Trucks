package com.example.moverconnect.data.repository

import android.util.Log
import com.example.moverconnect.data.model.DriverProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DriverProfileRepository private constructor() {
    private val TAG = "DriverProfileRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val driverProfilesCollection = firestore.collection("driver_profiles")

    companion object {
        @Volatile
        private var instance: DriverProfileRepository? = null

        fun getInstance(): DriverProfileRepository {
            return instance ?: synchronized(this) {
                instance ?: DriverProfileRepository().also { instance = it }
            }
        }
    }

    suspend fun saveDriverProfile(profile: DriverProfile): Result<DriverProfile> = try {
        val currentUser = auth.currentUser
        Log.d(TAG, "Current user: ${currentUser?.email}, UID: ${currentUser?.uid}")
        
        val userId = currentUser?.uid ?: throw Exception("User not authenticated")
        Log.d(TAG, "Saving profile for user: $userId")
        Log.d(TAG, "Profile data: $profile")

        // Validate required fields
        if (profile.fullName.isBlank() || profile.phoneNumber.isBlank() || 
            profile.truckType.isBlank() || profile.truckCapacity.isBlank() ||
            profile.city.isBlank() || profile.area.isBlank()) {
            throw Exception("Please fill in all required fields")
        }

        // Create updated profile
        val updatedProfile = profile.copy(
            userId = userId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        Log.d(TAG, "Created updated profile object: $updatedProfile")

        // Save to Firestore
        Log.d(TAG, "Saving to Firestore at path: driver_profiles/$userId")
        try {
            driverProfilesCollection.document(userId)
                .set(updatedProfile)
                .await()
            Log.d(TAG, "Successfully saved to Firestore")
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to Firestore", e)
            Result.failure(e)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error in saveDriverProfile", e)
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getDriverProfile(): Result<DriverProfile?> = try {
        val currentUser = auth.currentUser
        Log.d(TAG, "Getting profile for user: ${currentUser?.email}, UID: ${currentUser?.uid}")
        
        val userId = currentUser?.uid ?: throw Exception("User not authenticated")
        
        val document = driverProfilesCollection.document(userId)
            .get()
            .await()

        if (!document.exists()) {
            Log.d(TAG, "No existing profile found, creating new one")
            // Create a new profile document if it doesn't exist
            val newProfile = DriverProfile(userId = userId)
            driverProfilesCollection.document(userId)
                .set(newProfile)
                .await()
            Result.success(newProfile)
        } else {
            Log.d(TAG, "Found existing profile")
            Result.success(document.toObject(DriverProfile::class.java))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting profile", e)
        e.printStackTrace()
        Result.failure(e)
    }

    fun getAllActiveDriverProfiles(): Flow<List<DriverProfile>> = callbackFlow {
        try {
            val subscription = driverProfilesCollection
                .whereNotEqualTo("fullName", "") // Only get profiles with names
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening for driver profiles", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val profiles = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(DriverProfile::class.java)
                        }.filter { profile ->
                            // Additional validation to ensure profile is complete
                            profile.fullName.isNotBlank() &&
                            profile.phoneNumber.isNotBlank() &&
                            profile.truckType.isNotBlank() &&
                            profile.truckCapacity.isNotBlank() &&
                            profile.city.isNotBlank() &&
                            profile.area.isNotBlank()
                        }
                        trySend(profiles)
                    }
                }

            // Clean up the listener when the flow is cancelled
            awaitClose {
                subscription.remove()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all driver profiles", e)
            trySend(emptyList())
            close(e)
        }
    }
} 