package com.example.khsapp.ui

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBackClick: () -> Unit,
    viewModel: AnalyticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedDetailTitle by remember { mutableStateOf("") }
    
    fun onShowDetail(title: String) {
        selectedDetailTitle = title
        showDetailDialog = true
    }

    if (showDetailDialog) {
        DetailStatsDialog(
            title = selectedDetailTitle,
            onDismiss = { showDetailDialog = false },
            // Pass real data if needed, or keep detail dialog simple/generic for now as per scope
            // For this iteration, we keep the mock detail lists or can expand later.
            // The main charts are the priority.
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SectionTitle("Overview")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Total Patients", 
                        value = uiState.totalPatients.toString(), 
                        subtitle = "Registered Patients",
                        modifier = Modifier.weight(1f).clickable { onShowDetail("Total Patients") }
                    )
                    StatCard(
                        title = "Avg Patient Age", 
                        value = String.format("%.1f", uiState.avgPatientAge), 
                        subtitle = "Years", 
                        modifier = Modifier.weight(1f).clickable { onShowDetail("Average Age") }
                    )
                }
            }

            // Monthly Visits (using existing BarChart logic but mapped to Monthly data)
            item {
                SectionTitle("Monthly Visits")
                Box(modifier = Modifier.clickable { onShowDetail("Total Appointments") }) {
                    if (uiState.appointmentsMonthly.isEmpty()) {
                        Text("No Data", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        BarChart(
                            data = uiState.appointmentsMonthly,
                            modifier = Modifier.height(250.dp).fillMaxWidth()
                        )
                    }
                }
            }

            item {
                SectionTitle("Age Groups (Patients)")
                Box(modifier = Modifier.clickable { onShowDetail("Age Groups") }) {
                     if (uiState.ageDistribution.isEmpty()) {
                        Text("No Data", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        // Pie Chart logic needs Colors. 
                        // Generate colors based on size
                        val colors = listOf(
                            Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC)
                        )
                        PieChart(
                            data = uiState.ageDistribution,
                            colors = colors,
                            modifier = Modifier.height(250.dp).fillMaxWidth()
                        )
                    }
                }
            }

            item {
                SectionTitle("Top Doctors")
                 Box(modifier = Modifier.clickable { onShowDetail("Top Doctors") }) {
                     if (uiState.topDoctors.isEmpty()) {
                        Text("No data available", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        // Re-use ServiceUsageChart style (horizontal bars) or BarChart
                        // ServiceUsageChart is good for ranking
                         ServiceUsageChart(
                            data = uiState.topDoctors.mapValues { it.value.toInt() }, // Convert back to Int for display if Chart expects Int
                            modifier = Modifier.height(250.dp).fillMaxWidth()
                        )
                    }
                }
            }

            item {
                SectionTitle("Gender Distribution")
                Box(modifier = Modifier.clickable { onShowDetail("Gender Distribution") }) {
                    if (uiState.genderDistribution.isEmpty()) {
                         Text("No Data", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        PieChart(
                            data = uiState.genderDistribution,
                            colors = listOf(Color(0xFF42A5F5), Color(0xFFEF5350), Color.Gray),
                            modifier = Modifier.height(250.dp).fillMaxWidth()
                        )
                    }
                }
            }

            item {
                SectionTitle("Service Trends")
                Box(modifier = Modifier.clickable { onShowDetail("Service Usage") }) {
                    if (uiState.serviceUsage.isEmpty()) {
                        Text("No Data", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        ServiceUsageChart(
                            data = uiState.serviceUsage,
                            modifier = Modifier.height(250.dp).fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }
    }
}

@Composable
fun BarChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    val maxVal = data.values.maxOrNull()?.toFloat() ?: 1f
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val highlightColor = Color(0xFFFFC107).toArgb() // Amber
    
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val barWidth = size.width / (data.size * 1.5f)
                    val space = size.width / data.size
                    val index = (offset.x / space).toInt()
                    if (index in 0 until data.size) {
                         selectedKey = data.keys.elementAt(index)
                    } else {
                        selectedKey = null
                    }
                }
            }
        ) {
            val barWidth = size.width / (data.size * 1.5f)
            val space = size.width / data.size
            
            data.entries.forEachIndexed { index, entry ->
                val barHeight = (entry.value / maxVal) * size.height
                val x = index * space + (space - barWidth) / 2
                val y = size.height - barHeight
                
                val isSelected = selectedKey == entry.key
                val color = if (isSelected) primaryColor else secondaryColor
                
                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
                
                // Draw Label X-axis
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        entry.key,
                        x + barWidth / 2,
                        size.height + 40f, // Below bar
                        Paint().apply {
                            setColor(labelColor)
                            textAlign = Paint.Align.CENTER
                            textSize = 30f
                        }
                    )
                }
                
                if (isSelected) {
                     drawContext.canvas.nativeCanvas.apply {
                         drawText(
                            "${entry.value}",
                            x + barWidth / 2,
                            y - 20f,
                            Paint().apply {
                                setColor(highlightColor)
                                textAlign = Paint.Align.CENTER
                                textSize = 40f
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(data: Map<String, Float>, colors: List<Color>, modifier: Modifier = Modifier) {
    var selectedSection by remember { mutableStateOf<String?>(null) }
    val total = data.values.sum()
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()
    
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = kotlin.math.min(size.width, size.height) / 2f
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val dist = kotlin.math.sqrt(dx*dx + dy*dy)
                    
                    if (dist <= radius) {
                        var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (angle < 0) angle += 360f
                        
                        var currentAngle = 0f
                        data.entries.forEach { entry ->
                            val sweepAngle = (entry.value / total) * 360f
                            if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
                                selectedSection = entry.key
                                return@detectTapGestures
                            }
                            currentAngle += sweepAngle
                        }
                    } else {
                        selectedSection = null
                    }
                }
            }
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            var currentAngle = 0f
            
            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total) * 360f
                val color = colors[index % colors.size]
                val isSelected = selectedSection == entry.key
                val scale = if (isSelected) 1.1f else 1.0f
                
                drawArc(
                    color = color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius * scale, center.y - radius * scale),
                    size = Size(radius * 2 * scale, radius * 2 * scale)
                )
                
                // Draw Legend/Label roughly in center of slice
                val midAngle = Math.toRadians((currentAngle + sweepAngle / 2).toDouble())
                val labelR = radius * 0.7f
                val lx = center.x + labelR * cos(midAngle).toFloat()
                val ly = center.y + labelR * sin(midAngle).toFloat()
                
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${entry.key} ${(entry.value / total * 100).toInt()}%",
                        lx,
                        ly,
                        Paint().apply {
                            setColor(android.graphics.Color.WHITE) // Keep white inside pie slices
                            textAlign = Paint.Align.CENTER
                            textSize = 35f
                            setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)
                        }
                    )
                }
                
                currentAngle += sweepAngle
            }
        }
    }
}

