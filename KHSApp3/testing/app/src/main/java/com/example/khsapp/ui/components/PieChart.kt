package com.example.khsapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    data: Map<String, Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    val safeTotal = if (total == 0f) 1f else total
    var startAngle = -90f
    
    // Animation
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
        ) { value, _ ->
            progress = value
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val strokeWidth = 30.dp.toPx() // Thicker stroke
            val diameter = size.minDimension
            val radius = diameter / 2
            
            data.values.forEachIndexed { index, value ->
                val sweepAngle = (value / safeTotal) * 360f * progress
                val color = colors.getOrElse(index) { Color.Gray }
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset((size.width - diameter) / 2 + strokeWidth/2, (size.height - diameter) / 2 + strokeWidth/2),
                    size = Size(diameter - strokeWidth, diameter - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
        
        // Center Text
        androidx.compose.material3.Text(
            text = "${data.values.sum().toInt()}",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}
