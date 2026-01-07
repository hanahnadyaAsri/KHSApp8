package com.example.khsapp.model
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentReference

data class Doctor(
    @get:PropertyName("doctorId")
    val doctorId: String = "",
    
    @get:PropertyName("doctorName")
    val doctorName: String = "",
    
    @get:PropertyName("drspecialization")
    val drSpecialization: String = "",
    
    @get:PropertyName("drGender")
    val drGender: String = "Male",
    
    @get:PropertyName("yearOfExperience")
    val yearOfExperience: Any = 0,
    
    // Service IDs associated with this doctor (can be Strings or DocumentReferences)
    @get:PropertyName("serviceIds")
    val serviceIds: List<Any> = emptyList(),
    
    // Default values for UI items not present in this schema
    val description: String = "", 
    val rating: Double = 5.0,
    
    @get:PropertyName("profileImageUrl")
    val profileImageUrl: String = ""
) {
    fun providesService(targetServiceId: String): Boolean {
        val parsedIds = serviceIds.map { id ->
            when (id) {
                is String -> id
                is DocumentReference -> id.path
                else -> id.toString()
            }
        }
        // Checks if the list contains the ID directly OR the path "/Services/$targetServiceId"
        return parsedIds.contains(targetServiceId) || parsedIds.contains("Services/$targetServiceId") || parsedIds.contains("/Services/$targetServiceId")
    }
}
