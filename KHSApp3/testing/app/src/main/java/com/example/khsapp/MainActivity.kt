package com.example.khsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.khsapp.ui.*
import com.example.khsapp.ui.theme.KHSAppTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp
import java.util.Date
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KHSAppTheme {
                // Shared ViewModels
                val bookingViewModel: BookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                
                val loggedInDoctor by remember { mutableStateOf<com.example.khsapp.model.Doctor?>(null) }
                
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val startDestination = intent.getStringExtra("start_destination") ?: "login_route"
                    NavHost(
                        navController = navController,
                        startDestination = startDestination, // Dynamic start destination
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // --- Patient Flow ---
                        
                        composable("login_route") {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    val auth = FirebaseAuth.getInstance()
                                    if(email.isNotEmpty() && password.isNotEmpty()) {
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    navController.navigate("patient_dashboard") {
                                                        popUpTo("login_route") { inclusive = true }
                                                    }
                                                } else {
                                                    // Handle error
                                                }
                                            }
                                    }
                                },

                                onRegisterClick = { navController.navigate("register_route") },
                            )
                        }
                        
                        composable("register_route") {
                            val userRepository = remember { com.example.khsapp.repository.UserRepository() }
                            val scope = rememberCoroutineScope()
                            
                            SignUpScreen(
                                onSignupClick = { email, fullname, phone, pass, confirmpass, mailingAddress, dob, gender, imageUri ->
                                    // Basic validation
                                    if (email.isEmpty() || fullname.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                                         android.widget.Toast.makeText(this@MainActivity, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                                         return@SignUpScreen
                                    }
                                    if (pass != confirmpass) {
                                        android.widget.Toast.makeText(this@MainActivity, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                                        return@SignUpScreen
                                    }

                                    val auth = FirebaseAuth.getInstance()
                                    
                                    auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener { authResult ->
                                         val uid = authResult.user!!.uid
                                         scope.launch {
                                             android.widget.Toast.makeText(this@MainActivity, "Auth Success. Saving Profile...", android.widget.Toast.LENGTH_SHORT).show()
                                             
                                             var profileUrl = ""
                                             if (imageUri != null) {
                                                  val uploadResult = userRepository.uploadProfilePicture(imageUri, uid)
                                                  uploadResult.onSuccess { url -> profileUrl = url }
                                                  // Log or toast if upload fails? For now proceeding.
                                             }

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
                                             
                                             userRepository.saveUserWithId(uid, newUser)
                                                .onSuccess {
                                                    android.widget.Toast.makeText(this@MainActivity, "Signup Successful! ID: $it", android.widget.Toast.LENGTH_SHORT).show()
                                                    navController.navigate("patient_dashboard") {
                                                        popUpTo("login_route") { inclusive = true }
                                                    }
                                                }
                                                .onFailure { e ->
                                                     android.widget.Toast.makeText(this@MainActivity, "Saved user but db failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                                                     navController.navigate("patient_dashboard") {
                                                        popUpTo("login_route") { inclusive = true }
                                                    }
                                                }
                                         }
                                    }.addOnFailureListener { e ->
                                        android.widget.Toast.makeText(this@MainActivity, "Signup Failed: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        composable("patient_dashboard") {
                            PatientDashboard(
                                onNavigateToBook = { name ->
                                    bookingViewModel.setPatientName(name) 
                                    navController.navigate("service_selection") 
                                },
                                onNavigateToViewAppointments = { navController.navigate("view_appointments") },
                                onNavigateToProfile = { navController.navigate("profile_home") },
                                onNavigateToFeedback = { navController.navigate("feedback_rating") }
                            )
                        }
                        
                        composable("feedback_rating") {
                            AppointmentHistoryList(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToProfile = { navController.navigate("profile_home") }
                            )
                        }

                        composable("profile_home") {
                            // We need to update ProfileScreen to accept onNavigateToHome if possible, 
                            // or we assume it has a bottom bar.
                            // The ProfileScreen in Profile.kt has a bottom bar with Home icon.
                            // We should probably modify ProfileScreen slightly to accept onNavigateToHome or handleClick.
                            // For now, let's use the existing params.
                            ProfileScreen(
                                onNavigateToAppointments = { navController.navigate("view_appointments") },
                                onNavigateToSettings = { navController.navigate("settings_route") },
                                onNavigateToHome = {
                                    navController.navigate("patient_dashboard") {
                                        popUpTo("patient_dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("settings_route") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("service_selection") {
                            ServiceSelectionScreen(
                                onServiceSelected = { service ->
                                    bookingViewModel.setService(service)
                                    navController.navigate("doctor_selection") // No arg needs, getting from VM state if needed or just flow
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("doctor_selection") {
                            val bookingState by bookingViewModel.bookingState.collectAsState()
                            DoctorSelection(
                                serviceId = bookingState.serviceId,
                                onDoctorSelected = { doctor ->
                                    bookingViewModel.setDoctor(doctor)
                                    navController.navigate("time_selection")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("time_selection") {
                           val bookingState by bookingViewModel.bookingState.collectAsState()
                            
                            // Reconstruction of Doctor object from Appointment state so TimeSelection works
                            val doctor = if (bookingState.doctorName.isNotEmpty()) {
                                com.example.khsapp.model.Doctor(
                                    doctorId = bookingState.doctorId,
                                    doctorName = bookingState.doctorName,
                                    drSpecialization = ""
                                )
                            } else null

                            TimeSelection(
                                currentDoctor = doctor, 
                                onDateTimeSelected = { date, time ->
                                    bookingViewModel.setTime(date, time)
                                    navController.navigate("payment")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("payment") {
                            val bookingState by bookingViewModel.bookingState.collectAsState()
                            // We need to implement the PaymentMethod callback logic
                            val appointmentRepo = remember { com.example.khsapp.repository.BookingRepository() }
                            val scope = rememberCoroutineScope()
                             
                            Payment(
                                bookingState = bookingState,
                                onPaymentSelected = { method, onComplete ->
                                     // bookingState IS the appointment now.
                                     // Just refresh timestamp and ensure status
                                     val appointment = bookingState.copy(
                                         status = "Upcoming",
                                         timestamp = Timestamp(Date())
                                     )
                                     
                                     scope.launch {
                                        val result = appointmentRepo.confirmBooking(
                                            appointment = appointment,
                                            paymentMethod = method,
                                            totalAmount = (appointment.price) * 0.5 // Deposit
                                        )
                                        
                                        if (result.isSuccess) {
                                            onComplete()
                                            navController.navigate("booking_success")
                                        } else {
                                            onComplete()
                                            val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                                            android.widget.Toast.makeText(this@MainActivity, "Booking Failed: $errorMessage", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    } 
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("booking_success") {
                            ConfirmBook(
                                onViewAppointments = {
                                    bookingViewModel.reset()
                                    navController.navigate("view_appointments") {
                                        popUpTo("profile_home") 
                                    }
                                },
                                onGoHome = {
                                    bookingViewModel.reset()
                                    navController.navigate("patient_dashboard") {
                                        popUpTo("patient_dashboard") { inclusive = false }
                                    }
                                }
                            )
                        }
                        
                        composable("view_appointments") {
                            ViewAppointmentScreen(
                                onCancelClick = { appointmentId ->
                                    navController.navigate("cancel_appointment/$appointmentId")
                                }
                            )
                        }
                        
                        composable("cancel_appointment/{appointmentId}") { backStackEntry ->
                            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
                            CancelAppointmentScreen(
                                appointmentId = appointmentId,
                                onBackClick = { navController.popBackStack() },
                                onConfirmCancelClick = {
                                    navController.navigate("confirm_cancel/$appointmentId")
                                }
                            )
                        }
                        
                        composable("confirm_cancel/{appointmentId}") { backStackEntry ->
                            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                            val repo = remember { com.example.khsapp.repository.BookingRepository() }
                            
                            ConfirmCancelScreen(
                                serviceName = "Appointment", // Placeholder
                                date = "Date",
                                time = "Time",
                                onConfirmCancel = {
                                    repo.cancelAppointment(appointmentId, "User Cancelled")
                                    navController.navigate("view_appointments") {
                                        popUpTo("view_appointments") { inclusive = true }
                                    }
                                },
                                onKeepAppointment = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        
                        // --- Doctor Flow ---
                        // "doctor_login" is removed as requested. "doctor_dashboard" is left orphaned/unreachable for now.
                        
                        composable("doctor_dashboard") {
                            // Use the loggedInDoctor state
                            val doctor = loggedInDoctor ?: com.example.khsapp.model.Doctor("0", "Unknown", "Unknown")
                            val repo = remember { com.example.khsapp.repository.BookingRepository() }
                            
                            DoctorDashboard(
                                doctor = doctor,
                                repository = repo,
                                onBack = { navController.popBackStack() },
                                onViewDoctorSchedule = { navController.navigate("doctor_schedule") },
                                onViewStaffSchedule = { /* TODO: Enable later */ },
                                onViewAnalytics = { navController.navigate("analytics_route") }
                            )
                        }
                        
                         composable("doctor_schedule") {
                             ViewScheduleDoctorScreen()
                         }

                        composable("analytics_route") {
                            com.example.khsapp.ui.AnalyticsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // --- Staff/Admin Flow ---
                        composable("staff_schedule") {
                             ViewScheduleScreen()
                         }
                         

                    }
                }
            }
        }
    }
}