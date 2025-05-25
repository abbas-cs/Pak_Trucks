package com.example.moverconnect.ui.screens.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moverconnect.data.model.UserProfile
import com.example.moverconnect.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerProfileViewModel : ViewModel() {
    private val TAG = "CustomerProfileViewModel"
    private val repository = UserProfileRepository.getInstance()
    
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d(TAG, "Loading profile")
            
            try {
                repository.getUserProfile()
                    .onSuccess { profile ->
                        Log.d(TAG, "Profile loaded successfully")
                        _profile.value = profile
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Failed to load profile", e)
                        _error.value = e.message ?: "Failed to load profile"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading profile", e)
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        phoneNumber: String,
        address: String,
        bio: String
    ) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        Log.d(TAG, "Updating profile")
        
        try {
            val updatedProfile = UserProfile(
                fullName = fullName,
                email = email,
                phoneNumber = phoneNumber,
                address = address,
                bio = bio
            )
            
            repository.updateUserProfile(updatedProfile)
                .onSuccess { profile ->
                    Log.d(TAG, "Profile updated successfully")
                    _profile.value = profile
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to update profile", e)
                    _error.value = e.message ?: "Failed to update profile"
                }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error updating profile", e)
            _error.value = e.message ?: "An unexpected error occurred"
        } finally {
            _isLoading.value = false
        }
    }
} 