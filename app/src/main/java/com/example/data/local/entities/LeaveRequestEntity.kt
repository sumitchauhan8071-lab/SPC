package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val employeeId: String,
    val employeeName: String,
    val departmentName: String,
    val startDate: String, // "YYYY-MM-DD"
    val endDate: String,   // "YYYY-MM-DD"
    val leaveType: String, // "Casual Leave", "Sick Leave", "Paid Leave", "Emergency Leave", "Other"
    val reason: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val adminComment: String = "",
    val requestedAt: Long = System.currentTimeMillis()
)
