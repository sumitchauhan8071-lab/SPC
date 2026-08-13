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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSDivider
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StaffAttendanceConfirmationScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val currentTime = timeFormat.format(Date())
    val currentDate = viewModel.repository.getCurrentDateString()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.StaffHome) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = IOSBlue)
                }
                Text(
                    text = "Confirm Attendance",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            IOSCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StatusPresentGreen.copy(alpha = 0.12f))
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusPresentGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ready to Check-In?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Please verify your attendance details below before confirming.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Rows
                ConfirmationDetailRow(
                    icon = Icons.Default.Person,
                    label = "Employee Name",
                    value = currentUser?.fullName ?: "N/A"
                )
                Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 12.dp))

                ConfirmationDetailRow(
                    icon = Icons.Default.Badge,
                    label = "Employee ID",
                    value = currentUser?.employeeId ?: "N/A"
                )
                Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 12.dp))

                ConfirmationDetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Current Date",
                    value = currentDate
                )
                Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 12.dp))

                ConfirmationDetailRow(
                    icon = Icons.Default.AccessTime,
                    label = "Current Time",
                    value = currentTime
                )
                Divider(color = IOSDivider, modifier = Modifier.padding(vertical = 12.dp))

                ConfirmationDetailRow(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    value = "Office HQ • GPS Verified"
                )

                Spacer(modifier = Modifier.height(28.dp))

                IOSButton(
                    text = "Confirm Attendance",
                    onClick = { viewModel.markAttendance() },
                    containerColor = StatusPresentGreen,
                    testTag = "confirm_attendance_btn"
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { viewModel.navigateTo(Screen.StaffHome) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = IOSTextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun ConfirmationDetailRow(
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IOSBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextSecondary
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = IOSTextPrimary
        )
    }
}
