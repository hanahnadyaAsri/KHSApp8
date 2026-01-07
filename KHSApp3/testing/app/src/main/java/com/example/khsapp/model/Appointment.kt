package com.example.khsapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Appointment(
    @DocumentId
    val id: String = "",
    val userId: String = "", // Added just in case it's needed as separate field, but 'id' is doc id
    val doctorId: String = "", // Added for referencing doctor
    val doctorName: String = "",
    val patientName: String = "", // Requested field
    val serviceName: String = "",
    val serviceId: String = "", // Added for referencing service
    val date: String = "",
    val time: String = "",
    val timestamp: Timestamp? = null, // Requested field (nullable to handle existing data)
    
    // Status & Cancellation
    val status: String = "Upcoming", // Keep for backwards compatibility or UI display logic
    val cancellationReason: String = "",
    @get:com.google.firebase.firestore.PropertyName("IsCancelled")
    val isCancelled: Boolean = false,

    // Analytics Support
    val patientAge: Int = 0,
    val price: Double = 0.0
)
