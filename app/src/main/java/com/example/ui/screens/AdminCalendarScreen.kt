package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entities.UserEntity
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentBg
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.theme.StatusHalfDayAmber
import com.example.ui.theme.StatusHalfDayBg
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateOrange
import com.example.ui.theme.StatusLeaveBg
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCalendarScreen(
    viewModel: AttendlyViewModel
) {
    val allStaff by viewModel.allStaff.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()

    var selectedStaff by remember { mutableStateOf<UserEntity?>(allStaff.firstOrNull()) }
    var staffExpanded by remember { mutableStateOf(false) }
    var selectedDayRecord by remember { mutableStateOf<AttendanceEntity?>(null) }

    val daysInMonth = (1..31).toList()
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Attendance Calendar",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )
        Text(
            text = "Monthly calendar breakdown per staff member",
            style = MaterialTheme.typography.bodyMedium,
            color = IOSTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Staff Selector Dropdown
        IOSCard(cornerRadius = 18.dp) {
            Text("Select Employee", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = staffExpanded,
                onExpandedChange = { staffExpanded = !staffExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStaff?.fullName ?: "Select Employee",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = staffExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("calendar_staff_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = staffExpanded,
                    onDismissRequest = { staffExpanded = false }
                ) {
                    allStaff.forEach { s ->
                        DropdownMenuItem(
                            text = { Text("${s.fullName} (${s.employeeId})") },
                            onClick = {
                                selectedStaff = s
                                staffExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LegendDot(color = StatusPresentGreen, label = "Present")
            LegendDot(color = StatusAbsentRed, label = "Absent")
            LegendDot(color = StatusLateOrange, label = "Late")
            LegendDot(color = StatusLeaveBlue, label = "Leave")
            LegendDot(color = StatusHalfDayAmber, label = "Half Day")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        IOSCard(cornerRadius = 20.dp) {
            Text(
                text = "August 2026",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Day headers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val userRecords = allAttendance.filter { it.userId == selectedStaff?.id }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(daysInMonth) { dayNum ->
                    val dayStr = if (dayNum < 10) "0$dayNum" else "$dayNum"
                    val dateKey = "2026-08-$dayStr"
                    val record = userRecords.find { it.date == dateKey }

                    val (bgColor, textColor) = when (record?.status) {
                        "PRESENT" -> StatusPresentBg to StatusPresentGreen
                        "ABSENT" -> StatusAbsentBg to StatusAbsentRed
                        "LATE" -> StatusLateBg to StatusLateOrange
                        "LEAVE" -> StatusLeaveBg to StatusLeaveBlue
                        "HALF_DAY" -> StatusHalfDayBg to StatusHalfDayAmber
                        else -> IOSBackground to IOSTextSecondary
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .clickable {
                                if (record != null) {
                                    selectedDayRecord = record
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$dayNum",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }
                }
            }
        }
    }

    // Day details dialog
    selectedDayRecord?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedDayRecord = null },
            title = { Text("Attendance Details - ${record.date}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Employee: ${record.employeeName} (${record.employeeId})")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Status: ")
                        IOSStatusBadge(status = record.status)
                    }
                    Text("Check-In: ${record.checkInTime ?: "--"}")
                    Text("Check-Out: ${record.checkOutTime ?: "--"}")
                    val hrs = if (record.workingHoursMinutes > 0) "${record.workingHoursMinutes / 60}h ${record.workingHoursMinutes % 60}m" else "--"
                    Text("Working Hours: $hrs")
                    Text("Location: ${record.locationNote}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDayRecord = null }) {
                    Text("Close", color = IOSBlue)
                }
            }
        )
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = IOSTextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
