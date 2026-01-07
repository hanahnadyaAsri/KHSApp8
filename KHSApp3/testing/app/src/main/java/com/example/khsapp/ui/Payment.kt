package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Appointment
import com.example.khsapp.model.PaymentMethod
import com.example.khsapp.model.Services
import com.example.khsapp.model.Doctor
import androidx.compose.ui.tooling.preview.Preview
import com.example.khsapp.ui.theme.KHSAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Payment(
    bookingState: Appointment,
    onPaymentSelected: (PaymentMethod, () -> Unit) -> Unit,
    onBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CREDIT_CARD) }
    var isProcessing by remember { mutableStateOf(false) }

    // Dynamic Price Calculation
    val serviceFee = bookingState.price // Use price from Appointment which is populated in ViewModel
    val deposit = serviceFee * 0.5 // 50% Deposit
    val totalDueNow = deposit

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Custom Header logic to match design
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Confirm Booking",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003D33),
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "Review your appointment details and complete payment",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appointment Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Appointment Summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF003D33),
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        SummaryItem(
                            icon = Icons.Default.Favorite,
                            label = "Service",
                            value = bookingState.serviceName
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        SummaryItem(
                            icon = Icons.Default.Person,
                            label = "Doctor",
                            value = bookingState.doctorName
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        SummaryItem(
                            icon = Icons.Default.DateRange,
                            label = "Date & Time",
                            value = "${bookingState.date} at ${bookingState.time}"
                        )
                    }
                }
            }

            // Payment Details Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Payment Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF003D33),
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Service Fee", color = Color.Gray, fontSize = 16.sp)
                            Text("RM ${serviceFee.toInt()}", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFF003D33))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Booking Deposit", color = Color.Gray, fontSize = 16.sp)
                            Text("RM ${deposit.toInt()}", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFF003D33))
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFE0E0E0))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Due Now", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003D33))
                            Text("RM ${totalDueNow.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF009688))
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Can be cancel anytime with a solid reason",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Payment Method (Demo) Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Payment Method (Demo)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF003D33),
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Credit Card
                            PaymentMethodButton(
                                label = "Credit Card",
                                isSelected = selectedMethod == PaymentMethod.CREDIT_CARD,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = PaymentMethod.CREDIT_CARD }
                            )
                            // FPX (Online Banking)
                            PaymentMethodButton(
                                label = "FPX",
                                isSelected = selectedMethod == PaymentMethod.ONLINE_BANKING,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = PaymentMethod.ONLINE_BANKING }
                            )
                            // E-Wallet
                            PaymentMethodButton(
                                label = "E-Wallet",
                                isSelected = selectedMethod == PaymentMethod.E_WALLET,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = PaymentMethod.E_WALLET }
                            )
                        }
                    }
                }
            }

            // Pay Button
            item {
                Button(
                    onClick = {
                        isProcessing = true
                        onPaymentSelected(selectedMethod) {
                            isProcessing = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)), // Teal
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay RM ${totalDueNow.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun SummaryItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF009688),
            modifier = Modifier.size(24.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFF003D33))
        }
    }
}

@Composable
fun PaymentMethodButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF009688) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFFAFAFA),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF009688) else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    KHSAppTheme {
        Payment(
            bookingState = Appointment(
                serviceName = "Teeth Whitening",
                doctorName = "Dr. Ali",
                date = "24/10/2023",
                time = "10:30 AM",
                price = 200.0
            ),
            onPaymentSelected = { _, _ -> },
            onBack = {}
        )
    }
}