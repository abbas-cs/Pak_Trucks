package com.example.moverconnect.data.repository

import android.util.Log
import com.example.moverconnect.data.model.DriverProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DriverProfileRepository private constructor() {
    private val TAG = "DriverProfileRepository"
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val driverProfilesCollection = db.collection("driver_profiles")

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

    suspend fun getDriverProfile(): Result<DriverProfile?> {
        return try {
        val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            
            Log.d(TAG, "Getting profile for user $userId")
        
            val document = driverProfilesCollection.document(userId).get().await()
        if (!document.exists()) {
                Log.e(TAG, "No profile found for user $userId")
                return Result.failure(Exception("Driver profile not found"))
            }
            
            val profile = document.toObject(DriverProfile::class.java)
            Log.d(TAG, "Retrieved profile availability: ${profile?.isAvailable}")
            
            Result.success(profile)
    } catch (e: Exception) {
            Log.e(TAG, "Error getting driver profile", e)
        Result.failure(e)
        }
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

    suspend fun updateDriverAvailability(isAvailable: Boolean): Result<DriverProfile> {
        return try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            
            Log.d(TAG, "Updating availability for user $userId to $isAvailable")
            
            // Get current profile
            val currentProfile = getDriverProfile().getOrThrow() ?: return Result.failure(Exception("Driver profile not found"))
            Log.d(TAG, "Current profile availability: ${currentProfile.isAvailable}")
            
            // Update in Firestore
            driverProfilesCollection.document(userId)
                .update(
                    mapOf(
                        "isAvailable" to isAvailable,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            
            Log.d(TAG, "Successfully updated availability in Firestore")
            
            // Get the updated profile
            val updatedProfile = getDriverProfile().getOrThrow()
            Log.d(TAG, "Updated profile availability: ${updatedProfile?.isAvailable}")
            
            if (updatedProfile?.isAvailable != isAvailable) {
                Log.e(TAG, "Update verification failed: expected $isAvailable but got ${updatedProfile?.isAvailable}")
                return Result.failure(Exception("Failed to verify availability update"))
            }
            
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating driver availability", e)
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DriverProfileRepository? = null

        fun getInstance(): DriverProfileRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = DriverProfileRepository()
                INSTANCE = instance
                instance
            }
        }
    }
} 