package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.ui.theme.KHSAppTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmBook(
    onViewAppointments: () -> Unit,
    onGoHome: () -> Unit
) {
    // The user's code uses 'onHome' but my existing signature has 'onViewAppointments' and 'onGoHome'.
    // To minimize friction for the MainActivity, I'll adapt the user's UI to use my existing callbacks.
    // The user's UI has only ONE "Ok" button.
    // I will map the "Ok" button to 'onGoHome' as that seems most logical (or onHome).

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
         // Custom Icon Composition: User Outline + Green Check
         Box(
             contentAlignment = Alignment.Center
         ) {
             // User Icon Outline
             Icon(
                 imageVector = Icons.Default.Person, // Using Person as base, ideally "PersonOutline" or custom SVG
                 contentDescription = null,
                 tint = Color.Green,
                 modifier = Modifier.size(120.dp)
             )
             // Checkmark overlay (top right or overlaid)
             Icon(
                 imageVector = Icons.Default.Check,
                 contentDescription = null,
                 tint = Color.Green,
                 modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-10).dp)
             )
         }
         
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Appointment\nsuccessfully booked.",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))
        
        Text(
            text = "Edit appointment is not available\ncontact default admin for changes",
             style = MaterialTheme.typography.bodySmall,
             textAlign = TextAlign.Center,
             color = Color.Gray
        )


        // "Ok" Button logic might need adjustment if they want it bottom aligned or center. 
        // Design shows a blue "Ok" button.
         Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGoHome,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF448AFF)),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text("Ok", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmBookPreview() {
    KHSAppTheme {
        ConfirmBook(
            onViewAppointments = {},
            onGoHome = {}
        )
    }
}
