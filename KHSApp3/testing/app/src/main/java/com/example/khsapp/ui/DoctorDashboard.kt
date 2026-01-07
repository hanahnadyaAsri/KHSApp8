package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.TimeOff
import com.example.khsapp.repository.BookingRepository
import com.example.khsapp.model.Doctor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboard(
    doctor: Doctor,
    repository: BookingRepository,
    onBack: () -> Unit,
    onViewDoctorSchedule: () -> Unit,
    onViewStaffSchedule: () -> Unit,
    onViewAnalytics: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Calendar Generation Logic
    val calendars = remember {
        val list = mutableListOf<java.util.Calendar>()
        val current = java.util.Calendar.getInstance()
        for (i in 0 until 3) {
            val cal = current.clone() as java.util.Calendar
            cal.add(java.util.Calendar.MONTH, i)
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1) 
            list.add(cal)
        }
        list
    }

    // State
    var allTimeOff by remember { mutableStateOf<Map<String, List<TimeOff>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Time Off
    LaunchedEffect(doctor.doctorId) {
        val tempMap = mutableMapOf<String, List<TimeOff>>()
        calendars.forEach { cal ->
            val monthStr = getMonthName(cal.get(java.util.Calendar.MONTH))
            val yearVal = cal.get(java.util.Calendar.YEAR)
            val list = repository.getTimeOff(doctor.doctorId, monthStr, yearVal)
            tempMap["$monthStr $yearVal"] = list
        }
        allTimeOff = tempMap
        isLoading = false
    }

    DoctorDashboardContent(
        doctor = doctor,
        calendars = calendars,
        allTimeOff = allTimeOff,
        onBack = onBack,
        onViewDoctorSchedule = onViewDoctorSchedule,
        onViewStaffSchedule = onViewStaffSchedule,
        onViewAnalytics = onViewAnalytics,
        onToggleAvailability = { day, monthStr, yearVal, isCurrentlyOff ->
             scope.launch {
                 val dateString = "$day $monthStr $yearVal"
                 if (isCurrentlyOff) {
                     // Remove
                     val existingOff = allTimeOff["$monthStr $yearVal"]?.find { it.date == dateString }
                     if (existingOff != null) {
                         repository.deleteTimeOff(existingOff.id)
                     }
                 } else {
                     // Add
                     repository.addTimeOff(
                         TimeOff(
                             doctorId = doctor.doctorId,
                             date = dateString,
                             reason = "Doctor Update"
                         )
                     )
                 }
                 // Refresh (Optimistic update or re-fetch)
                 val list = repository.getTimeOff(doctor.doctorId, monthStr, yearVal)
                 allTimeOff = allTimeOff.toMutableMap().apply { put("$monthStr $yearVal", list) }
             }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardContent(
    doctor: Doctor,
    calendars: List<java.util.Calendar>,
    allTimeOff: Map<String, List<TimeOff>>,
    onBack: () -> Unit,
    onViewDoctorSchedule: () -> Unit,
    onViewStaffSchedule: () -> Unit,
    onViewAnalytics: () -> Unit,
    onToggleAvailability: (Int, String, Int, Boolean) -> Unit
) {
    var selectedDayToToggle by remember { mutableStateOf<Triple<Int, String, Int>?>(null) } // Day, MonthStr, Year
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Schedule", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
                    }
                },
                actions = {
                    TextButton(onClick = onViewAnalytics) {
                        Text("Analytics", color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = onViewDoctorSchedule) {
                        Text("My Schedule", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onViewStaffSchedule) {
                        Text("Staff Schedule", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Stats / Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Doctor: ${doctor.doctorName}", fontWeight = FontWeight.Bold)
                    Text("Availability: Next 3 Months", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap a date to mark it as UNAVAILABLE.", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(calendars.size) { index ->
                    val cal = calendars[index]
                    val monthVal = cal.get(java.util.Calendar.MONTH)
                    val yearVal = cal.get(java.util.Calendar.YEAR)
                    val monthName = getMonthName(monthVal)
                    val daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                    val firstDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
                    val startOffset = firstDayOfWeek - 1

                    val timeOffs = allTimeOff["$monthName $yearVal"] ?: emptyList()

                    MonthCalendarItem(
                        month = monthName,
                        year = yearVal,
                        totalDays = daysInMonth,
                        startOffset = startOffset,
                        timeOffList = timeOffs,
                        onDateClick = { day ->
                            selectedDayToToggle = Triple(day, monthName, yearVal)
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showConfirmDialog && selectedDayToToggle != null) {
        val (day, monthStr, yearVal) = selectedDayToToggle!!
        val dateString = "$day $monthStr $yearVal"
        val existingOff = allTimeOff["$monthStr $yearVal"]?.find { it.date == dateString }
        val isOff = existingOff != null

        // Fix logic: we want checkstate to match isOff initially
        var tempIsUnavailable by remember(selectedDayToToggle) { mutableStateOf(isOff) }

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Manage Availability") },
            text = {
                Column {
                    Text("Date: $dateString", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempIsUnavailable,
                            onCheckedChange = { tempIsUnavailable = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Unavailable")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempIsUnavailable != isOff) {
                            onToggleAvailability(day, monthStr, yearVal, isOff)
                        }
                        showConfirmDialog = false
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DoctorDashboardPreview() {
    val calendars = remember {
        val list = mutableListOf<java.util.Calendar>()
        val current = java.util.Calendar.getInstance()
        for (i in 0 until 3) {
            val cal = current.clone() as java.util.Calendar
            cal.add(java.util.Calendar.MONTH, i)
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1) 
            list.add(cal)
        }
        list
    }

    DoctorDashboardContent(
        doctor = Doctor("1", "Dr. Test", "General"),
        calendars = calendars,
        allTimeOff = emptyMap(),
        onBack = {},
        onViewDoctorSchedule = {},
        onViewStaffSchedule = {},
        onViewAnalytics = {},
        onToggleAvailability = { _, _, _, _ -> }
    )
}

fun getMonthName(monthIndex: Int): String {
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    return months.getOrElse(monthIndex) { "" }
}

@Composable
fun MonthCalendarItem(
    month: String,
    year: Int,
    totalDays: Int,
    startOffset: Int,
    timeOffList: List<TimeOff>,
    onDateClick: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$month $year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Days Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                    FixedWidthText(it, width = 36.dp, textAlign = TextAlign.Center, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            val calendarGridItems = buildList {
                repeat(startOffset) { add(null) }
                (1..totalDays).forEach { add(it) }
            }
            val rows = calendarGridItems.chunked(7)

            rows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 7) {
                        if (i < week.size) {
                            val day = week[i]
                            if (day == null) {
                                Spacer(modifier = Modifier.width(36.dp))
                            } else {
                                val dateString = "$day $month $year"
                                val isOff = timeOffList.any { it.date == dateString }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = if (isOff) Color(0xFFFFEBEE) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isOff) Color.Red else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { onDateClick(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isOff) Color.Red else Color.Black,
                                        fontWeight = if (isOff) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }
                }
            }
        }
    }
}

// Renamed to avoid overload ambiguity with androidx.compose.material3.Text
@Composable
fun FixedWidthText(text: String, width: androidx.compose.ui.unit.Dp, textAlign: TextAlign, color: Color, fontSize: androidx.compose.ui.unit.TextUnit) {
    androidx.compose.material3.Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = textAlign,
        color = color,
        fontSize = fontSize
    )
}
