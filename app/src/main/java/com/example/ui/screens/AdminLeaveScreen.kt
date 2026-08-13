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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.local.entities.LeaveRequestEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSStatusBadge
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminLeaveScreen(
    viewModel: AttendlyViewModel
) {
    val allLeaves by viewModel.allLeaveRequests.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Processed

    var selectedLeave by remember { mutableStateOf<LeaveRequestEntity?>(null) }
    var adminComment by remember { mutableStateOf("") }

    val tabs = listOf("Pending Review", "Processed Leaves")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Leave Requests",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )
        Text(
            text = "Approve or reject employee leave applications",
            style = MaterialTheme.typography.bodyMedium,
            color = IOSTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(title, fontWeight = FontWeight.Bold) },
                    selectedContentColor = IOSBlue,
                    unselectedContentColor = IOSTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = if (selectedTab == 0) {
            allLeaves.filter { it.status == "PENDING" }
        } else {
            allLeaves.filter { it.status != "PENDING" }
        }

        if (filtered.isEmpty()) {
            IOSCard {
                Text(
                    text = if (selectedTab == 0) "No pending leave requests." else "No processed leave history.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { leave ->
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
                                        text = leave.employeeName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = IOSTextPrimary
                                    )
                                    Text(
                                        text = "${leave.employeeId} • ${leave.departmentName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IOSTextSecondary
                                    )
                                }
                                IOSStatusBadge(status = leave.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${leave.leaveType} (${leave.startDate} to ${leave.endDate})",
                                style = MaterialTheme.typography.labelLarge,
                                color = IOSBlue
                            )
                            Text(
                                text = "Reason: ${leave.reason}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IOSTextPrimary
                            )

                            if (leave.status == "PENDING") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IOSButton(
                                        text = "Approve",
                                        onClick = {
                                            selectedLeave = leave
                                        },
                                        containerColor = StatusPresentGreen,
                                        modifier = Modifier.weight(1f),
                                        testTag = "approve_leave_btn"
                                    )
                                    IOSButton(
                                        text = "Reject",
                                        onClick = {
                                            viewModel.processLeave(leave, false, "Rejected by Admin")
                                        },
                                        containerColor = StatusAbsentRed,
                                        modifier = Modifier.weight(1f),
                                        testTag = "reject_leave_btn"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Process leave comment dialog
    selectedLeave?.let { leave ->
        AlertDialog(
            onDismissRequest = { selectedLeave = null },
            title = { Text("Approve Leave for ${leave.employeeName}") },
            text = {
                Column {
                    IOSTextField(
                        value = adminComment,
                        onValueChange = { adminComment = it },
                        label = "Admin Comment (Optional)",
                        placeholder = "e.g. Approved. Please hand over tasks."
                    )
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Confirm Approval",
                    onClick = {
                        viewModel.processLeave(leave, true, adminComment)
                        selectedLeave = null
                        adminComment = ""
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { selectedLeave = null }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }
}
