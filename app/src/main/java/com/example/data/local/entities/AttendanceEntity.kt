package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val employeeId: String,
    val employeeName: String,
    val departmentName: String,
    val date: String, // "YYYY-MM-DD"
    val checkInTime: String? = null, // "09:32 AM"
    val checkOutTime: String? = null, // "06:12 PM"
    val checkInTimestamp: Long? = null,
    val checkOutTimestamp: Long? = null,
    val workingHoursMinutes: Int = 0, // calculated minutes
    val status: String, // "PRESENT", "ABSENT", "LATE", "HALF_DAY", "LEAVE", "NOT_MARKED"
    val locationNote: String = "Office Location",
    val isManualCorrection: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
