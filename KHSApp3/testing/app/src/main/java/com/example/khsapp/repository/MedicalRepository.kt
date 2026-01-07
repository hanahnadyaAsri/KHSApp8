package com.example.khsapp.repository

import com.example.khsapp.model.Doctor
import com.example.khsapp.model.Services
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MedicalRepository {
    private val db = FirebaseFirestore.getInstance()
    private val servicesRef = db.collection("Services")
    private val doctorsRef = db.collection("Doctors") // Capitalized "Doctors"

    suspend fun getServices(): Result<List<Services>> {
        return try {
            val snapshot = servicesRef.get().await()
            val services = snapshot.toObjects(Services::class.java)
            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDoctorsByServiceId(serviceId: String): Result<List<Doctor>> {
        return try {
            // Fetch all doctors and filter client-side for flexibility with path strings vs plain IDs
            val snapshot = doctorsRef.get().await()
            val allDoctors = snapshot.toObjects(Doctor::class.java)
            
            val filteredDoctors = allDoctors.filter { doc ->
                doc.providesService(serviceId)
            }
            
            Result.success(filteredDoctors)
        } catch (e: Exception) {
             Result.failure(e)
        }
    }
}
