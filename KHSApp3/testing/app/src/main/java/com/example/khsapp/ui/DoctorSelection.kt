package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Doctor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorSelection(
    serviceId: String,
    onDoctorSelected: (Doctor) -> Unit,
    onBack: () -> Unit
) {
    val repository = remember { com.example.khsapp.repository.MedicalRepository() }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(serviceId) {
        if (serviceId.isNotEmpty()) {
            val result = repository.getDoctorsByServiceId(serviceId)
            result.onSuccess {
                Doctors = it
                isLoading = false
            }
            result.onFailure { e ->
                isLoading = false
                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        } else {
             isLoading = false
        }
    }

    DoctorSelectionContent(
        doctors = doctors,
        isLoading = isLoading,
        onDoctorSelected = onDoctorSelected,
        onBack = onBack
    )
}

@Composable
fun DoctorSelectionContent(
    Doctors: List<Doctor>,
    isLoading: Boolean,
    onDoctorSelected: (Doctor) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        // --- Navigation Update: Step 2 of 4 ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Step 2 of 4",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color(0xFF009688), RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color(0xFF009688), RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Header ---
        Text(
            text = "Choose Your Doctor",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003D33),
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Select a doctor for your General Consultation",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF009688))
            }
        } else if (doctors.isEmpty()) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                     text = "No doctors available for this service.",
                     color = Color.Gray,
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(doctors.size) { index ->
                    val doctor = doctors[index]
                    DoctorCard(doctor = doctor, onClick = { onDoctorSelected(doctor) })
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if(doctor.drGender == "Female") Color(0xFFE0F7FA) else Color(0xFFE3F2FD)
                        ),
                    contentAlignment = Alignment.Center
                ){
                    // Placeholder icon
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint =  if(doctor.drGender == "Female") Color(0xFF006064) else Color(0xFF0D47A1),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.doctorName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF003D33),
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = doctor.drSpecialization,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF009688) // Teal
                    )
                    // Description removed as per new schema requirements
                }
                
                // Right Arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                 Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFF9800), // Orange Star
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${doctor.rating}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "${doctor.yearOfExperience} years experience",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DoctorSelectionPreview() {
    val mockDoctors = listOf(
        Doctor(
            doctorId = "1",
            doctorName = "Dr. Ali",
            drSpecialization = "Cardiologist",
            yearOfExperience = 10,
            drGender = "Male",
            serviceIds = listOf("S1")
        ),
        Doctor(
            doctorId = "2",
            doctorName = "Dr. Siti",
            drSpecialization = "Pediatrician",
            yearOfExperience = 8,
            drGender = "Female",
            serviceIds = listOf("S1")
        )
    )

    DoctorSelectionContent(
        doctors = mockDoctors,
        isLoading = false,
        onDoctorSelected = {},
        onBack = {}
    )
}
