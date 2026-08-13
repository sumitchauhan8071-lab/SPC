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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IOSBarChart
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSDonutChart
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminReportsScreen(
    viewModel: AttendlyViewModel
) {
    val context = LocalContext.current
    val allStaff by viewModel.allStaff.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()

    val totalRecords = allAttendance.size.coerceAtLeast(1)
    val presentCount = allAttendance.count { it.status == "PRESENT" }
    val absentCount = allAttendance.count { it.status == "ABSENT" }
    val lateCount = allAttendance.count { it.status == "LATE" }
    val leaveCount = allAttendance.count { it.status == "LEAVE" }

    val presentPct = (presentCount.toFloat() / totalRecords) * 100f
    val absentPct = (absentCount.toFloat() / totalRecords) * 100f
    val latePct = (lateCount.toFloat() / totalRecords) * 100f
    val leavePct = (leaveCount.toFloat() / totalRecords) * 100f

    val deptData = listOf(
        "IT" to 88f,
        "HR" to 92f,
        "Sales" to 81f,
        "Accounts" to 95f,
        "Ops" to 85f
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reports & Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = IOSTextPrimary
                    )
                    Text(
                        text = "Monthly organization performance insights",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IOSTextSecondary
                    )
                }
                IOSButton(
                    text = "Export",
                    onClick = {
                        val reportText = buildString {
                            appendLine("ATTENDLY - MONTHLY ATTENDANCE REPORT")
                            appendLine("Generated: ${viewModel.repository.getCurrentDateString()}")
                            appendLine("Total Employees: ${allStaff.size}")
                            appendLine("Present: $presentCount | Absent: $absentCount | Late: $lateCount | Leave: $leaveCount")
                            appendLine("Overall Compliance: %.1f%%".format(presentPct + latePct))
                        }
                        viewModel.exportReport(context, "Attendly_Monthly_Report.csv", reportText)
                    },
                    icon = Icons.Default.Share,
                    modifier = Modifier.testTag("export_report_btn")
                )
            }
        }

        // Distribution Chart
        item {
            IOSCard(cornerRadius = 20.dp) {
                Text(
                    text = "Overall Attendance Distribution",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IOSDonutChart(
                        presentPct = presentPct,
                        absentPct = absentPct,
                        latePct = latePct,
                        leavePct = leavePct
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportMetricLabel(label = "Present", valText = "$presentCount (${presentPct.toInt()}%)", color = IOSBlue)
                        ReportMetricLabel(label = "Late", valText = "$lateCount (${latePct.toInt()}%)", color = Color(0xFFFF9500))
                        ReportMetricLabel(label = "Absent", valText = "$absentCount (${absentPct.toInt()}%)", color = Color(0xFFFF3B30))
                        ReportMetricLabel(label = "Leave", valText = "$leaveCount (${leavePct.toInt()}%)", color = Color(0xFF34C759))
                    }
                }
            }
        }

        // Department Bar Chart
        item {
            IOSCard(cornerRadius = 20.dp) {
                Text(
                    text = "Department Attendance Rate (%)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                IOSBarChart(data = deptData)
            }
        }

        // Employee Summary List
        item {
            Text(
                text = "Staff Monthly Summary",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary
            )
        }

        items(allStaff) { staff ->
            val staffAttendance = allAttendance.filter { it.userId == staff.id }
            val workDays = 26
            val present = staffAttendance.count { it.status == "PRESENT" }
            val late = staffAttendance.count { it.status == "LATE" }
            val absent = staffAttendance.count { it.status == "ABSENT" }
            val leave = staffAttendance.count { it.status == "LEAVE" }
            val pct = ((present + late).toFloat() / workDays * 100).coerceAtMost(100f)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = staff.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = IOSTextPrimary
                            )
                            Text(
                                text = "${staff.employeeId} • ${staff.departmentName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = IOSTextSecondary
                            )
                        }
                        Text(
                            text = "%.1f%%".format(pct),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = IOSBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Working Days: $workDays | Present: $present | Late: $late | Absent: $absent | Leave: $leave",
                        style = MaterialTheme.typography.bodySmall,
                        color = IOSTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ReportMetricLabel(label: String, valText: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("• ", color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("$label: ", style = MaterialTheme.typography.bodySmall, color = IOSTextSecondary)
        Text(valText, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = IOSTextPrimary)
    }
}
