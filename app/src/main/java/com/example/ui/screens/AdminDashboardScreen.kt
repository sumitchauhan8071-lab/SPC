package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AttendanceEntity
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatSummaryCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSCardSurface
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.theme.StatusLateOrange
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel
import com.example.ui.viewmodel.Screen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allStaff by viewModel.allStaff.collectAsState()
    val todayAttendanceList by viewModel.repository.getTodayAttendanceList().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("All") }
    var selectedStatus by remember { mutableStateOf("All") }
    var deptExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val departments = listOf("All", "IT", "HR", "Sales", "Accounts", "Operations", "Production", "Administration")
    val statusList = listOf("All", "PRESENT", "ABSENT", "LATE", "HALF_DAY", "LEAVE")

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val totalEmp = allStaff.size.coerceAtLeast(10)
    val presentToday = todayAttendanceList.count { it.status == "PRESENT" }
    val absentToday = todayAttendanceList.count { it.status == "ABSENT" }
    val lateToday = todayAttendanceList.count { it.status == "LATE" }
    val leaveToday = todayAttendanceList.count { it.status == "LEAVE" }
    val attendancePct = if (totalEmp > 0) {
        ((presentToday + lateToday).toFloat() / totalEmp * 100).coerceIn(0f, 100f)
    } else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
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
                        text = currentUser?.fullName ?: "Admin",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = IOSTextPrimary
                    )
                }
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.AdminNotifications) },
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
        }

        // Summary Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IOSStatSummaryCard(
                        title = "Total Employees",
                        value = "$totalEmp",
                        subtext = "Active Staff",
                        icon = Icons.Default.Group,
                        modifier = Modifier.weight(1f)
                    )
                    IOSStatSummaryCard(
                        title = "Present Today",
                        value = "$presentToday",
                        subtext = "On-time Check-ins",
                        icon = Icons.Default.CheckCircle,
                        accentColor = StatusPresentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IOSStatSummaryCard(
                        title = "Absent Today",
                        value = "$absentToday",
                        subtext = "Unexcused",
                        icon = Icons.Default.EventBusy,
                        accentColor = StatusAbsentRed,
                        modifier = Modifier.weight(1f)
                    )
                    IOSStatSummaryCard(
                        title = "Late Today",
                        value = "$lateToday",
                        subtext = "Grace Period 15m",
                        icon = Icons.Default.Schedule,
                        accentColor = StatusLateOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IOSStatSummaryCard(
                        title = "On Leave",
                        value = "$leaveToday",
                        subtext = "Approved Requests",
                        icon = Icons.Default.TimeToLeave,
                        accentColor = StatusLeaveBlue,
                        modifier = Modifier.weight(1f)
                    )
                    IOSStatSummaryCard(
                        title = "Attendance %",
                        value = "%.1f%%".format(attendancePct),
                        subtext = "Daily Compliance",
                        icon = Icons.Default.CheckCircle,
                        accentColor = IOSBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Filter & Search Section
        item {
            IOSCard(cornerRadius = 20.dp) {
                Text(
                    text = "Today's Attendance",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                IOSTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search Employee or ID",
                    placeholder = "Search by name or employee ID...",
                    leadingIcon = Icons.Default.Search,
                    testTag = "admin_attendance_search_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Department Filter
                    ExposedDropdownMenuBox(
                        expanded = deptExpanded,
                        onExpandedChange = { deptExpanded = !deptExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Dept: $selectedDept",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = IOSCardSurface,
                                unfocusedContainerColor = IOSBackground
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = deptExpanded,
                            onDismissRequest = { deptExpanded = false }
                        ) {
                            departments.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        selectedDept = d
                                        deptExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Status Filter
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Status: $selectedStatus",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = IOSCardSurface,
                                unfocusedContainerColor = IOSBackground
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            statusList.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        selectedStatus = s
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Attendance Table / Cards
        val filtered = todayAttendanceList.filter { record ->
            val matchesQuery = record.employeeName.contains(searchQuery, ignoreCase = true) ||
                    record.employeeId.contains(searchQuery, ignoreCase = true)
            val matchesDept = selectedDept == "All" || record.departmentName.equals(selectedDept, ignoreCase = true)
            val matchesStatus = selectedStatus == "All" || record.status.equals(selectedStatus, ignoreCase = true)
            matchesQuery && matchesDept && matchesStatus
        }

        if (filtered.isEmpty()) {
            item {
                IOSCard {
                    Text(
                        text = "No attendance records match the selected filters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IOSTextSecondary
                    )
                }
            }
        } else {
            items(filtered) { record ->
                AdminAttendanceRowItem(record)
            }
        }
    }
}

@Composable
fun AdminAttendanceRowItem(record: AttendanceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = record.employeeName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextPrimary
                    )
                    Text(
                        text = "${record.employeeId} • ${record.departmentName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IOSTextSecondary
                    )
                }
                IOSStatusBadge(status = record.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "In: ${record.checkInTime ?: "--"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = IOSTextSecondary
                )
                Text(
                    text = "Out: ${record.checkOutTime ?: "--"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = IOSTextSecondary
                )
                val hoursText = if (record.workingHoursMinutes > 0)
                    "${record.workingHoursMinutes / 60}h ${record.workingHoursMinutes % 60}m"
                else "--"
                Text(
                    text = "Hours: $hoursText",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = IOSBlue
                )
            }
        }
    }
}
