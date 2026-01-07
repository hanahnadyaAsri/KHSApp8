package com.example.khsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.R
import com.example.khsapp.ui.components.PieChart
import com.example.khsapp.ui.components.BarChart
import androidx.compose.ui.tooling.preview.Preview
import com.example.khsapp.ui.theme.KHSAppTheme


@Composable
fun PatientDashboard(
    onNavigateToBook: (String) -> Unit,
    onNavigateToViewAppointments: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFeedback: () -> Unit
) {
    val scrollState = rememberScrollState()
    val tealColor = Color(0xFF009688)
    
    // Analytics State
    val viewModel: AnalyticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // User State
    var userName by remember { mutableStateOf("") } // Start empty
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("Users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("fullName") ?: "Patient"
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(tealColor)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "07:00", // Clock placeholder 
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Welcome",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        userName, 
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                
                // Profile Image
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp)
                        .clickable { onNavigateToProfile() },
                    color = Color.LightGray
                ) {
                   // Placeholder for profile image
                   Image(
                       painter = painterResource(id = R.drawable.ic_launcher_foreground), // Fallback
                       contentDescription = "Profile",
                       contentScale = ContentScale.Crop
                   )
                }
            }
        }

        // Branding Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFB2DFDB),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Stethoscope Placeholder
                        contentDescription = "Logo",
                        tint = tealColor,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "KHS BANGI CLINIC",
                color = tealColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                "Your Health , Our Priority",
                color = tealColor,
                fontSize = 14.sp
            )
        }
        
        // Info Cards
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                icon = R.drawable.ic_launcher_foreground,
                text = "Quality Care",
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = R.drawable.ic_launcher_foreground, 
                text = "Expert Doctors",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action Buttons
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                text = "Book\nAppointment",
                icon = Icons.Default.DateRange, // Calendar Icon
                onClick = { onNavigateToBook(if(userName.isNotEmpty()) userName else "Patient") },
                backgroundColor = Color(0xFFB2DFDB) // Light Teal
            )
            
            ActionButton(
                text = "View\nAppointment",
                icon = androidx.compose.material.icons.Icons.Default.DateRange, 
                onClick = onNavigateToViewAppointments,
                backgroundColor = Color(0xFFB2DFDB)
            )

            // Feedback Button
             ActionButton(
                text = "Feedback\n& Rating",
                icon = androidx.compose.material.icons.Icons.Default.DateRange, // Or appropriate icon
                onClick = onNavigateToFeedback,
                backgroundColor = Color(0xFFB2DFDB)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Analytics Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Our Clinic at Glance",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "Trusted by thousands of patients",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Monthly Visit
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monthly Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    // Use live data 
                    val chartData = if (uiState.appointmentsMonthly.isNotEmpty()) uiState.appointmentsMonthly else mapOf("No Data" to 0)
                    BarChart(
                        data = chartData, 
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Age Groups
                 Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(220.dp) // Height increased for layout
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Age Groups", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(8.dp))
                        val ageData = if (uiState.ageDistribution.isNotEmpty()) uiState.ageDistribution else mapOf("N/A" to 1f)
                        PieChart(
                            data = ageData,
                            colors = listOf(
                                Color(0xFF009688), // Teal
                                Color(0xFFFF7043), // Deep Orange
                                Color(0xFF5C6BC0)  // Indigo
                            ),
                            modifier = Modifier.size(120.dp)
                        )
                        // Legend (Simple)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ageData.keys.forEachIndexed { index, label ->
                                Text(
                                    label, 
                                    fontSize = 10.sp, 
                                    color = listOf(Color(0xFF009688), Color(0xFFFF7043), Color(0xFF5C6BC0)).getOrElse(index) {Color.Gray}
                                )
                            }
                        }
                    }
                }
                
                // Top Doctors
                 Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(220.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Top Doctors", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp), 
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.topDoctors.isNotEmpty()) {
                                var rank = 1
                                uiState.topDoctors.forEach { (name, progress) ->
                                    val rankColor = when(rank) {
                                        1 -> Color(0xFFFFD700) // Gold
                                        2 -> Color(0xFFC0C0C0) // Silver
                                        3 -> Color(0xFFCD7F32) // Bronze
                                        else -> tealColor
                                    }
                                    DoctorBar(name, progress, rankColor)
                                    rank++
                                }
                            } else {
                                Text("No data available", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Footer (SAME)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(tealColor)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray) 
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Visit Us : KHC BANGI CLINIC", color = Color.Black, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Us : +6016-4081283", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun InfoCard(icon: Int, text: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(id = icon), null, tint = Color(0xFF009688), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF009688),
                modifier = Modifier.size(30.dp)
            )
            
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Arrow placeholder
            Icon(
                 painter = painterResource(id = R.drawable.ic_launcher_foreground), // Specific Arrow resource needed or vector
                 contentDescription = null,
                 tint = Color(0xFF009688),
                 modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DoctorBar(name: String, progress: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, fontSize = 10.sp, modifier = Modifier.width(50.dp))
        Box(
            modifier = Modifier
                .height(8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDashboardPreview() {
    KHSAppTheme {
        PatientDashboard(
            onNavigateToBook = {},
            onNavigateToViewAppointments = {},
            onNavigateToProfile = {},
            onNavigateToFeedback = {}
        )
    }
}
