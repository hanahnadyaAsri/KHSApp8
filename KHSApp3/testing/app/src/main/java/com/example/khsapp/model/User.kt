package com.example.khsapp.model

data class User(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val mailingAddress: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val role: String = "Patient",
    val status: String = "Active",
    val profilePictureUrl: String = ""
)
