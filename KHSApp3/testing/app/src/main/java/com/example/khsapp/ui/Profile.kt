package com.example.khsapp.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth





@Composable
fun ProfileScreen(
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},

    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val currentUser = auth.currentUser

    // User State
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }


    // Fetch Data
    val userRepository = remember { com.example.khsapp.repository.UserRepository() }
    val scope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && currentUser != null) {
            isUploading = true
            scope.launch {
                val uploadResult = userRepository.uploadProfilePicture(uri, currentUser.uid)
                uploadResult.onSuccess { url ->
                    val updateResult = userRepository.updateUserProfilePicture(currentUser.uid, url)
                    updateResult.onSuccess {
                        profilePictureUrl = url
                        Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { e ->
                    android.util.Log.e("ProfileUpload", "Upload failed", e)
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                isUploading = false
            }
        }
    }


    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            email = currentUser.email ?: ""
            val user = userRepository.getUser(currentUser.uid)
            if (user != null) {
                name = user.fullName
                phone = user.phone
                address = user.mailingAddress
                dob = user.dateOfBirth
                gender = user.gender
                profilePictureUrl = user.profilePictureUrl
                if (user.email.isNotEmpty()) email = user.email // Prefer Firestore email
            }
        }
    }

    ProfileContent(
        name = name,
        email = email,
        phone = phone,
        dob = dob,
        gender = gender,
        address = address,
        profilePictureUrl = profilePictureUrl,
        isUploading = isUploading,
        onProfilePictureClick = {
            photoPickerLauncher.launch("image/*")
        },
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToHome = onNavigateToHome
    )
}
@Composable
fun ProfileContent(
    name: String,
    email: String,
    phone: String,
    dob: String,
    gender: String,
    address: String,
    profilePictureUrl: String,
    isUploading: Boolean,
    onProfilePictureClick: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF9FAFB), // Very light gray background
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home", tint = Color.Gray) },
                    selected = false,
                    onClick = onNavigateToHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, "Appointments", tint = Color.Gray) },
                    selected = false,
                    onClick = onNavigateToAppointments
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Outlined.Person,
                            "Profile",
                            tint = Color(0xFF009688)
                        )
                    },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                    onClick = {}
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp), // Increased padding
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    )
                    Text("Manage your personal information", color = Color.Gray, fontSize = 14.sp)
                }

                Button(
                    onClick = onNavigateToSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF374151), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Edit", color = Color(0xFF374151))
                }
            }
            // Member Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF9)), // Mint Greenish
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCFBF1))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2F1),
                        modifier = Modifier.size(56.dp).clickable { onProfilePictureClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (profilePictureUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profilePictureUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person, // Changed to Person icon
                                    null,
                                    tint = Color(0xFF009688),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF009688)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                    Column {
                        Text("Member since", fontSize = 13.sp, color = Color(0xFF4B5563))
                        Text("January 15, 2022", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                        Text("1447 days with us", fontSize = 13.sp, color = Color(0xFF009688), fontWeight = FontWeight.Medium)
                    }
                }
            }
            // Personal Information
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp) // Subtle shadow
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Personal Information", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF1F2937), modifier = Modifier.padding(bottom = 20.dp), fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)

                    // Row 1: Name & Phone
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { ReadOnlyField("Full Name", name, Icons.Outlined.Person) }
                        Box(Modifier.weight(1f)) { ReadOnlyField("Phone Number", phone, Icons.Default.Phone) }
                    }

                    // Row 2: Email & DOB
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { ReadOnlyField("Email", email, Icons.Default.Email) }
                        Box(Modifier.weight(1f)) { ReadOnlyField("Date of Birth", dob, Icons.Default.DateRange) }
                    }

                    // Gender
                    ReadOnlyField("Gender", gender, Icons.Default.Person)

                    // Address
                    ReadOnlyField("Address", address, Icons.Default.Home)


                }
            }



            Spacer(Modifier.height(32.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileContent(
        name = "Siti Aminah",
        email = "siti@example.com",
        phone = "+60123456789",
        dob = "10/10/1990",
        gender = "Female",
        address = "123 Jalan Ampang, KL",
        profilePictureUrl = "",
        isUploading = false,
        onNavigateToAppointments = {},
        onNavigateToSettings = {},
        onNavigateToHome = {},
        onProfilePictureClick = {}
    )
}

@Composable
fun ReadOnlyField(label: String, value: String, icon: ImageVector) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            leadingIcon = { Icon(icon, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFFE5E7EB),
                disabledBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color(0xFFF9FAFB), // Slight gray fill
                unfocusedContainerColor = Color(0xFFF9FAFB)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
        )
    }
}

