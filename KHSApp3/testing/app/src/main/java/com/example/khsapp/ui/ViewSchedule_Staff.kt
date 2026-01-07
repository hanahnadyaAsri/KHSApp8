package com.example.khsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.ui.theme.KHSAppTheme

data class Doctor(
    val name: String,
    val specialization: String,
    val time: String? = null,
    val isAvailable: Boolean = true,
    @DrawableRes val imageRes: Int? = null // Placeholder for now
)

class ViewScheduleStaffActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KHSAppTheme {
                ViewScheduleScreen()
            }
        }
    }
}

@Composable
fun ViewScheduleScreen() {
    val availableDoctors = listOf(
        Doctor("Dr Anas", "Dermatologist", "2:00pm - 3:00pm", true),
        Doctor("Dr Siti", "Cardiologist", "1:00pm - 8:00pm", true)
    )

    val unavailableDoctors = listOf(
        Doctor("Dr Adam", "Cardiologist", isAvailable = false)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection()
            }

            item {
                Text(
                    text = "Doctor Available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(availableDoctors) { doctor ->
                DoctorCard(doctor = doctor)
            }

            item {
                Text(
                    text = "Doctor Not Available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(unavailableDoctors) { doctor ->
                DoctorCard(doctor = doctor)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome, Samad",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            // Empty space or additional info if needed
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Placeholder for profile image
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Image
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier.size(50.dp)
                    ) {
                        // Use actual image if available, else icon
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = doctor.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // Status Indicator (Blue dot if available)
                if (doctor.isAvailable) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF03A9F4)) // Light Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (doctor.isAvailable) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange, // Clock icon would be better, using DateRange for now or standard time
                        contentDescription = null, // decorative
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doctor.time ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = doctor.specialization,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 20.dp) // Indent to align with time text roughly
                )
            } else {
                Text(
                    text = doctor.specialization,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle Message Click */ },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(150.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (doctor.isAvailable) Color(0xFF03A9F4) else Color(0xFF757575)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Message", color = Color.White)
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { /* Navigate Home */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF03A9F4),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Schedule") },
            selected = false,
            onClick = { /* Navigate Schedule */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            selected = false,
            onClick = { /* Navigate Profile */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ViewScheduleScreenPreview() {
    KHSAppTheme {
        ViewScheduleScreen()
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun ViewScheduleScreenScrollPreview() {
    KHSAppTheme {
        // Create a fake list of many doctors to test scrolling
        val manyDoctors = List(10) {
            Doctor("Dr Number $it", "Specialist", "10:00am - 5:00pm", true)
        }

        Scaffold(
            bottomBar = { BottomNavigationBar() },
            containerColor = Color.White
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HeaderSection() }
                item {
                    Text(
                        text = "Doctor Available",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(manyDoctors) { doctor ->
                    DoctorCard(doctor = doctor)
                }
                item {
                    Text(
                        text = "Doctor Not Available",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(manyDoctors.take(3)) { doctor ->
                    DoctorCard(doctor = doctor.copy(isAvailable = false))
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
