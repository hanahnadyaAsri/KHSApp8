package com.example.khsapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khsapp.ui.components.CustomPasswordField
import com.example.khsapp.ui.components.CustomTextField
import com.example.khsapp.ui.theme.KHSAppTheme
import android.util.Patterns
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.khsapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.khsapp.R


class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()


        setContent {
            KHSAppTheme {
                SignUpScreen(
                    onSignupClick = { email, fullname, phone, pass, _, mailingAddress, dob, gender, imageUri ->
                        auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@RegisterActivity, "Auth Success. Uploading/Saving...", Toast.LENGTH_SHORT).show()
                                    val uid = task.result.user!!.uid
                                    
                                    fun saveUserToFirestore(profileUrl: String) {
                                        val newUser = com.example.khsapp.model.User(
                                            email = email,
                                            fullName = fullname,
                                            phone = phone,
                                            mailingAddress = mailingAddress,
                                            dateOfBirth = dob,
                                            gender = gender,
                                            role = "Patient",
                                            profilePictureUrl = profileUrl
                                        )
                                        val userRepository = UserRepository()
                                        
                                        lifecycleScope.launch {
                                            userRepository.saveUserWithId(uid, newUser)
                                                .onSuccess {
                                                    Toast.makeText(this@RegisterActivity, "Signup Successful!", Toast.LENGTH_LONG).show()
                                                    finish()
                                                }
                                                .onFailure {
                                                    Toast.makeText(this@RegisterActivity, "DB Save Failed: ${it.message}", Toast.LENGTH_LONG).show()
                                                    finish()
                                                }
                                        }
                                    }

                                    if (imageUri != null) {
                                        val storageRef = FirebaseStorage.getInstance().reference
                                        val profileRef = storageRef.child("profiles/patients/${uid}.jpg")
                                        
                                        profileRef.putFile(imageUri)
                                            .addOnSuccessListener {
                                                profileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                                                    saveUserToFirestore(uri.toString())
                                                }
                                            }
                                            .addOnFailureListener { e: Exception ->
                                                Toast.makeText(this@RegisterActivity, "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                saveUserToFirestore("") // Proceed without image
                                            }
                                    } else {
                                        saveUserToFirestore("")
                                    }

                                } else {
                                    try {
                                        throw task.exception!!
                                    } catch (e: FirebaseAuthUserCollisionException) {
                                        Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(this, "Signup Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                )
                    }

            }
        }
    }

@Composable
fun SignUpScreen(
    onSignupClick: (String, String, String, String, String, String, String, String, Uri?) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mailingAddress by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    var isEmailError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isAddressError by remember { mutableStateOf(false) }
    var isPassError by remember { mutableStateOf(false) }
    var isConfirmPassError by remember { mutableStateOf(false) }
    var isDobError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4)) // Soft mint/white background
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937) // Dark text
            )
        )
        
        Text(
            text = "Join us for a healthier tomorrow",
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Center the image
            ) {
                // Image Picker
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { 
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Fallback/Placeholder
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Camera Icon Overlay
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Photo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF009688), CircleShape)
                            .padding(4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Full Name
                Column {
                    Text(
                        text = "Full Name",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = fullname,
                        placeholder = "Your Name",
                        icon = Icons.Default.Person,
                        isError = isNameError
                    ) { 
                        fullname = it 
                        if(isNameError) isNameError = false
                    }
                }

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
                        icon = Icons.Default.Email,
                        isError = isEmailError
                    ) { 
                        email = it 
                        if(isEmailError) isEmailError = false
                    }
                }
                
                // Phone
                Column {
                    Text(
                        text = "Phone Number",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                     CustomTextField(
                        value = phone,
                        placeholder = "+60-12345630",
                        icon = Icons.Default.Phone,
                        isError = isPhoneError
                    ) { 
                        phone = it 
                        if(isPhoneError) isPhoneError = false
                    }
                }

                // Address
                Column {
                    Text(
                        text = "Address",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = mailingAddress,
                        placeholder = "No,street,village",
                        icon = Icons.Default.Home,
                        isError = isAddressError
                    ) {
                        mailingAddress = it
                        if(isAddressError) isAddressError = false
                    }
                }

                // Date of Birth
                val context = androidx.compose.ui.platform.LocalContext.current
                val calendar = java.util.Calendar.getInstance()
                
                // Date Picker Dialog
                val datePickerDialog = android.app.DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        // Format: DD/MM/YYYY
                        dob = "$dayOfMonth/${month + 1}/$year"
                        if(isDobError) isDobError = false
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                )

                Column {
                    Text(
                        text = "Date of Birth",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Use readOnly instead of disabled to keep text color black but prevent typing
                    // Click logic handled by modifier overlay or direct clickable if CustomTextField exposes it (it propagates modifier to OutlinedTextField, which might capture click if enabled)
                    // Safest: Box overlay approach with readOnly=true (so keyboard doesnt show)
                    Box {
                        CustomTextField(
                            value = dob,
                            placeholder = "DD/MM/YYYY",
                            icon = Icons.Default.DateRange,
                            isError = isDobError,
                            readOnly = true, // Prevent typing
                            enabled = false // Disable interaction so Box catches click
                        ) {
                            // No-op
                        }
                        
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { datePickerDialog.show() }
                        )
                    }
                }

                // Gender
                Column {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Male",
                            onClick = { gender = "Male" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF009688))
                        )
                        Text("Male")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = gender == "Female",
                            onClick = { gender = "Female" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF009688))
                        )
                        Text("Female")
                    }
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
                        icon = Icons.Default.Lock,
                        isError = isPassError
                    ) { 
                        password = it 
                        if(isPassError) isPassError = false
                    }
                }

                // Confirm Password
                Column {
                    Text(
                        text = "Confirm Password",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomPasswordField(
                        value = confirmPassword,
                        placeholder = "........",
                        icon = Icons.Default.Lock,
                        isError = isConfirmPassError
                    ) { 
                        confirmPassword = it 
                        if(isConfirmPassError) isConfirmPassError = false
                    }
                }

                Spacer(Modifier.height(8.dp))



                Button(
                    onClick = {
                        // Validation Logic
                        var valid = true
                        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            isEmailError = true
                            valid = false
                        }
                        if (fullname.isEmpty()) {
                            isNameError = true
                            valid = false
                        }
                         if (phone.isEmpty()) {
                            isPhoneError = true
                            valid = false
                        }
                        if (mailingAddress.isEmpty()) {
                            isAddressError = true
                            valid = false
                        }
                        if (dob.isEmpty()) {
                            isDobError = true
                            valid = false
                        }
                        // Password Validation: 8 alphanumeric
                        val alpanumericRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$".toRegex()
                        if (password.isEmpty() || !alpanumericRegex.matches(password)) {
                            isPassError = true
                            valid = false
                            if(password.isNotEmpty()){
                                 Toast.makeText(context, "Password must be at least 8 characters and alphanumeric", Toast.LENGTH_SHORT).show()
                            }
                        }
                        if (confirmPassword != password) {
                            isConfirmPassError = true
                            valid = false
                        }

                        if (valid) {
                            onSignupClick(email, fullname, phone, password, confirmPassword, mailingAddress, dob, gender, selectedImageUri)
                        } else {
                            Toast.makeText(context, "Please fix errors", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                        shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688), // Teal Color
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    KHSAppTheme {
        SignUpScreen(
            onSignupClick = { _, _, _, _, _, _, _, _, _ -> }
        )
    }
}
