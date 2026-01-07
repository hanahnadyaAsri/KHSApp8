package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.tooling.preview.Preview
import com.example.khsapp.ui.theme.KHSAppTheme

@Composable
fun ConfirmCancelScreen(
    serviceName: String = "Consultation",
    date: String = "Date",
    time: String = "Time",
    refundAmount: String = "0.00", // Not actively used but good for API
    onConfirmCancel: () -> Unit,
    onKeepAppointment: () -> Unit
) {
    // Since it's a full screen composable in NavHost, we simulate the Dialog look
    // But usually this would be content INSIDE a Dialog if using "dialog" destination.
    // However, user asked for "ui like this" in a screen context, so we'll center a card.
    

    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)), // Dimmed background
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Close Icon + Title)
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onKeepAppointment,
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = Color.Gray)
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cancel Appointment",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Are you sure you want to cancel this appointment?",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Appointment Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)) // Light Teal/Blue
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = serviceName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF004D40)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$date at $time",
                            fontSize = 14.sp,
                            color = Color(0xFF00695C)
                        )
                    }
                }
                

                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Buttons
                Button(
                    onClick = onConfirmCancel,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Yes, Cancel", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onKeepAppointment,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF004D40)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB0BEC5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Keep Appointment", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmCancelScreenPreview() {
    KHSAppTheme {
        ConfirmCancelScreen(
            serviceName = "General Checkup",
            date = "Oct 24, 2023",
            time = "10:00 AM",
            onConfirmCancel = {},
            onKeepAppointment = {}
        )
    }
}
