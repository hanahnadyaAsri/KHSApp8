package com.example.khsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Appointment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelAppointmentScreen(
    appointmentId: String?,
    viewModel: AppointmentsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit,
    onConfirmCancelClick: () -> Unit
) {
    // Find the appointment from the list (shared ViewModel needed or pass data)
    // For simplicity, we assume we just trigger delete on ID and navigate

    val appointment = viewModel.appointments.collectAsState().value.find { it.id == appointmentId }
        ?: Appointment(doctorName = "Unknown", serviceName = "", date = "", time = "")

    val context = androidx.compose.ui.platform.LocalContext.current

    CancelAppointmentContent(
        appointment = appointment,
        onBackClick = onBackClick,
        onConfirmCancel = { reason ->
            if (appointmentId != null) {
                viewModel.cancelAppointment(appointmentId, reason) { result ->
                    if (result.isSuccess) {
                        onConfirmCancelClick()
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        android.widget.Toast.makeText(context, "Cancellation failed: $error", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelAppointmentContent(
    appointment: Appointment,
    onBackClick: () -> Unit,
    onConfirmCancel: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Simple back icon
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Doctor Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = appointment.doctorName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "${appointment.date}", color = Color.Gray)
            Text(text = "Time: ${appointment.time}", color = Color.Gray)
            Text(text = appointment.serviceName, color = Color.Gray) // Doctor Name field in mock

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Reason for cancellation:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                placeholder = { Text("Write your reason") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Transparent,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (reason.isBlank()) {
                        // show error or ignore
                        return@Button
                    }
                    onConfirmCancel(reason)
                },
                enabled = reason.isNotBlank(), // Disable if empty
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), // Blue color
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon placeholder
                    Text("🚫", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Appointment", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)), // Dark Button
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Back", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CancelAppointmentPreview() {
    CancelAppointmentContent(
        appointment = Appointment(
            doctorName = "Dr. Farhan",
            serviceName = "General Consultation",
            date = "12/12/2025",
            time = "10:00am"
        ),
        onBackClick = {},
        onConfirmCancel = {}
    )
}
