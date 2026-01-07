package com.example.khsapp.ui

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ViewAppointmentScreen(
    viewModel: AppointmentsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onCancelClick: (String) -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    ViewAppointmentContent(
        appointments = appointments,
        onCancelClick = onCancelClick
    )
}

@Composable
fun ViewAppointmentContent(
    appointments: List<Appointment>,
    onCancelClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Past", "Cancelled")

    val upcomingAppointments = appointments.filter { !it.isCancelled && it.status == "Upcoming" }
    val pastAppointments = appointments.filter { !it.isCancelled && it.status == "Completed" }
    val canceledAppointments = appointments.filter { it.isCancelled || it.status == "Cancelled" }

    val currentList = when (selectedTab) {
        0 -> upcomingAppointments
        1 -> pastAppointments
        else -> canceledAppointments
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        // --- Header ---
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "My Appointments",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003D33),
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "View and manage your appointments",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // --- Custom Tabs ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0F2F1))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                // Using Standard Core Icons as fallback
                val icon = when (index) {
                    0 -> Icons.Filled.DateRange // For Upcoming
                    1 -> Icons.Filled.Check // For Past/Completed
                    2 -> Icons.Filled.Close // For Cancelled
                    else -> Icons.Filled.DateRange
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { selectedTab = index },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF003D33) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            color = if (isSelected) Color(0xFF003D33) else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Appointment List ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(currentList) { appointment ->
                AppointmentCard(appointment, onCancelClick)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ViewAppointmentPreview() {
    ViewAppointmentContent(
        appointments = listOf(
            Appointment(id = "1", doctorName = "Dr. Farhan", serviceName = "General Consultation", date = "12/12/2025", time = "10:00am", status = "Upcoming"),
            Appointment(id = "2", doctorName = "Dr. Sarah", serviceName = "Dental Care", date = "10/12/2025", time = "2:00pm", status = "Completed"),
            Appointment(id = "3", doctorName = "Dr. Wong", serviceName = "Skin Check", date = "01/12/2025", time = "9:00am", status = "Upcoming", isCancelled = true)
        ),
        onCancelClick = {}
    )
}

@Composable
fun AppointmentCard(appointment: Appointment, onCancelClick: (String) -> Unit) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row: Icon + Details
            // Corrected Row parameters: removed crossAxisAlignment
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange, // Core icon replacement for CalendarToday
                        contentDescription = null,
                        tint = Color(0xFF009688),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.serviceName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF003D33)
                    )
                    Text(
                        text = appointment.doctorName,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Add to Calendar Button (Only for Upcoming)
                if (!appointment.isCancelled && appointment.status == "Upcoming") {
                    IconButton(
                        onClick = { addToCalendar(context, appointment) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange, // Using DateRange as "Add to Calendar" fallback
                            contentDescription = "Add to Calendar",
                            tint = Color(0xFF009688)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and Time Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.date,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    imageVector = Icons.Filled.Info, // Core icon replacement for AccessTime
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.time,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer: Action Button
            if (!appointment.isCancelled && appointment.status == "Upcoming") {
                OutlinedButton(
                    onClick = { onCancelClick(appointment.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close, // Close is core
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel")
                }
            } else if (appointment.isCancelled || appointment.status == "Cancelled") {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = Color(0xFFE57373),
                        disabledContainerColor = Color(0xFFFFEBEE).copy(alpha = 0.3f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelled")
                }
            } else {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = Color(0xFF00695C),
                        disabledContainerColor = Color(0xFFE0F2F1).copy(alpha = 0.3f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check, // Check is core
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completed")
                }
            }
        }
    }
}

private fun addToCalendar(context: Context, appointment: Appointment) {
    try {
        val dateString = "${appointment.date} ${appointment.time}"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = sdf.parse(dateString)

        if (date != null) {
            val startMillis = date.time
            val endMillis = startMillis + (60 * 60 * 1000)

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, "${appointment.serviceName} with ${appointment.doctorName}")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Appointment at Tenang Clinic")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Tenang Clinic")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        if (e is android.content.ActivityNotFoundException) {
            Toast.makeText(context, "No Calendar app installed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error adding to calendar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        e.printStackTrace()
    }
}
