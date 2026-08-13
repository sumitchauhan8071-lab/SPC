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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.IOSCard
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminAuditLogsScreen(viewModel: AttendlyViewModel) {
    val auditLogs by viewModel.auditLogs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Audit Logs",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )
        Text(
            text = "Track administrative & system activities",
            style = MaterialTheme.typography.bodyMedium,
            color = IOSTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (auditLogs.isEmpty()) {
            IOSCard {
                Text("No audit log records found.", style = MaterialTheme.typography.bodyMedium, color = IOSTextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(auditLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = log.action,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = IOSBlue
                                )
                                Text(
                                    text = log.dateString,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IOSTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Target: ${log.targetEmployeeName} | Prev: ${log.previousValue} -> New: ${log.newValue}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IOSTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Admin: ${log.adminName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = IOSTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
