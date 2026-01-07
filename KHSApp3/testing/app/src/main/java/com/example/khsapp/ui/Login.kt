package com.example.khsapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Favorite

import com.example.khsapp.MainActivity
import com.example.khsapp.R
import com.example.khsapp.ui.components.CustomPasswordField
import com.example.khsapp.ui.components.CustomTextField
import com.example.khsapp.ui.theme.KHSAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        auth = FirebaseAuth.getInstance()



        setContent {
            KHSAppTheme {
                LoginScreen(
                    onLoginClick = { email, password ->
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("start_destination", "patient_dashboard")
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        try {
                                            throw task.exception!!
                                        } catch (e: FirebaseAuthInvalidUserException) {
                                            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(this, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                        }
                    },
                    onRegisterClick = {
                        // Navigate to RegisterActivity
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    },
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4)) // Soft mint/white background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo and Brand Name match
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF009688) // Teal
            ) {
                 Box(contentAlignment = Alignment.Center) {
                     Icon(
                         imageVector = Icons.Default.Favorite,
                         contentDescription = "Logo",
                         tint = Color.White,
                         modifier = Modifier.size(24.dp)
                     )
                 }
            }
            
            Spacer(Modifier.width(16.dp))

            Text(
                text = "KHS Bangi",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                )
            )
        }
        
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        )
        
        Text(
            text = "Sign in to manage your appointments",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email
                Column {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = email,
                        placeholder = "you@example.com",
                        icon = Icons.Default.Email
                    ) { email = it }
                }

                // Password
                Column {
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomPasswordField(
                        value = password,
                        placeholder = "........",
                        icon = Icons.Default.Lock
                    ) { password = it }
                }
                
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688), // Teal
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                         text = "Don't have an account? ",
                         fontSize = 14.sp,
                         color = Color.Gray
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF009688),
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    KHSAppTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onRegisterClick = {}
        )
    }
}
