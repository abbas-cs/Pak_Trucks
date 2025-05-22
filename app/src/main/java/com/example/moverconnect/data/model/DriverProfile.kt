package com.example.moverconnect.data.model

import java.time.LocalDate

data class DriverProfile(
    val userId: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "", // Store as ISO string
    val yearsOfExperience: String = "",
    val phoneNumber: String = "",
    val whatsappNumber: String = "",
    val workingHoursFrom: String = "",
    val workingHoursTo: String = "",
    val truckType: String = "",
    val truckCapacity: String = "",
    val city: String = "",
    val area: String = "",
    val profileImageUrl: String = "",
    val vehicleImageUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 