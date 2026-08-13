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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PersonOff
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
import com.example.data.local.entities.UserEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSCardSurface
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.theme.StatusPresentGreen
import com.example.ui.viewmodel.AttendlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmployeesScreen(
    viewModel: AttendlyViewModel
) {
    val staffList by viewModel.allStaff.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }

    // Reset Password State
    var resetTargetUser by remember { mutableStateOf<UserEntity?>(null) }
    var newTempPass by remember { mutableStateOf("") }

    // New Employee Form Fields
    var fullName by remember { mutableStateOf("") }
    var empId by remember { mutableStateOf("EMP-${100 + staffList.size + 1}") }
    var username by remember { mutableStateOf("") }
    var tempPassword by remember { mutableStateOf("password123") }
    var deptName by remember { mutableStateOf("IT") }
    var designation by remember { mutableStateOf("Software Engineer") }
    var joiningDate by remember { mutableStateOf(viewModel.repository.getCurrentDateString()) }
    var deptExpanded by remember { mutableStateOf(false) }

    val departments = listOf("IT", "HR", "Sales", "Accounts", "Operations", "Production", "Administration")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Employees",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = IOSTextPrimary
                )
                Text(
                    text = "${staffList.size} Total Staff Members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
            IOSButton(
                text = "+ Add Staff",
                onClick = { showAddModal = true },
                modifier = Modifier.testTag("add_employee_main_btn")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        IOSTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search Staff",
            placeholder = "Search by name, ID or department...",
            leadingIcon = Icons.Default.Search,
            testTag = "search_staff_input"
        )

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = staffList.filter { emp ->
            emp.fullName.contains(searchQuery, ignoreCase = true) ||
                    emp.employeeId.contains(searchQuery, ignoreCase = true) ||
                    emp.departmentName.contains(searchQuery, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            IOSCard {
                Text(
                    text = "No employees found matching query.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { emp ->
                    EmployeeCardItem(
                        employee = emp,
                        onResetPass = { resetTargetUser = emp },
                        onToggleActive = { viewModel.toggleEmployeeActive(emp.id, !emp.isActive) },
                        onDelete = { viewModel.deleteEmployee(emp.id) }
                    )
                }
            }
        }
    }

    // Add Employee Modal
    if (showAddModal) {
        AlertDialog(
            onDismissRequest = { showAddModal = false },
            title = { Text("Add New Employee", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    IOSTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (username.isBlank()) {
                                username = it.lowercase().replace(" ", ".")
                            }
                        },
                        label = "Full Name",
                        placeholder = "e.g. Rahul Sharma"
                    )
                    IOSTextField(value = empId, onValueChange = { empId = it }, label = "Employee ID")
                    IOSTextField(value = username, onValueChange = { username = it }, label = "Username")
                    IOSTextField(value = tempPassword, onValueChange = { tempPassword = it }, label = "Temporary Password")

                    Text("Department", style = MaterialTheme.typography.labelMedium, color = IOSTextSecondary)
                    ExposedDropdownMenuBox(
                        expanded = deptExpanded,
                        onExpandedChange = { deptExpanded = !deptExpanded }
                    ) {
                        OutlinedTextField(
                            value = deptName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                            shape = RoundedCornerShape(12.dp),
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
                                        deptName = d
                                        deptExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    IOSTextField(value = designation, onValueChange = { designation = it }, label = "Designation")
                    IOSTextField(value = joiningDate, onValueChange = { joiningDate = it }, label = "Joining Date (YYYY-MM-DD)")
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Create Account",
                    onClick = {
                        viewModel.createEmployee(
                            fullName = fullName,
                            employeeId = empId,
                            username = username,
                            tempPassword = tempPassword,
                            departmentName = deptName,
                            designation = designation,
                            joiningDate = joiningDate,
                            workingHours = "09:30 AM - 06:00 PM"
                        )
                        showAddModal = false
                        fullName = ""
                        username = ""
                    },
                    testTag = "save_employee_btn"
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddModal = false }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }

    // Reset Password Modal
    resetTargetUser?.let { target ->
        AlertDialog(
            onDismissRequest = { resetTargetUser = null },
            title = { Text("Reset Password for ${target.fullName}") },
            text = {
                Column {
                    IOSTextField(
                        value = newTempPass,
                        onValueChange = { newTempPass = it },
                        label = "New Password",
                        placeholder = "e.g. password123"
                    )
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Reset Password",
                    onClick = {
                        viewModel.resetEmployeePassword(target.id, newTempPass)
                        resetTargetUser = null
                        newTempPass = ""
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { resetTargetUser = null }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }
}

@Composable
fun EmployeeCardItem(
    employee: UserEntity,
    onResetPass: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
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
                        text = employee.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IOSTextPrimary
                    )
                    Text(
                        text = "${employee.employeeId} • ${employee.departmentName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IOSTextSecondary
                    )
                }
                Text(
                    text = if (employee.isActive) "ACTIVE" else "INACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (employee.isActive) StatusPresentGreen else StatusAbsentRed
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Designation: ${employee.designation} | Username: ${employee.username}",
                style = MaterialTheme.typography.bodySmall,
                color = IOSTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onResetPass) {
                    Icon(Icons.Default.LockReset, contentDescription = "Reset Password", tint = IOSBlue)
                }
                IconButton(onClick = onToggleActive) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = "Toggle Active",
                        tint = if (employee.isActive) StatusAbsentRed else StatusPresentGreen
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Employee", tint = StatusAbsentRed)
                }
            }
        }
    }
}
