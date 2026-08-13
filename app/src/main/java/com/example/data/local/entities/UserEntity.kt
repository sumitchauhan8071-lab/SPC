package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: String, // "ADMIN" or "STAFF"
    val fullName: String,
    val employeeId: String,
    val departmentId: Int? = null,
    val departmentName: String = "",
    val designation: String = "",
    val joiningDate: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePhoto: String = "",
    val workingHours: String = "9:30 AM - 6:00 PM",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
