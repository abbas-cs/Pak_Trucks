package com.example.moverconnect.ui.screens.driver

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moverconnect.data.model.DriverProfile
import com.example.moverconnect.data.repository.DriverProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DriverProfileViewModel : ViewModel() {
    private val TAG = "DriverProfileViewModel"
    private val repository = DriverProfileRepository.getInstance()
    
    private val _profile = MutableStateFlow<DriverProfile?>(null)
    val profile: StateFlow<DriverProfile?> = _profile

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
            
            try {
                repository.getDriverProfile()
                    .onSuccess { profile ->
                        _profile.value = profile
                    }
                    .onFailure { e ->
                        _error.value = e.message ?: "Failed to load profile"
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            // Optimistically update the UI immediately
            _profile.value = _profile.value?.copy(isAvailable = isAvailable)
            
            try {
                repository.updateDriverAvailability(isAvailable)
                    .onSuccess { updatedProfile ->
                        _profile.value = updatedProfile
                    }
                    .onFailure { e ->
                        // Revert the optimistic update on failure
                        loadProfile()
                        _error.value = e.message ?: "Failed to update availability"
                    }
            } catch (e: Exception) {
                // Revert the optimistic update on error
                loadProfile()
                _error.value = e.message ?: "An unexpected error occurred"
            }
        }
    }

    fun toggleAvailability() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val currentProfile = _profile.value ?: return@launch
                val updatedProfile = currentProfile.copy(
                    isAvailable = !currentProfile.isAvailable,
                    updatedAt = System.currentTimeMillis()
                )
                
                repository.saveDriverProfile(updatedProfile)
                    .onSuccess { savedProfile ->
                        _profile.value = savedProfile
                    }
                    .onFailure { e ->
                        _error.value = e.message ?: "Failed to update availability"
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun saveProfile(profile: DriverProfile) {
        _isLoading.value = true
        _error.value = null
        Log.d(TAG, "Saving profile")
        
        try {
            repository.saveDriverProfile(profile)
                .onSuccess { savedProfile ->
                    Log.d(TAG, "Profile saved successfully")
                    _profile.value = savedProfile
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to save profile", e)
                    _error.value = e.message ?: "Failed to save profile"
                }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error saving profile", e)
            _error.value = e.message ?: "An unexpected error occurred"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
} 