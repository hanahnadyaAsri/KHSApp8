package com.example.khsapp.repository

import com.example.khsapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")

    suspend fun saveUserWithId(authUid: String, user: User): Result<String> {
        return try {
            val counterRef = firestore.collection("counters").document("userCounter")
            
            val userId = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(counterRef)
                val newCount = if (snapshot.exists()) {
                    (snapshot.getLong("count") ?: 0) + 1
                } else {
                    1
                }
                transaction.set(counterRef, mapOf("count" to newCount))
                newCount
            }.await().let { count ->
                 String.format("U%03d", count)
            }
            
            // Format Name
            val formattedName = user.fullName.trim().lowercase().split("\\s+".toRegex())
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                
            // Prepare Map (Matching user request structure, minus password for security)
            val userMap = hashMapOf(
                "userId" to userId,
                "firebaseUid" to authUid,
                "fullName" to formattedName,
                "phone" to user.phone,
                "email" to user.email,
                "mailingAddress" to user.mailingAddress,
                "dateOfBirth" to user.dateOfBirth, // Fixed key to match User model
                "gender" to user.gender,
                "role" to "Patient",
                "status" to "Active",
                "profilePictureUrl" to user.profilePictureUrl
            )

            android.util.Log.d("UserRepository", "Saving user map: $userMap")
            usersCollection.document(authUid).set(userMap).await()
            
            Result.success(userId)

        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Transaction/Save Failed", e)
            
            // Fallback for Permission Denied on Counter
            try {
                android.util.Log.w("UserRepository", "Attempting fallback save...")
                 val fallbackId = "U-${java.util.UUID.randomUUID().toString().take(6).uppercase()}"
                 val userMap = hashMapOf(
                    "userId" to fallbackId,
                    "firebaseUid" to authUid,
                    "fullName" to user.fullName,
                    "phone" to user.phone,
                    "email" to user.email,
                    "mailingAddress" to user.mailingAddress,
                    "dateOfBirth" to user.dateOfBirth, // Added missing field
                    "gender" to user.gender, // Added missing field
                    "role" to "Patient",
                    "status" to "Active"
                )
                usersCollection.document(authUid).set(userMap).await()
                Result.success(fallbackId)
            } catch (fallbackEx: Exception) {
                 android.util.Log.e("UserRepository", "Fallback Save Failed", fallbackEx)
                 Result.failure(fallbackEx) // Return the fallback error to understand why even fallback failed
            }
        }
    }

    private suspend fun generateUserId(): String {
        val counterRef = firestore.collection("counters").document("user_counter")
        
        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val newCount = if (snapshot.exists()) {
                (snapshot.getLong("current") ?: 0) + 1
            } else {
                1
            }
            transaction.set(counterRef, mapOf("current" to newCount))
            newCount
        }.await().let { count ->
            "U${count.toString().padStart(3, '0')}"
        }
    }

    suspend fun getUser(authUid: String): User? {
        return try {
            val snapshot = usersCollection.document(authUid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePicture(uri: Uri, uid: String): Result<String> {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
            val profileRef = storageRef.child("profiles/patients/$uid.jpg")
            
            profileRef.putFile(uri).await()
            val downloadUrl = profileRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfilePicture(uid: String, url: String): Result<Unit> {
        return try {
            usersCollection.document(uid).update("profilePictureUrl", url).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
