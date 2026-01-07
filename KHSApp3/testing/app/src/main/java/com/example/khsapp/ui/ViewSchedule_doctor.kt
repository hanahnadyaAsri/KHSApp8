package com.example.khsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.ui.theme.KHSAppTheme

data class Patient(
    val name: String,
    val time: String,
    val date: String,
    val reason: String,
    val imageRes: Int? = null // Placeholder
)

class ViewScheduleDoctorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KHSAppTheme {
                ViewScheduleDoctorScreen()
            }
        }
    }
}

@Composable
fun ViewScheduleDoctorScreen() {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val patients = listOf(
        Patient("Mustaqim bin Mahmod", "2:00pm - 3:00pm", "2nd, Friday", "Vaccination"),
        Patient("Adam bin Ikmal", "1:00pm - 3:00pm", "5th, Wednesday", "Ear Suction"),
        Patient("Danish bin Hariz", "2:00pm - 3:00pm", "20th, Thursday", "Wound Care"),
        Patient("Syakirah binti Salman", "9:00am - 10:00pm", "28th, Friday", "Consultation")
    )

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Schedule",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            SearchBar(searchQuery) { searchQuery = it }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(patients) { patient ->
                    PatientCard(patient)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun SearchBar(query: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text("Type here ...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEEEEEE),
            unfocusedContainerColor = Color(0xFFEEEEEE),
            disabledContainerColor = Color(0xFFEEEEEE),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun PatientCard(patient: Patient) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar Placeholder
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = patient.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange, // Using clock icon would be better but reusing DateRange as generic time/date icon
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${patient.time} (${patient.date})",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason/Type Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite, // Heart icon for medical reason
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = patient.reason,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewScheduleDoctorScreenPreview() {
    KHSAppTheme {
        ViewScheduleDoctorScreen()
    }
}
