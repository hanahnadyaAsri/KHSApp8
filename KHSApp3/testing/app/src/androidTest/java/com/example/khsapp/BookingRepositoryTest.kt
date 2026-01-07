package com.example.khsapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.khsapp.model.Appointment
import com.example.khsapp.repository.BookingRepository
import com.example.khsapp.model.PaymentMethod
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookingRepositoryTest {

    @Test
    fun testAddBookingPreservesUserId() = runBlocking {
        val repository = BookingRepository()
        val expectedUserId = "PATIENT_TEST_123"
        val appointment = Appointment(
            userId = expectedUserId,
            doctorName = "Test Doctor",
            serviceName = "Test Service",
            date = "2024-01-01",
            time = "10:00"
        )

        val result = repository.addBooking(appointment)
        val bookingId = result.getOrThrow()

        // Verify in Firestore
        val firestore = FirebaseFirestore.getInstance()
        val snapshot = firestore.collection("bookings").document(bookingId).get().await()
        val savedAppointment = snapshot.toObject(Appointment::class.java)

        assertEquals("UserId should match the patient ID", expectedUserId, savedAppointment?.userId)
        assertNotEquals("UserId should NOT match the Booking ID", bookingId, savedAppointment?.userId)
        
        // Cleanup
        firestore.collection("bookings").document(bookingId).delete().await()
    }
}
