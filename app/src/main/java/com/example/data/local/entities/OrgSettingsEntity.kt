package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "org_settings")
data class OrgSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "Attendly Inc.",
    val companyAddress: String = "100 Corporate Plaza, Suite 400",
    val contactEmail: String = "admin@attendly.com",
    val contactPhone: String = "+1 (800) 555-0199",
    val officeStartTime: String = "09:30 AM",
    val officeEndTime: String = "06:00 PM",
    val gracePeriodMinutes: Int = 15,
    val lateThresholdMinutes: Int = 15,
    val minWorkingHoursMinutes: Int = 240, // 4 hrs
    val halfDayThresholdMinutes: Int = 240  // 4 hrs
)
