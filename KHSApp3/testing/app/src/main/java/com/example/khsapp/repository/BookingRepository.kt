package com.example.khsapp.repository

import com.example.khsapp.model.Appointment
import com.example.khsapp.model.TimeOff
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val timeOffCollection = firestore.collection("timeOff")
    private val bookingsCollection = firestore.collection("bookings")

    // --- Time Off (Existing) ---
    suspend fun getTimeOff(doctorId: String, month: String, year: Int): List<TimeOff> {
        return try {
            val snapshot = timeOffCollection
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()
            
            snapshot.toObjects(TimeOff::class.java).filter { 
                it.date.contains(month) && it.date.contains(year.toString()) 
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTimeOff(timeOff: TimeOff) {
        val docRef = timeOffCollection.document()
        val newTimeOff = timeOff.copy(id = docRef.id)
        docRef.set(newTimeOff).await()
    }

    suspend fun deleteTimeOff(id: String) {
        timeOffCollection.document(id).delete().await()
    }

    // --- Appointments (Merged from AppointmentRepository) ---

    fun getAppointments(): Flow<List<Appointment>> {
        return bookingsCollection.snapshots().map { snapshot ->
            snapshot.toObjects(Appointment::class.java)
        }
    }

    // Generate ID Transactionally (Callback based - kept for existing code compatibility)
    private fun generateId(prefix: String, counterDoc: String, padding: Int, onComplete: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val counterRef = firestore.collection("counters").document(counterDoc)
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val newCount = if (snapshot.exists()) {
                (snapshot.getLong("current") ?: 0) + 1
            } else {
                1
            }
            transaction.set(counterRef, mapOf("current" to newCount))
            newCount
        }.addOnSuccessListener { count ->
            val id = "$prefix${count.toString().padStart(padding, '0')}"
            onComplete(id)
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    // Generate ID Suspend Version for Coroutines
    private suspend fun generateIdSuspend(prefix: String, counterDoc: String, padding: Int): String {
        val counterRef = firestore.collection("counters").document(counterDoc)
        val count = firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val newCount = if (snapshot.exists()) {
                (snapshot.getLong("current") ?: 0) + 1
            } else {
                1
            }
            transaction.set(counterRef, mapOf("current" to newCount))
            newCount
        }.await()
        return "$prefix${count.toString().padStart(padding, '0')}"
    }

    fun addAppointment(appointment: Appointment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        generateId("B", "booking_counter", 7, 
            onComplete = { newId ->
                val newAppointment = appointment
                bookingsCollection.document(newId).set(newAppointment)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            },
            onFailure = { onFailure(it) }
        )
    }

    // --- New Methods Requested by User ---

    suspend fun addBooking(appointment: com.example.khsapp.model.Appointment): Result<String> {
        return try {
             // Use Auto-ID Generation
             val bookingRef = bookingsCollection.document()
             val bookingId = bookingRef.id
             
             // We use the Appointment model directly, but ensure ID and Timestamp are set
             val finalAppointment = appointment.copy(
                 timestamp = com.google.firebase.Timestamp.now()
             )
            
            bookingsCollection.document(bookingId).set(finalAppointment).await()
            android.util.Log.d("BookingRepo", "Booking added with ID: $bookingId")
            Result.success(bookingId)
            
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "Exception adding booking", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun addDoctor(doctor: com.example.khsapp.model.Doctor): Result<String> {
        return try {
            val doctorId = generateIdSuspend("D", "doctor_counter", 3) // D001, D002...
            val newDoctor = doctor.copy(doctorId = doctorId)
            
            firestore.collection("Doctors").document(doctorId).set(newDoctor).await()
            Result.success(doctorId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPayment(payment: com.example.khsapp.model.Payment): Result<String> {
        return try {
            val paymentsCollection = firestore.collection("payments")
            val paymentRef = paymentsCollection.add(payment).await()
            android.util.Log.d("BookingRepo", "Payment added with ID: ${paymentRef.id}")
            Result.success(paymentRef.id)
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "Error adding payment", e)
            Result.failure(e)
        }
    }

    suspend fun confirmBooking(
        appointment: com.example.khsapp.model.Appointment, 
        paymentMethod: com.example.khsapp.model.PaymentMethod,
        totalAmount: Double
    ): Result<String> {
        return try {
            // 1. Generate ID (Try Sequential -> Fallback to Auto-ID)
            val bookingId = try {
                generateIdSuspend("B", "booking_counter", 7)
            } catch (e: Exception) {
                android.util.Log.e("BookingRepo", "Counter failed, using fallback ID", e)
                bookingsCollection.document().id
            }
            
            val bookingRef = bookingsCollection.document(bookingId)
            val batch = firestore.batch()
            
            // 2. Prepare Booking Reference & Data
            val finalAppointment = appointment.copy(
                 timestamp = com.google.firebase.Timestamp.now()
            )
            
            // 3. Prepare Payment Reference & Data
            val paymentsCollection = firestore.collection("payments")
            val paymentRef = paymentsCollection.document()
            
            val payment = com.example.khsapp.model.Payment(
                id = paymentRef.id,
                bookingId = bookingId,
                amount = totalAmount,
                method = paymentMethod,
                status = com.example.khsapp.model.PaymentStatus.COMPLETED,
                transactionId = java.util.UUID.randomUUID().toString()
            )
            
            // 4. Batch Writes
            batch.set(bookingRef, finalAppointment)
            batch.set(paymentRef, payment)
            
            // 5. Commit
            batch.commit().await()
            
            android.util.Log.d("BookingRepo", "Batch confirm success: $bookingId")
            Result.success(bookingId)
            
        } catch (e: Throwable) {
            android.util.Log.e("BookingRepo", "Batch confirm failed", e)
            e.printStackTrace()
            Result.failure(Exception(e)) // Wrap in Exception for Result.failure
        }
    }

    fun cancelAppointment(
        appointmentId: String, 
        reason: String, 
        date: String? = null, 
        time: String? = null, 
        doctorName: String? = null,
        onResult: ((Result<Unit>) -> Unit)? = null
    ) {
        val updates = mapOf(
            "IsCancelled" to true,
            "cancellationReason" to reason
        )
        bookingsCollection.document(appointmentId).update(updates)
            .addOnSuccessListener {
                generateId("O", "timeoff_counter", 8,
                    onComplete = { timeOffId ->
                        val timeOffRecord = hashMapOf(
                            "id" to timeOffId,
                            "bookingId" to appointmentId,
                            "reason" to reason,
                            "date" to (date ?: ""),
                            "time" to (time ?: ""),
                            "doctorName" to (doctorName ?: ""),
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )
                        timeOffCollection.document(timeOffId).set(timeOffRecord)
                            .addOnSuccessListener { onResult?.invoke(Result.success(Unit)) }
                            .addOnFailureListener { e -> 
                                onResult?.invoke(Result.failure(e)) 
                            }
                    },
                    onFailure = { e -> 
                        e.printStackTrace()
                        onResult?.invoke(Result.failure(e))
                    }
                )
            }
            .addOnFailureListener { e -> 
                e.printStackTrace() 
                onResult?.invoke(Result.failure(e))
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
                if (!doc.contains("patientName")) updates["patientName"] = "Unknown Patient"
                if (!doc.contains("IsCancelled")) updates["IsCancelled"] = false
                if (!doc.contains("cancellationReason")) updates["cancellationReason"] = ""
                if (!doc.contains("timestamp")) updates["timestamp"] = com.google.firebase.Timestamp.now()
                if (!doc.contains("patientAge")) updates["patientAge"] = 25
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
