package com.example.moverconnect.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moverconnect.data.model.Review
import com.example.moverconnect.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewState(
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReviewViewModel(
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val TAG = "ReviewViewModel"
    private val _state = MutableStateFlow(ReviewState())
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    fun loadDriverReviews(driverId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading reviews for driver: $driverId")
                _state.update { it.copy(isLoading = true, error = null) }
                
                // Load top reviews
                reviewRepository.getTopReviews(driverId).fold(
                    onSuccess = { reviews ->
                        Log.d(TAG, "Successfully loaded ${reviews.size} reviews")
                        _state.update { it.copy(reviews = reviews) }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error loading reviews", error)
                        _state.update { it.copy(error = error.message) }
                    }
                )

                // Load average rating
                reviewRepository.getAverageRating(driverId).fold(
                    onSuccess = { rating ->
                        Log.d(TAG, "Successfully loaded average rating: $rating")
                        _state.update { it.copy(averageRating = rating) }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error loading average rating", error)
                        _state.update { it.copy(error = error.message) }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in loadDriverReviews", e)
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addReview(
        driverId: String,
        customerId: String,
        customerName: String,
        customerProfileImageUrl: String,
        rating: Float,
        comment: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Adding review for driver: $driverId")
                _state.update { it.copy(isLoading = true, error = null) }
                
                val review = Review(
                    driverId = driverId,
                    customerId = customerId,
                    customerName = customerName,
                    customerProfileImageUrl = customerProfileImageUrl,
                    rating = rating,
                    comment = comment
                )

                reviewRepository.addReview(review).fold(
                    onSuccess = { newReview ->
                        Log.d(TAG, "Successfully added review: $newReview")
                        // Add the new review to the existing list
                        _state.update { currentState ->
                            currentState.copy(
                                reviews = listOf(newReview) + currentState.reviews,
                                isLoading = false
                            )
                        }
                        // Reload all reviews to ensure consistency
                        loadDriverReviews(driverId)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error adding review", error)
                        _state.update { it.copy(error = error.message, isLoading = false) }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in addReview", e)
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    class ReviewViewModelFactory(
        private val reviewRepository: ReviewRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                return ReviewViewModel(reviewRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 