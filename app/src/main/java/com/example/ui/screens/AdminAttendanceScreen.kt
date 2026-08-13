package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.AttendanceEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttendanceScreen(
    viewModel: AttendlyViewModel
) {
    val allAttendance by viewModel.allAttendance.collectAsState()
    val allStaff by viewModel.allStaff.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var statusExpanded by remember { mutableStateOf(false) }

    // Edit modal state
    var editingRecord by remember { mutableStateOf<AttendanceEntity?>(null) }
    var newStatus by remember { mutableStateOf("PRESENT") }
    var newCheckIn by remember { mutableStateOf("") }
    var newCheckOut by remember { mutableStateOf("") }
    var editStatusExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("PRESENT", "ABSENT", "LATE", "HALF_DAY", "LEAVE")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Attendance Management",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )
        Text(
            text = "Review and modify employee attendance records",
            style = MaterialTheme.typography.bodyMedium,
            color = IOSTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IOSTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search Employee",
                placeholder = "Search by name or ID...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.weight(1f),
                testTag = "admin_attendance_search_page_input"
            )

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.weight(0.8f)
            ) {
                OutlinedTextField(
                    value = "Status: $selectedStatus",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth().padding(top = 22.dp)
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    listOf("All", "PRESENT", "ABSENT", "LATE", "HALF_DAY", "LEAVE").forEach { s ->
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

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = allAttendance.filter { item ->
            val matchesSearch = item.employeeName.contains(searchQuery, ignoreCase = true) ||
                    item.employeeId.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == "All" || item.status.equals(selectedStatus, ignoreCase = true)
            matchesSearch && matchesStatus
        }

        if (filtered.isEmpty()) {
            IOSCard {
                Text("No matching attendance records.", style = MaterialTheme.typography.bodyMedium, color = IOSTextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.employeeName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = IOSTextPrimary
                                )
                                Text(
                                    text = "${record.employeeId} • ${record.departmentName} • ${record.date}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IOSTextSecondary
                                )
                                Text(
                                    text = "In: ${record.checkInTime ?: "--"} | Out: ${record.checkOutTime ?: "--"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IOSBlue
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                IOSStatusBadge(status = record.status)
                                IconButton(onClick = {
                                    editingRecord = record
                                    newStatus = record.status
                                    newCheckIn = record.checkInTime ?: "09:30 AM"
                                    newCheckOut = record.checkOutTime ?: "06:00 PM"
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Attendance", tint = IOSBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Attendance Correction Modal
    editingRecord?.let { record ->
        AlertDialog(
            onDismissRequest = { editingRecord = null },
            title = { Text("Correct Attendance: ${record.employeeName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Status", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                    ExposedDropdownMenuBox(
                        expanded = editStatusExpanded,
                        onExpandedChange = { editStatusExpanded = !editStatusExpanded }
                    ) {
                        OutlinedTextField(
                            value = newStatus,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editStatusExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = editStatusExpanded,
                            onDismissRequest = { editStatusExpanded = false }
                        ) {
                            statusOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        newStatus = opt
                                        editStatusExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    IOSTextField(value = newCheckIn, onValueChange = { newCheckIn = it }, label = "Check-In Time")
                    IOSTextField(value = newCheckOut, onValueChange = { newCheckOut = it }, label = "Check-Out Time")
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Save Correction",
                    onClick = {
                        viewModel.adminCorrectAttendance(record, newStatus, newCheckIn, newCheckOut)
                        editingRecord = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { editingRecord = null }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }
}
