package com.example.khsapp.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.model.Services


@Composable
fun ServiceSelectionScreen(
    onServiceSelected: (Services) -> Unit,
    onBack: () -> Unit
) {
    val repository = remember { com.example.khsapp.repository.MedicalRepository() }
    var services by remember { mutableStateOf<List<Services>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        val result = repository.getServices()
        result.onSuccess {
            // Sort by serviceId as requested/implied
            services = it.sortedBy { s -> s.serviceId }
            isLoading = false
        }
        result.onFailure { e ->
            isLoading = false
            android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    ServiceSelectionContent(
        services = services,
        isLoading = isLoading,
        onServiceSelected = onServiceSelected,
        onBack = onBack
    )
}

@Composable
fun ServiceSelectionContent(
    services: List<Services>,
    isLoading: Boolean,
    onServiceSelected: (Services) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        // --- Navigation Update: Step 1 of 4 ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Step 1 of 4",
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
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
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
            text = "Choose a Service",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003D33),
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Select the type of care you need",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF009688))
            }
        } else {
             if (services.isEmpty()) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text(
                         text = "No services found.\nPlease check your internet connection.",
                         color = Color.Gray,
                         textAlign = androidx.compose.ui.text.style.TextAlign.Center
                     )
                 }
             } else {
                // --- Service List ---
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(services.size) { index ->
                        val service = services[index]
                        ServiceCard(service = service, onClick = { onServiceSelected(service) })
                    }
                }
             }
        }
    }
}

@Composable
fun ServiceCard(service: Services, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        // Replaced MedicalServices with Favorite as fallback since MedicalServices is not in Core
                        imageVector = if(service.specialization.contains("General")) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFF009688),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = service.specialization,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF003D33),
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = service.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RM ${service.price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF009688),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(
                            imageVector = Icons.Default.Info, // Replaced AccessTime with Info
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = service.duration,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ServiceSelectionPreview() {
    val mockServices = listOf(
        Services(
            serviceId = "S01",
            specialization = "General Consultation",
            price = 50.0,
            description = "Basic health checkup and consultation",
            duration = "30 min"
        ),
        Services(
            serviceId = "S02",
            specialization = "Dental Care",
            price = 150.0,
            description = "Teeth cleaning and oral hygiene check",
            duration = "45 min"
        )
    )

    ServiceSelectionContent(
        services = mockServices,
        isLoading = false,
        onServiceSelected = {},
        onBack = {}
    )
}