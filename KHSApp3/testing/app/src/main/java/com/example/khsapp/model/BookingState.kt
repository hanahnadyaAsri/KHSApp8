package com.example.khsapp.model

data class BookingState(
    val selectedService: Services? = null,
    val selectedDoctor: Doctor? = null,
    val selectedDate: String = "",
    val selectedTime: String = "",
    val patientName: String = ""
)
