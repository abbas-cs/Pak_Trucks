package com.example.moverconnect.data.model

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",
    val driverId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: Timestamp = Timestamp.now(),
    val customerProfileImageUrl: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "driverId" to driverId,
            "customerId" to customerId,
            "customerName" to customerName,
            "rating" to rating,
            "comment" to comment,
            "date" to date,
            "customerProfileImageUrl" to customerProfileImageUrl
        )
    }
} 