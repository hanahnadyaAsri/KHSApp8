package com.example.khsapp.data

import com.example.khsapp.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AppointmentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection("bookings")

    fun getAppointments(): Flow<List<Appointment>> {
        return bookingsCollection.snapshots().map { snapshot ->
            snapshot.toObjects(Appointment::class.java)
        }
    }

    fun addAppointment(appointment: Appointment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        bookingsCollection.document(appointment.userId).set(appointment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    suspend fun cancelAppointment(appointmentId: String, reason: String) {
        try {
            val updates = mapOf(
                "IsCancelled" to true,
                "cancellationReason" to reason
            )
            bookingsCollection.document(appointmentId).update(updates).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateStatus(appointmentId: String, status: String) {
        try {
            bookingsCollection.document(appointmentId).update("status", status).await()
        } catch (e: Exception) {
             e.printStackTrace()
        }
    }
    suspend fun performMigration() {
        try {
            val snapshots = bookingsCollection.get().await()
            val batch = firestore.batch()

            for (doc in snapshots.documents) {
                val updates = mutableMapOf<String, Any>()
                
                // Add missing fields defaults
                if (!doc.contains("patientName")) updates["patientName"] = "Unknown Patient"
                if (!doc.contains("IsCancelled")) updates["IsCancelled"] = false
                if (!doc.contains("cancellationReason")) updates["cancellationReason"] = ""
                if (!doc.contains("timestamp")) updates["timestamp"] = com.google.firebase.Timestamp.now()
                
                // Analytics defaults
                if (!doc.contains("patientAge")) updates["patientAge"] = 25

                // Strict Removal
                if (doc.contains("feedbackSubmitted")) {
                    updates["feedbackSubmitted"] = com.google.firebase.firestore.FieldValue.delete()
                }

                if (updates.isNotEmpty()) {
                    batch.update(doc.reference, updates)
                }
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
