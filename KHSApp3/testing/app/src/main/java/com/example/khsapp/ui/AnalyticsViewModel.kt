package com.example.khsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khsapp.repository.BookingRepository
import com.example.khsapp.repository.UserRepository // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Period
import java.util.Locale

data class AnalyticsUiState(
    val totalPatients: Int = 0,
    val avgPatientAge: Double = 0.0,
    val weeklyIncome: List<Float> = emptyList(), // Last 7 days or similar
    val totalIncome: Double = 0.0,
    val genderDistribution: Map<String, Float> = emptyMap(),
    val serviceUsage: Map<String, Int> = emptyMap(),
    val appointmentsMonthly: Map<String, Int> = emptyMap(),
    val ageDistribution: Map<String, Float> = emptyMap(),
    val topDoctors: Map<String, Float> = emptyMap()
)

class AnalyticsViewModel : ViewModel() {
    private val bookingRepository = BookingRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        fetchAnalytics()
    }

    private fun fetchAnalytics() {
        viewModelScope.launch {
            try {
                // Fetch Appointments
                var validAppointments: List<com.example.khsapp.model.Appointment> = emptyList()
                bookingRepository.getAppointments().collect { appointments ->
                     validAppointments = appointments.filter { !it.isCancelled }
                }

                // Fetch Users
                val usersResult = userRepository.getAllUsers()
                val allUsers = usersResult.getOrElse { emptyList() }
                
                // Filter Users: Only Patients
                val patientUsers = allUsers.filter { it.role.equals("Patient", ignoreCase = true) }


                // --- 1. Total Patients (Count of Patient Users) ---
                val totalPatients = patientUsers.count()

                // --- 2. Calculate Ages from DateOfBirth ---
                // Format assumed: dd/MM/yyyy
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                val today = LocalDate.now()
                
                val ages = patientUsers.mapNotNull { user ->
                    try {
                        if (user.dateOfBirth.isNotBlank()) {
                            val birthDate = LocalDate.parse(user.dateOfBirth, dateFormatter)
                            Period.between(birthDate, today).years
                        } else null
                    } catch (e: Exception) {
                        null // Handle parsing errors gracefully
                    }
                }

                val avgAge = if (ages.isNotEmpty()) ages.average() else 0.0

                // --- 3. Age Distribution (Pie/Groups) ---
                var ageChild = 0f    // 0-12
                var ageTeen = 0f     // 13-19
                var ageYoungAdult = 0f // 20-39
                var ageAdult = 0f    // 40-59
                var ageSenior = 0f   // 60+
                
                ages.forEach { age ->
                    when {
                        age <= 12 -> ageChild++
                        age <= 19 -> ageTeen++
                        age <= 39 -> ageYoungAdult++
                        age <= 59 -> ageAdult++
                        else -> ageSenior++
                    }
                }
                
                // Construct map for non-zero groups
                val ageGroups = mutableMapOf<String, Float>()
                if(ageChild > 0) ageGroups["Child (0-12)"] = ageChild
                if(ageTeen > 0) ageGroups["Teen (13-19)"] = ageTeen
                if(ageYoungAdult > 0) ageGroups["Adult (20-39)"] = ageYoungAdult
                if(ageAdult > 0) ageGroups["Mid-Age (40-59)"] = ageAdult
                if(ageSenior > 0) ageGroups["Senior (60+)"] = ageSenior


                // --- 4. Gender Distribution (from Users) ---
                val genderMap = patientUsers
                    .groupingBy { it.gender.ifBlank { "Unknown" } }
                    .eachCount()
                    .mapValues { it.value.toFloat() }


                // --- 5. Total Income (from Bookings) ---
                val totalIncome = validAppointments.sumOf { it.price }

                // --- 6. Service Trends (from Bookings) ---
                // Using serviceName or serviceId. Using name for readable charts.
                val serviceMap = validAppointments
                    .groupingBy { it.serviceName.ifBlank { "Unknown Service" } }
                    .eachCount()

                // --- 7. Monthly Visits (from Bookings) ---
                val monthlyMap = mutableMapOf<String, Int>()
                validAppointments.forEach { appt ->
                   try {
                        if (appt.date.isNotBlank()) {
                             val dateParts = appt.date.split("/")
                             if (dateParts.size == 3) {
                                 // Basic month extraction
                                 val monthNum = dateParts[1]
                                 val monthName = when(monthNum) {
                                    "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
                                    "05" -> "May"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Aug"
                                    "09" -> "Sep"; "10" -> "Oct"; "11" -> "Nov"; "12" -> "Dec"
                                    else -> "Unk"
                                 }
                                 monthlyMap[monthName] = (monthlyMap[monthName] ?: 0) + 1
                             }
                        }
                   } catch (e: Exception) { /* Ignore bad dates */ }
                }

                // --- 8. Top Doctors (from Bookings) ---
                val doctorsCount = validAppointments
                    .groupingBy { it.doctorName.ifBlank { "Unknown Doctor" } }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .take(5) // Top 5
                
                // Convert to map for charts (normalize relative to max if needed by UI, or just raw counts)
                // Existing UI expects Float values (implied relative scale or raw)
                val topDoctorsMap = doctorsCount.associate { it.key to it.value.toFloat() }

                
                _uiState.value = AnalyticsUiState(
                    totalPatients = totalPatients,
                    avgPatientAge = avgAge,
                    totalIncome = totalIncome,
                    serviceUsage = serviceMap,
                    appointmentsMonthly = monthlyMap,
                    ageDistribution = ageGroups,
                    genderDistribution = genderMap,
                    topDoctors = topDoctorsMap
                )

            } catch (e: Exception) {
                android.util.Log.e("AnalyticsViewModel", "Error fetching analytics", e)
            }
        }
    }
}
