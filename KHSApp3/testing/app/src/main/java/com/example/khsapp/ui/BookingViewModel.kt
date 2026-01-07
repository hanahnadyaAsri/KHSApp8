package com.example.khsapp.ui

import androidx.lifecycle.ViewModel
import com.example.khsapp.model.Appointment
import com.example.khsapp.model.Doctor
import com.example.khsapp.model.Services
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookingViewModel : ViewModel() {
    private val _bookingState = MutableStateFlow(Appointment())
    val bookingState: StateFlow<Appointment> = _bookingState.asStateFlow()

    fun setService(service: Services) {
        _bookingState.value = _bookingState.value.copy(
            serviceName = service.specialization,
            serviceId = service.serviceId,
            price = service.price
        )
    }

     fun setDoctor(doctor: Doctor) {
        _bookingState.value = _bookingState.value.copy(
            doctorName = doctor.doctorName,
             doctorId = doctor.doctorId 
        )
    }

    fun setTime(date: String, time: String) {
        _bookingState.value = _bookingState.value.copy(date = date, time = time)
    }
    
    fun setPatientName(name: String) {
        _bookingState.value = _bookingState.value.copy(patientName = name)
    }

    fun reset() {
        _bookingState.value = Appointment()
    }
}
