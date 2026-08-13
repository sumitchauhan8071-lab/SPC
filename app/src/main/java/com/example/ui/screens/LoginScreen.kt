package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel
import com.example.ui.viewmodel.LoginViewModel

/**
 * Clean, modern Login Screen supporting direct Staff Login, Admin Login,
 * and dedicated buttons for Admin Account Creation.
 */
@Composable
fun LoginScreen(
    viewModel: AttendlyViewModel,
    loginViewModel: LoginViewModel = viewModel(),
    hasAdmin: Boolean = true
) {
    val uiState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(hasAdmin) {
        if (!hasAdmin) {
            loginViewModel.setSetupMode(true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(IOSBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Attendly Logo",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Attendly",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                ),
                color = IOSTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (uiState.isSetupMode) "Set Up Organization Admin" else "Employee Attendance Management",
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Auth Card
            IOSCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                elevation = 4.dp
            ) {
                if (!uiState.isSetupMode) {
                    // Segmented Toggle Control for Switching between Staff and Admin
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(IOSBackground)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Staff Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.selectedRoleTab == 0) IOSBlue else Color.Transparent)
                                .clickable { loginViewModel.selectRoleTab(0) }
                                .testTag("staff_role_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (uiState.selectedRoleTab == 0) Color.White else IOSTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Staff",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.selectedRoleTab == 0) Color.White else IOSTextSecondary
                                    )
                                )
                            }
                        }

                        // Admin Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.selectedRoleTab == 1) IOSBlue else Color.Transparent)
                                .clickable { loginViewModel.selectRoleTab(1) }
                                .testTag("admin_role_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (uiState.selectedRoleTab == 1) Color.White else IOSTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Admin",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.selectedRoleTab == 1) Color.White else IOSTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(
                    text = when {
                        uiState.isSetupMode -> "Create Admin Account"
                        uiState.selectedRoleTab == 0 -> "Staff Sign In"
                        else -> "Admin Sign In"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        uiState.isSetupMode -> "Create the primary administrator account for your company."
                        uiState.selectedRoleTab == 0 -> "Sign in with your staff username or employee ID."
                        else -> "Sign in with your administrator credentials."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Error Message Display
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Setup Mode Extra Field: Full Name
                AnimatedVisibility(visible = uiState.isSetupMode) {
                    Column {
                        IOSTextField(
                            value = uiState.fullName,
                            onValueChange = { loginViewModel.updateFullName(it) },
                            label = "Full Name",
                            placeholder = "Administrator Name",
                            leadingIcon = Icons.Default.Person,
                            testTag = "setup_fullname_input"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Username field
                IOSTextField(
                    value = uiState.username,
                    onValueChange = { loginViewModel.updateUsername(it) },
                    label = when {
                        uiState.isSetupMode -> "Username"
                        uiState.selectedRoleTab == 0 -> "Staff Username / ID"
                        else -> "Admin Username"
                    },
                    placeholder = when {
                        uiState.isSetupMode -> "admin"
                        uiState.selectedRoleTab == 0 -> "e.g. rahul.sharma or EMP-101"
                        else -> "e.g. admin"
                    },
                    leadingIcon = Icons.Default.Person,
                    testTag = "login_username_input"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                IOSTextField(
                    value = uiState.password,
                    onValueChange = { loginViewModel.updatePassword(it) },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = IOSTextSecondary
                            )
                        }
                    },
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    testTag = "login_password_input"
                )

                // Setup Mode Confirm Password field
                AnimatedVisibility(visible = uiState.isSetupMode) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        IOSTextField(
                            value = uiState.confirmPassword,
                            onValueChange = { loginViewModel.updateConfirmPassword(it) },
                            label = "Confirm Password",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            testTag = "setup_confirm_password_input"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Area
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = IOSBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else if (uiState.isSetupMode) {
                    // SETUP MODE: Primary Action -> Create Admin Account
                    IOSButton(
                        text = "Create Admin Account",
                        onClick = {
                            loginViewModel.setupFirstAdmin(
                                onSuccess = { admin -> viewModel.onUserAuthenticated(admin) },
                                onError = { msg -> viewModel.showToast(msg) }
                            )
                        },
                        testTag = "create_admin_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Downside Actions for Setup Mode: Admin Login & Staff Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                loginViewModel.setSetupMode(false)
                                loginViewModel.selectRoleTab(1) // Admin Login
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("admin_login_downside_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = IOSBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Admin Login",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = IOSBlue
                                    )
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                loginViewModel.setSetupMode(false)
                                loginViewModel.selectRoleTab(0) // Staff Login
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("staff_login_downside_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = IOSBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Staff Login",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = IOSBlue
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // LOGIN MODE: Primary Action -> Sign In
                    IOSButton(
                        text = if (uiState.selectedRoleTab == 0) "Sign In as Staff" else "Sign In as Admin",
                        onClick = {
                            loginViewModel.login(
                                onSuccess = { user -> viewModel.onUserAuthenticated(user) },
                                onError = { msg -> viewModel.showToast(msg) }
                            )
                        },
                        testTag = "sign_in_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Action -> Role Switch Button (Admin Login or Staff Login)
                    OutlinedButton(
                        onClick = {
                            val newRole = if (uiState.selectedRoleTab == 0) 1 else 0
                            loginViewModel.selectRoleTab(newRole)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("switch_role_login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.selectedRoleTab == 0) Icons.Default.Lock else Icons.Default.Person,
                                contentDescription = null,
                                tint = IOSBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.selectedRoleTab == 0) "Admin Login" else "Staff Login",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = IOSBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Separate Button for Admin Account Creation
                    TextButton(
                        onClick = { loginViewModel.setSetupMode(true) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = IOSBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Create Admin Account",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = IOSBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
