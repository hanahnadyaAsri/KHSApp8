package com.example.khsapp.model

data class TimeOff(
    val id: String = "",
    val doctorId: String = "",
    val date: String = "", // Format: "Day MonthName Year"
    val reason: String = ""
)
