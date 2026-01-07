package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Doctor
import com.example.khsapp.model.TimeOff
import com.example.khsapp.repository.BookingRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelection(
    currentDoctor: Doctor?,
    onDateTimeSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    // Repository
    val bookingRepository = remember { BookingRepository() }
    var timeOffList by remember { mutableStateOf<List<TimeOff>>(emptyList()) }
    
    // Calendar helper for fetching
    // ... Logic to calculate currentMonthOffset needed for fetch is inside the UI currently ...
    // ... This refactor is slightly tricky because the fetch logic depends on state `currentMonthOffset` which is UI state ...
    
    // We will hoist `currentMonthOffset` to Content or keep it in Content and expose a callback for fetch?
    // Easier: Let Content control month offset and request "time off for month X".
    // Or pass the repository? No, preview needs to avoid repository.
    
    // Better: Hoist the month navigation state out? Or just pass a callback "onRequestTimeOff(month, year)"
    
    // Simplified Refactor:
    // We'll keep the repository in TimeSelection, but the `LaunchedEffect` needs to know about month/year.
    // So we need to hoist the month/year state up to TimeSelection.

    var currentMonthOffset by remember { mutableStateOf(0) }
    
     // Helper to get month name
    fun getMonthName(monthIndex: Int): String {
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.getOrElse(monthIndex) { "" }
    }

    val displayCalendar = remember(currentMonthOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, currentMonthOffset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal
    }
    
    val currentMonthName = getMonthName(displayCalendar.get(Calendar.MONTH))
    val currentYear = displayCalendar.get(Calendar.YEAR)

    // Fetch Availability
    LaunchedEffect(currentDoctor?.doctorId, currentMonthOffset) {
        if (currentDoctor != null) {
            timeOffList = bookingRepository.getTimeOff(currentDoctor.doctorId, currentMonthName, currentYear)
        }
    }
    
    TimeSelectionContent(
        currentDoctor = currentDoctor,
        timeOffList = timeOffList,
        onDateTimeSelected = onDateTimeSelected,
        onBack = onBack,
        currentMonthOffset = currentMonthOffset,
        onMonthOffsetChange = { newOffset -> currentMonthOffset = newOffset }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionContent(
    currentDoctor: Doctor?,
    timeOffList: List<TimeOff>,
    onDateTimeSelected: (String, String) -> Unit,
    onBack: () -> Unit,
    currentMonthOffset: Int,
    onMonthOffsetChange: (Int) -> Unit
) {
    // State
    var selectedFullDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showTimeSheet by remember { mutableStateOf(false) }
    
    val timeSlots = listOf(
        "9:00am", "9:30am", "10:00am",
        "10:30am", "11:00am", "11:30am",
        "12:00pm", "12:30pm", "1:00pm",
        "1:30pm", "2:00pm", "2:30pm",
        "3:00pm", "3:30pm", "4:00pm"
    )

    // Helper to get month name
    fun getMonthName(monthIndex: Int): String {
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.getOrElse(monthIndex) { "" }
    }

    // Current Displayed Calendar
    val displayCalendar = remember(currentMonthOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, currentMonthOffset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal
    }
    
    val currentMonthName = getMonthName(displayCalendar.get(Calendar.MONTH))
    val currentYear = displayCalendar.get(Calendar.YEAR)

    Scaffold(
        topBar = {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Nav Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { onBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back", color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Step 3 of 4", color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (index <= 2) Color(0xFF009688) else Color(0xFFE0E0E0),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Pick Date & Time",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003D33),
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Choose an available slot with ${currentDoctor?.doctorName ?: "Doctor"}",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Calendar Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month Navigation using Icons as requested
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onMonthOffsetChange(currentMonthOffset - 1) }) {
                           Icon(Icons.Default.ArrowBack, null)
                        }
                        
                        Text(
                            text = "$currentMonthName $currentYear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF003D33)
                        )

                        IconButton(onClick = { onMonthOffsetChange(currentMonthOffset + 1) }) {
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Days Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                            Text(
                                text = it,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days Grid
                    val daysInMonth = displayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val startOffset = displayCalendar.get(Calendar.DAY_OF_WEEK) - 1
                    
                    val calendarGridItems = buildList {
                        repeat(startOffset) { add(null) }
                        (1..daysInMonth).forEach { add(it) }
                    }
                    val rows = calendarGridItems.chunked(7)

                    rows.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 0 until 7) {
                                if (i < week.size) {
                                    val day = week[i]
                                    if (day == null) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    } else {
                                        val dateString = "$day $currentMonthName $currentYear"
                                        val isTimeOff = timeOffList.any { it.date == dateString }
                                        
                                        val now = Calendar.getInstance()
                                        val checkCal = Calendar.getInstance()
                                        checkCal.set(currentYear, displayCalendar.get(Calendar.MONTH), day)
                                        
                                        val isToday = now.get(Calendar.YEAR) == currentYear && 
                                                    now.get(Calendar.MONTH) == displayCalendar.get(Calendar.MONTH) && 
                                                    now.get(Calendar.DAY_OF_MONTH) == day
                                        val isPast = checkCal.before(now) && !isToday

                                        val isBlocked = isPast || isTimeOff
                                        val isSelected = selectedFullDate == dateString

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) Color(0xFFFFAB91) // Peach color
                                                    else Color.Transparent 
                                                )
                                                .clickable(enabled = !isBlocked) {
                                                    selectedFullDate = dateString
                                                    showTimeSheet = true
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = day.toString(),
                                                color = when {
                                                    isSelected -> Color(0xFFD84315) // Darker peach text
                                                    isTimeOff -> Color.Red
                                                    isBlocked -> Color.LightGray
                                                    else -> Color.Black
                                                },
                                                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Time Slot Bottom Sheet
    if (showTimeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimeSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Available Slots",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003D33)
                )
                Text(
                    text = "for $selectedFullDate",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(250.dp) // Fixed height for grid
                ) {
                    items(timeSlots) { time ->
                        val isSelected = selectedTime == time
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF009688) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (isSelected) Color(0xFFE0F2F1) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTime = time },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = time,
                                color = if (isSelected) Color(0xFF009688) else Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedTime != null && selectedFullDate != null) {
                            showTimeSheet = false
                            onDateTimeSelected(selectedFullDate!!, selectedTime!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedTime != null
                ) {
                    Text("Confirm Booking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun TimeSelectionPreview() {
    val doctor = Doctor(doctorId = "1", doctorName = "Dr. Test", drSpecialization = "General")
    TimeSelectionContent(
        currentDoctor = doctor,
        timeOffList = emptyList(),
        onDateTimeSelected = { _, _ -> },
        onBack = {},
        currentMonthOffset = 0,
        onMonthOffsetChange = {}
    )
}
