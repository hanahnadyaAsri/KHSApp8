package com.example.khsapp.model

import com.google.firebase.firestore.PropertyName

data class Services(
    @get:PropertyName("serviceIds")
    val serviceId: String = "",
    
    val specialization: String = "",
    
    val price: Double = 0.0,
    
    val description: String = "",
    
    val duration: String = "30 min"
)
