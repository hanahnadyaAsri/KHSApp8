package com.example.khsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val rawMax = data.values.maxOrNull() ?: 0
    val max = if (rawMax == 0) 1 else rawMax
    
    // Animation state
    // distinct animation on load. 
    // Using LaunchedEffect for entry animation.
    var progress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        ) { value, _ ->
            progress = value
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            val barHeightFraction = (value / max.toFloat()) * progress
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                 // Value Label (Optional, maybe show only on valid data)
                 if (value > 0) {
                     Text(
                        text = value.toString(), 
                        fontSize = 10.sp, 
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                     )
                 }
                 
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight(barHeightFraction)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF26A69A), // Light Teal
                                    Color(0xFF009688)  // Primary Teal
                                )
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = label, 
                    fontSize = 10.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
    }
}
