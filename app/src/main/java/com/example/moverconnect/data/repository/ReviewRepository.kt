package com.example.moverconnect.data.repository

import android.util.Log
import com.example.moverconnect.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class ReviewRepository {
    private val TAG = "ReviewRepository"
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    init {
        // Log the Firestore instance and collection reference
        Log.d(TAG, "Firestore instance initialized: ${db.app.name}")
        Log.d(TAG, "Reviews collection path: ${reviewsCollection.path}")
    }

    suspend fun addReview(review: Review): Result<Review> {
        return try {
            Log.d(TAG, "Starting to add review: $review")
            val reviewWithId = review.copy(id = UUID.randomUUID().toString())
            Log.d(TAG, "Generated review ID: ${reviewWithId.id}")
            
            // Log the document path
            val docRef = reviewsCollection.document(reviewWithId.id)
            Log.d(TAG, "Document path: ${docRef.path}")
            
            // Log the data being saved
            Log.d(TAG, "Saving review data: ${reviewWithId.toMap()}")
            
            docRef.set(reviewWithId).await()
            Log.d(TAG, "Successfully added review with ID: ${reviewWithId.id}")
            
            // Verify the document was saved
            val savedDoc = docRef.get().await()
            Log.d(TAG, "Verification - Document exists: ${savedDoc.exists()}")
            Log.d(TAG, "Verification - Document data: ${savedDoc.data}")
            
            Result.success(reviewWithId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding review", e)
            Log.e(TAG, "Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getDriverReviews(driverId: String): Result<List<Review>> {
        return try {
            Log.d(TAG, "Getting reviews for driver: $driverId")
            val snapshot = reviewsCollection
                .whereEqualTo("driverId", driverId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            Log.d(TAG, "Query returned ${snapshot.documents.size} documents")
            val reviews = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Processing document: ${doc.id}")
                doc.toObject(Review::class.java)
            }
            Log.d(TAG, "Successfully retrieved ${reviews.size} reviews")
            Result.success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting driver reviews", e)
            Log.e(TAG, "Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getTopReviews(driverId: String, limit: Int = 3): Result<List<Review>> {
        return try {
            Log.d(TAG, "Getting top reviews for driver: $driverId")
            val snapshot = reviewsCollection
                .whereEqualTo("driverId", driverId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            Log.d(TAG, "Query returned ${snapshot.documents.size} documents")
            val reviews = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Processing document: ${doc.id}")
                doc.toObject(Review::class.java)
            }
            Log.d(TAG, "Successfully retrieved ${reviews.size} top reviews")
            Result.success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting top reviews", e)
            Log.e(TAG, "Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getAverageRating(driverId: String): Result<Float> {
        return try {
            Log.d(TAG, "Getting average rating for driver: $driverId")
            val snapshot = reviewsCollection
                .whereEqualTo("driverId", driverId)
                .get()
                .await()
            
            Log.d(TAG, "Query returned ${snapshot.documents.size} documents")
            val reviews = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Processing document: ${doc.id}")
                doc.toObject(Review::class.java)
            }
            
            val average = if (reviews.isEmpty()) {
                Log.d(TAG, "No reviews found, returning 0")
                0f
            } else {
                val avg = reviews.map { it.rating }.average().toFloat()
                Log.d(TAG, "Calculated average rating: $avg from ${reviews.size} reviews")
                avg
            }
            Result.success(average)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting average rating", e)
            Log.e(TAG, "Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
} 