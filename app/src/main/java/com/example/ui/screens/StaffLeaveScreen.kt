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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.LeaveRequestEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSCardSurface
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffLeaveScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allLeaves by viewModel.allLeaveRequests.collectAsState()
    val userLeaves = allLeaves.filter { it.userId == currentUser?.id }

    var startDate by remember { mutableStateOf(viewModel.repository.getCurrentDateString()) }
    var endDate by remember { mutableStateOf(viewModel.repository.getCurrentDateString()) }
    var leaveType by remember { mutableStateOf("Casual Leave") }
    var reason by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }

    val leaveTypes = listOf("Casual Leave", "Sick Leave", "Paid Leave", "Emergency Leave", "Other")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Apply for Leave",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = IOSTextPrimary
            )
            Text(
                text = "Submit a formal leave request to administration",
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextSecondary
            )
        }

        item {
            IOSCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IOSTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = "Start Date",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f),
                        testTag = "leave_start_date_input"
                    )
                    IOSTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = "End Date",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f),
                        testTag = "leave_end_date_input"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Leave Type", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedMenu,
                    onExpandedChange = { expandedMenu = !expandedMenu }
                ) {
                    OutlinedTextField(
                        value = leaveType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = IOSCardSurface,
                            unfocusedContainerColor = IOSBackground
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("leave_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        leaveTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    leaveType = type
                                    expandedMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                IOSTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = "Reason",
                    placeholder = "Describe reason for leave...",
                    singleLine = false,
                    testTag = "leave_reason_input"
                )

                Spacer(modifier = Modifier.height(20.dp))

                IOSButton(
                    text = "Submit Leave Request",
                    onClick = {
                        viewModel.submitLeave(startDate, endDate, leaveType, reason)
                        reason = ""
                    },
                    testTag = "submit_leave_btn"
                )
            }
        }

        item {
            Text(
                text = "My Leave History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary
            )
        }

        if (userLeaves.isEmpty()) {
            item {
                IOSCard {
                    Text("No leave requests submitted yet.", style = MaterialTheme.typography.bodyMedium, color = IOSTextSecondary)
                }
            }
        } else {
            items(userLeaves) { leave ->
                LeaveRequestCard(leave)
            }
        }
    }
}

@Composable
fun LeaveRequestCard(leave: LeaveRequestEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
                        text = leave.leaveType,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextPrimary
                    )
                    Text(
                        text = "${leave.startDate} to ${leave.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IOSTextSecondary
                    )
                }
                IOSStatusBadge(status = leave.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reason: ${leave.reason}",
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextPrimary
            )
            if (leave.adminComment.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Admin Note: ${leave.adminComment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IOSBlue
                )
            }
        }
    }
}
