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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.IOSCard
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun StaffHistoryScreen(
    viewModel: AttendlyViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val historyList by viewModel.repository.getAttendanceHistoryForUser(currentUser?.id ?: 0)
        .collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("This Month") }
    val filters = listOf("All", "This Week", "This Month", "Last Month")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Attendance History",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = IOSTextPrimary
        )
        Text(
            text = "Your personal attendance log",
            style = MaterialTheme.typography.bodyMedium,
            color = IOSTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IOSBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = IOSTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("filter_chip_$filter")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            IOSCard {
                Text(
                    text = "No attendance history available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList) { item ->
                    IOSHistoryRowItem(item = item)
                }
            }
        }
    }
}
