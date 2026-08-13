package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSDivider
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun StaffProfileScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        IOSCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            elevation = 3.dp
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(IOSBlue)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUser?.fullName?.take(2)?.uppercase() ?: "EM",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentUser?.fullName ?: "Employee",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "${currentUser?.designation} • ${currentUser?.departmentName}",
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileDetailItem(icon = Icons.Default.Badge, label = "Employee ID", value = currentUser?.employeeId ?: "--")
            Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 10.dp))

            ProfileDetailItem(icon = Icons.Default.Person, label = "Username", value = currentUser?.username ?: "--")
            Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 10.dp))

            ProfileDetailItem(icon = Icons.Default.Business, label = "Department", value = currentUser?.departmentName ?: "--")
            Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 10.dp))

            ProfileDetailItem(icon = Icons.Default.Work, label = "Designation", value = currentUser?.designation ?: "--")
            Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 10.dp))

            ProfileDetailItem(icon = Icons.Default.CalendarToday, label = "Joining Date", value = currentUser?.joiningDate ?: "--")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Change Password Card
        IOSCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            IOSTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = "Current Password",
                placeholder = "••••••••",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                testTag = "change_pass_old_input"
            )

            Spacer(modifier = Modifier.height(10.dp))

            IOSTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                placeholder = "••••••••",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                testTag = "change_pass_new_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            IOSButton(
                text = "Update Password",
                onClick = {
                    val userId = currentUser?.id ?: return@IOSButton
                    viewModel.resetEmployeePassword(userId, newPassword)
                    oldPassword = ""
                    newPassword = ""
                },
                testTag = "update_password_btn"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        IOSButton(
            text = "Logout",
            onClick = { viewModel.logout() },
            containerColor = StatusAbsentRed,
            icon = Icons.Default.Logout,
            testTag = "logout_btn"
        )
    }
}

@Composable
fun ProfileDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = IOSBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = IOSTextSecondary)
        }
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = IOSTextPrimary)
    }
}