@Composable
fun ServiceUsageChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    // Horizontal bars
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val maxVal = data.values.maxOrNull()?.toFloat() ?: 1f
        
        data.entries.forEach { entry ->
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(entry.key, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    Text("${entry.value} visits", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f), RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(entry.value / maxVal)
                            .height(12.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}



@Composable
fun LineChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    val maxVal = data.values.maxOrNull() ?: 1f
    
    // Get colors from theme to use in Canvas
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f).toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryColorArgb = primaryColor.toArgb()
    
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val space = size.width / (data.size - 1)
                    val index = (offset.x / space).toInt().coerceIn(0, data.size - 1)
                    if (Math.abs(offset.x - (index * space)) < space / 2) {
                        selectedKey = data.keys.elementAt(index)
                    }
                }
            }
        ) {
            val space = size.width / (data.size - 1)
            val path = Path()
            
            data.entries.forEachIndexed { index, entry ->
                val x = index * space
                val y = size.height - (entry.value / maxVal) * size.height
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                
                // Draw Dot
                drawCircle(
                    color = primaryColor,
                    radius = if(selectedKey == entry.key) 8.dp.toPx() else 4.dp.toPx(),
                    center = Offset(x, y)
                )
                
                // X-Axis Label
                 drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        entry.key,
                        x,
                        size.height + 40f,
                        Paint().apply {
                            setColor(labelColor)
                            textAlign = Paint.Align.CENTER
                            textSize = 30f
                        }
                    )
                }
                
                 if (selectedKey == entry.key) {
                     drawContext.canvas.nativeCanvas.apply {
                         drawText(
                            "RM ${entry.value.toInt()}",
                            x,
                            y - 30f,
                            Paint().apply {
                                setColor(primaryColorArgb)
                                textAlign = Paint.Align.CENTER
                                textSize = 40f
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
            
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Fill gradient below
            val fillPath = Path()
            fillPath.addPath(path)
            fillPath.lineTo(size.width, size.height)
            fillPath.lineTo(0f, size.height)
            fillPath.close()
            
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent)
                )
            )
        }
    }
}

@Composable
fun DetailStatsDialog(title: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mock list data based on title
                val items = when (title) {
                    "Total Patients" -> listOf("Yesterday: +10", "Today: +12", "This Week: +85", "Last Month: +320")
                    "Average Age" -> listOf("Ali - 32", "Abu - 45", "Siti - 28", "Ahmad - 35", "Mina - 22")
                    "Weekly Income" -> listOf("Mon: RM1200", "Tue: RM1500", "Wed: RM1100", "Thu: RM1800", "Fri: RM2200", "Sat: RM2500")
                    "Total Appointments" -> listOf("Jan - 40", "Feb - 55", "Mar - 30", "Apr - 70")
                    "Gender Distribution" -> listOf("Male - 150", "Female - 100")
                    "Service Usage" -> listOf("Checkup - 45", "Dental - 30", "Cardio - 15")
                    else -> listOf("Item 1", "Item 2", "Item 3")
                }
                
                items(items.size) { index ->
                    Text(
                        text = "${index + 1}. ${items[index]}",
                        fontSize = 16.sp
                    )
                    Divider(color = Color.LightGray)
                }
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    AnalyticsScreen(onBackClick = {})
}
