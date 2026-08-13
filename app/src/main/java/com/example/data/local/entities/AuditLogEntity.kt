package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val adminName: String,
    val action: String, // e.g. "Attendance Modified", "Employee Added", "Leave Approved"
    val targetEmployeeName: String,
    val previousValue: String = "",
    val newValue: String = "",
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
