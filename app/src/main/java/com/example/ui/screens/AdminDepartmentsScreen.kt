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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.local.entities.DepartmentEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminDepartmentsScreen(
    viewModel: AttendlyViewModel
) {
    val departments by viewModel.allDepartments.collectAsState()
    val allStaff by viewModel.allStaff.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var deptName by remember { mutableStateOf("") }
    var deptDesc by remember { mutableStateOf("") }

    var editingDept by remember { mutableStateOf<DepartmentEntity?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }

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
                    text = "Departments",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = IOSTextPrimary
                )
                Text(
                    text = "${departments.size} Active Departments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )
            }
            IOSButton(
                text = "+ Add Dept",
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_department_btn")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (departments.isEmpty()) {
            IOSCard {
                Text("No departments created yet.", style = MaterialTheme.typography.bodyMedium, color = IOSTextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(departments) { dept ->
                    val staffCount = allStaff.count { it.departmentName.equals(dept.name, ignoreCase = true) }
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
                                        text = dept.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = IOSTextPrimary
                                    )
                                    Text(
                                        text = "$staffCount Assigned Employees",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IOSBlue
                                    )
                                }
                                Row {
                                    IconButton(onClick = {
                                        editingDept = dept
                                        editName = dept.name
                                        editDesc = dept.description
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IOSBlue)
                                    }
                                    IconButton(onClick = {
                                        viewModel.deleteDepartment(dept.id, dept.name)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusAbsentRed)
                                    }
                                }
                            }
                            if (dept.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dept.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IOSTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Department") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    IOSTextField(
                        value = deptName,
                        onValueChange = { deptName = it },
                        label = "Department Name",
                        placeholder = "e.g. Marketing"
                    )
                    IOSTextField(
                        value = deptDesc,
                        onValueChange = { deptDesc = it },
                        label = "Description",
                        placeholder = "Brief department responsibilities..."
                    )
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Save",
                    onClick = {
                        viewModel.addDepartment(deptName, deptDesc)
                        showAddDialog = false
                        deptName = ""
                        deptDesc = ""
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }

    editingDept?.let { dept ->
        AlertDialog(
            onDismissRequest = { editingDept = null },
            title = { Text("Edit Department") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    IOSTextField(value = editName, onValueChange = { editName = it }, label = "Department Name")
                    IOSTextField(value = editDesc, onValueChange = { editDesc = it }, label = "Description")
                }
            },
            confirmButton = {
                IOSButton(
                    text = "Update",
                    onClick = {
                        viewModel.updateDepartment(dept.copy(name = editName, description = editDesc))
                        editingDept = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { editingDept = null }) {
                    Text("Cancel", color = IOSTextSecondary)
                }
            }
        )
    }
}
