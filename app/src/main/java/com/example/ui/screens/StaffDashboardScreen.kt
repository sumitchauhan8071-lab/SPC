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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AttendanceEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatSummaryCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSDivider
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel
import com.example.ui.viewmodel.Screen
import java.util.Calendar

@Composable
fun StaffDashboardScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val todayAttendance by viewModel.staffTodayAttendance.collectAsState()
    val userHistory by viewModel.repository.getAttendanceHistoryForUser(currentUser?.id ?: 0)
        .collectAsState(initial = emptyList())

    LaunchedEffect(currentUser) {
        viewModel.refreshStaffTodayAttendance()
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$greeting,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
                Text(
                    text = currentUser?.fullName ?: "Employee",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = IOSTextPrimary
                )
            }
            IconButton(
                onClick = { viewModel.navigateTo(Screen.StaffNotifications) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = IOSBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prominent Today's Attendance Card
        IOSCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 22.dp,
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TODAY'S ATTENDANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = IOSTextSecondary
                    )
                    Text(
                        text = viewModel.repository.getCurrentDateString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = IOSTextSecondary
                    )
                }
                IOSStatusBadge(status = todayAttendance?.status ?: "NOT_MARKED")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-In / Check-Out details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Check-In", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                    Text(
                        text = todayAttendance?.checkInTime ?: "-- : --",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextPrimary
                    )
                }
                Column {
                    Text("Check-Out", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                    Text(
                        text = todayAttendance?.checkOutTime ?: "-- : --",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextPrimary
                    )
                }
                Column {
                    Text("Working Hours", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                    val mins = todayAttendance?.workingHoursMinutes ?: 0
                    val hoursText = if (mins > 0) "${mins / 60}h ${mins % 60}m" else "--"
                    Text(
                        text = hoursText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = IOSBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            when {
                todayAttendance == null -> {
                    IOSButton(
                        text = "MARK ATTENDANCE",
                        onClick = { viewModel.navigateTo(Screen.StaffConfirmation) },
                        icon = Icons.Default.CheckCircle,
                        containerColor = StatusPresentGreen,
                        testTag = "mark_attendance_main_btn"
                    )
                }
                todayAttendance?.checkOutTime == null -> {
                    IOSButton(
                        text = "CHECK OUT",
                        onClick = { viewModel.performCheckOut() },
                        icon = Icons.Default.ExitToApp,
                        containerColor = IOSBlue,
                        testTag = "check_out_main_btn"
                    )
                }
                else -> {
                    IOSButton(
                        text = "ATTENDANCE MARKED TODAY",
                        onClick = {},
                        enabled = false,
                        testTag = "attendance_completed_btn"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats
        val presentCount = userHistory.count { it.status == "PRESENT" || it.status == "LATE" }
        val lateCount = userHistory.count { it.status == "LATE" }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IOSStatSummaryCard(
                title = "Days Present",
                value = "$presentCount",
                subtext = "This Month",
                icon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
            IOSStatSummaryCard(
                title = "Late Arrivals",
                value = "$lateCount",
                subtext = "Grace Period 15m",
                icon = Icons.Default.AccessTime,
                accentColor = Color(0xFFFF9500),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent History Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge,
                color = IOSBlue,
                modifier = Modifier.testTag("view_all_history").padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (userHistory.isEmpty()) {
            IOSCard {
                Text(
                    text = "No previous attendance records found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
        } else {
            userHistory.take(5).forEach { item ->
                IOSHistoryRowItem(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun IOSHistoryRowItem(item: AttendanceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
                val times = if (item.checkInTime != null) {
                    "${item.checkInTime} → ${item.checkOutTime ?: "Active"}"
                } else {
                    "No check-in recorded"
                }
                Text(
                    text = times,
                    style = MaterialTheme.typography.bodySmall,
                    color = IOSTextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                IOSStatusBadge(status = item.status)
                if (item.workingHoursMinutes > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${item.workingHoursMinutes / 60}h ${item.workingHoursMinutes % 60}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = IOSTextSecondary
                    )
                }
            }
        }
    }
}
