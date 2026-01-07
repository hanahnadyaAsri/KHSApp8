package com.example.khsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.ui.components.CustomPasswordField
import com.example.khsapp.ui.components.CustomTextField
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    // State simulating backend values
    var name by remember { mutableStateOf("Siti Aminah") }
    var phone by remember { mutableStateOf("+60123456789") }
    var email by remember { mutableStateOf("siti.aminah@example.com") }
    
    // Edit Profile Logic
    var isEditingProfile by remember { mutableStateOf(false) }
    var lastEditDate by remember { mutableStateOf<Date?>(null) } // Would come from DB
    val cooldownDays = 14
    var showEditError by remember { mutableStateOf(false) }
    
    // Change Password Logic
    var isChangingPassword by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Profile Section with Image
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                 Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Box(contentAlignment = Alignment.BottomEnd) {
                         Surface(
                             shape = CircleShape,
                             color = Color(0xFFE0F2F1),
                             modifier = Modifier.size(80.dp)
                         ) {
                             Box(contentAlignment = Alignment.Center) {
                                 Icon(
                                     Icons.Default.Person, 
                                     null, 
                                     tint = Color(0xFF009688),
                                     modifier = Modifier.size(40.dp)
                                 )
                             }
                         }
                         Surface(
                             shape = CircleShape,
                             color = Color(0xFF009688),
                             modifier = Modifier.size(28.dp).clickable { /* TODO: Image Picker */ }
                         ) {
                             Box(contentAlignment = Alignment.Center) {
                                 Icon(Icons.Default.Create, null, tint = Color.White, modifier = Modifier.size(16.dp))
                             }
                         }
                     }
                     Spacer(modifier = Modifier.height(12.dp))
                     Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                     Text(email, color = Color.Gray, fontSize = 14.sp)
                 }
            }

            // Settings Sections
            Text("Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItem(
                        title = "Edit Profile", 
                        subtitle = "Name, Phone, Address",
                        onClick = {
                            // Check cooldown
                            val now = Date()
                            val twoWeeksMillis = cooldownDays * 24 * 60 * 60 * 1000L
                            if (lastEditDate != null && (now.time - lastEditDate!!.time) < twoWeeksMillis) {
                                showEditError = true
                            } else {
                                isEditingProfile = true
                            }
                        }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SettingsItem(
                        title = "Change Password", 
                        subtitle = "Security & Access",
                        onClick = { isChangingPassword = true }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SettingsItem(
                        title = "Notifications", 
                        trailContent = {
                            var checked by remember { mutableStateOf(true) }
                            Switch(
                                checked = checked, 
                                onCheckedChange = { checked = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF009688))
                            )
                        }
                    )
                }
            }

            Text("General", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
             Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                     SettingsItem(
                        title = "Language", 
                        subtitle = "English",
                        onClick = {}
                    )
                     HorizontalDivider(color = Color(0xFFEEEEEE))
                    SettingsItem(
                        title = "Dark Mode", 
                        trailContent = {
                            var checked by remember { mutableStateOf(false) }
                            Switch(checked = checked, onCheckedChange = { checked = it })
                        }
                    )
                }
             }
             
             Spacer(modifier = Modifier.weight(1f))
             
             Text(
                 "App Version 1.0.0", 
                 modifier = Modifier.fillMaxWidth(), 
                 textAlign = TextAlign.Center,
                 color = Color.Gray,
                 fontSize = 12.sp
             )
        }
    }
    
    // Bottom Sheets or Dialogs would go here for Edit/Password
    if (showEditError) {
        AlertDialog(
            onDismissRequest = { showEditError = false },
            confirmButton = { TextButton(onClick = { showEditError = false }) { Text("OK") } },
            title = { Text("Limit Reached") },
            text = { Text("You can only edit your profile once every 2 weeks. Please try again later.") }
        )
    }
    
    if (isEditingProfile) {
        EditProfileDialog(
            currentName = name,
            currentPhone = phone,
            onDismiss = { isEditingProfile = false },
            onSave = { newName, newPhone ->
                name = newName
                phone = newPhone
                lastEditDate = Date() // Set cooldown timestamp
                isEditingProfile = false
            }
        )
    }
    
    if (isChangingPassword) {
        ChangePasswordDialog(
            onDismiss = { isChangingPassword = false },
            onSave = {
                // Handle Password Change
                isChangingPassword = false
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            if (subtitle != null) {
                Text(subtitle, color = Color.Gray, fontSize = 13.sp)
            }
        }
        if (trailContent != null) {
            trailContent()
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentPhone: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                Text("Note: You can only edit this once every 14 days.", fontSize = 12.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, phone) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = oldPass, onValueChange = { oldPass = it }, label = { Text("Old Password") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("New Password") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                OutlinedTextField(value = confirmPass, onValueChange = { confirmPass = it }, label = { Text("Confirm New Password") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (newPass != confirmPass) {
                    error = "Passwords do not match"
                } else if (newPass.length < 6) {
                    error = "Password too short"
                } else {
                    onSave() // In real app, re-auth and update
                }
            }) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        onNavigateBack = {}
    )
}
