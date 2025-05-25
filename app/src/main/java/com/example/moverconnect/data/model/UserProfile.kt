package com.example.moverconnect.data.model

data class UserProfile(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val coverPhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 