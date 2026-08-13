package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetUserId: Int? = null, // null means broadcast to role
    val targetRole: String? = null, // "ADMIN" or "STAFF"
    val title: String,
    val message: String,
    val type: String = "INFO", // "INFO", "SUCCESS", "WARNING", "ALERT"
    val isRead: Boolean = false,
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
