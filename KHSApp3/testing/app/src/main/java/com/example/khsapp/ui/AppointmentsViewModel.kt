package com.example.khsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khsapp.repository.BookingRepository
import com.example.khsapp.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppointmentsViewModel : ViewModel() {
    private val repository = BookingRepository()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    init {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        viewModelScope.launch {
            try {
                repository.getAppointments().collect { list ->
                    // Auto-complete logic
                    list.forEach { appointment ->
                        if (!appointment.isCancelled && appointment.status == "Upcoming" && isPast(appointment.date, appointment.time)) {
                            launch {
                                repository.updateStatus(appointment.userId, "Completed")
                            }
                        }
                    }
                    _appointments.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("AppointmentsViewModel", "Error fetching appointments", e)
            }
        }
    }

    fun cancelAppointment(appointmentId: String, reason: String, onResult: (Result<Unit>) -> Unit) {
        // Since repository.cancelAppointment is not a suspend function but callback-based,
        // we call it directly (not inside launch, although inside launch is fine too if it's blocking, but this is async).
        // However, repo.cancelAppointment is void return, so we assume it handles its threads or main thread.
        // To be safe and consistent with ViewModel scope:
        repository.cancelAppointment(appointmentId, reason, onResult = onResult)
    }

    private fun isPast(dateStr: String, timeStr: String): Boolean {
        return try {
            // Try standard formats
            val dateTimeStr = "$dateStr $timeStr"
            val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val date = format.parse(dateTimeStr)
            date != null && date.before(java.util.Date())
        } catch (e: Exception) {
            try {
                 // Fallback format if needed
                 val dateTimeStr = "$dateStr $timeStr"
                 val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                 val date = format.parse(dateTimeStr)
                 date != null && date.before(java.util.Date())
            } catch (e2: Exception) {
                false
            }
        }
    }
}
