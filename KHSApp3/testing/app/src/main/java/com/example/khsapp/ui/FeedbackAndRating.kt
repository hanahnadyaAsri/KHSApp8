package com.example.khsapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Data Model
data class Appointment(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val date: String,
    var feedbackSubmitted: Boolean = false,
    var rating: Int = 0,
    var comment: String = ""
)

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentHistoryList(onNavigateBack: () -> Unit = {}, onNavigateToProfile: () -> Unit = {}) {
    // Mock Data
    val appointments = remember {
        mutableStateListOf(
            Appointment(
                "1",
                "Dr. Aiman",
                "Cardiologist",
                "Dec 12, 2024",
                feedbackSubmitted = false
            ),
            Appointment(
                "2",
                "Dr. Sarah",
                "Dermatologist",
                "Dec 10, 2024",
                feedbackSubmitted = true,
                rating = 5,
                comment = "Great service!"
            ),
            Appointment(
                "3",
                "Dr. Wong",
                "General Practitioner",
                "Nov 28, 2024",
                feedbackSubmitted = false
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Appointment History",
                        style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onNavigateToProfile) {
                        Text(
                            "Profile",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF009688)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFFAFAFA), // Neutral background
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(
                    appointment = appointment,
                    onRateClick = {
                        selectedAppointment = appointment
                        showDialog = true
                    },
                    onViewFeedbackClick = {
                        // Simple feedback view action (e.g. snackbar or
                        // just info)
                    }
                )
            }
        }

        if (showDialog && selectedAppointment != null) {
            FeedbackDialog(
                onDismiss = { showDialog = false },
                onSubmit = { rating, comment ->
                    // Update the appointment in the list
                    val index = appointments.indexOf(selectedAppointment)
                    if (index != -1) {
                        appointments[index] =
                            appointments[index].copy(
                                feedbackSubmitted = true,
                                rating = rating,
                                comment = comment
                            )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onRateClick: () -> Unit,
    onViewFeedbackClick: () -> Unit
) {
    Card(
        modifier =
        Modifier.fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Doctor Icon / Initial
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0F2F1) // Light Teal
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = appointment.doctorName.take(1),
                            style =
                            MaterialTheme.typography.titleLarge
                                .copy(
                                    color =
                                    Color(
                                        0xFF009688
                                    ),
                                    fontWeight =
                                    FontWeight
                                        .Bold
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = appointment.doctorName,
                        style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = appointment.specialty,
                        style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.date,
                    style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Status Chip
                Surface(
                    color = Color(0xFFE0F2F1), // Light Teal for status too? Or match status color? Keeping Green for Completed
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Completed",
                            style =
                            MaterialTheme.typography.labelSmall
                                .copy(
                                    color =
                                    Color(
                                        0xFF00796B
                                    ), // Darker Teal/Green
                                    fontWeight =
                                    FontWeight
                                        .Bold
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!appointment.feedbackSubmitted) {
                Button(
                    onClick = onRateClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688)
                    ), // Teal
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Rate Appointment", fontWeight = FontWeight.SemiBold) }
            } else {
                OutlinedButton(
                    onClick = onViewFeedbackClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF009688)
                    ),
                    border =
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFF009688)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("View Feedback", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
fun FeedbackDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Share your Experience",
                    style =
                    MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "How was your appointment?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive Rating Bar
                RatingBar(rating = rating, onRatingChanged = { rating = it })

                Spacer(modifier = Modifier.height(24.dp))

                // Comment Input
                OutlinedTextField(
                    value = comment,
                    onValueChange = { if (it.length <= 200) comment = it },
                    label = { Text("Tell us more about your visit") },
                    placeholder = { Text("Write your review here...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF009688),
                        focusedLabelColor = Color(0xFF009688),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 5,
                    supportingText = {
                        Text(
                            text = "${comment.length}/200",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign =
                            androidx.compose.ui.text.style
                                .TextAlign.End
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = Color.Gray)
                    }

                    Button(
                        onClick = { onSubmit(rating, comment) },
                        enabled = rating > 0,
                        colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF009688),
                            disabledContainerColor =
                            Color(0xFF80CBC4) // Disabled Light Teal
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Submit Feedback") }
                }
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector =
                if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star $i",
                modifier =
                Modifier.size(40.dp)
                    .clickable(
                        interactionSource =
                        remember {
                            MutableInteractionSource()
                        },
                        indication =
                        null // Remove ripple for cleaner
                        // look
                    ) { onRatingChanged(i) }
                    .padding(4.dp),
                tint =
                if (i <= rating) Color(0xFFFFC107)
                else Color.LightGray // Amber for selected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentHistoryListPreview() {
    AppointmentHistoryList()
}
