package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.dao.AttendanceDao
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.DepartmentDao
import com.example.data.local.dao.LeaveDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.AttendanceEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DepartmentEntity
import com.example.data.local.entities.LeaveRequestEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrgSettingsEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AttendlyRepository(
    private val userDao: UserDao,
    private val departmentDao: DepartmentDao,
    private val attendanceDao: AttendanceDao,
    private val leaveDao: LeaveDao,
    private val notificationDao: NotificationDao,
    private val settingsDao: SettingsDao,
    private val auditLogDao: AuditLogDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allStaff: Flow<List<UserEntity>> = userDao.getAllStaff()
    val allDepartments: Flow<List<DepartmentEntity>> = departmentDao.getAllDepartments()
    val allAttendance: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()
    val allLeaveRequests: Flow<List<LeaveRequestEntity>> = leaveDao.getAllLeaveRequests()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val orgSettings: Flow<OrgSettingsEntity?> = settingsDao.getSettings()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getAllAuditLogs()

    suspend fun hasAnyAdmin(): Boolean {
        return userDao.getAdminCount() > 0
    }

    suspend fun authenticateUser(username: String, plainPassword: String): UserEntity? {
        val user = userDao.getUserByUsername(username.trim()) ?: return null
        val hash = AppDatabase.hashPassword(plainPassword.trim())
        return if (user.passwordHash == hash && user.isActive) user else null
    }

    suspend fun registerAdmin(fullName: String, username: String, plainPassword: String): UserEntity {
        val user = UserEntity(
            username = username.trim(),
            passwordHash = AppDatabase.hashPassword(plainPassword.trim()),
            role = "ADMIN",
            fullName = fullName.trim(),
            employeeId = "ADM-001",
            departmentName = "Administration",
            designation = "Administrator"
        )
        val id = userDao.insertUser(user)
        return user.copy(id = id.toInt())
    }

    suspend fun createStaffAccount(
        fullName: String,
        employeeId: String,
        username: String,
        plainPassword: String,
        departmentName: String,
        designation: String,
        joiningDate: String,
        workingHours: String = "09:30 AM - 06:00 PM",
        adminName: String
    ): UserEntity {
        val staff = UserEntity(
            username = username.trim(),
            passwordHash = AppDatabase.hashPassword(plainPassword.trim()),
            role = "STAFF",
            fullName = fullName.trim(),
            employeeId = employeeId.trim(),
            departmentName = departmentName,
            designation = designation.trim(),
            joiningDate = joiningDate,
            workingHours = workingHours
        )
        val id = userDao.insertUser(staff)
        recordAudit(
            adminName = adminName,
            action = "Employee Created",
            targetEmployee = fullName,
            previousVal = "",
            newVal = "ID: $employeeId | Dept: $departmentName"
        )
        return staff.copy(id = id.toInt())
    }

    suspend fun updateUserProfile(user: UserEntity, adminName: String? = null) {
        userDao.updateUser(user)
        if (adminName != null) {
            recordAudit(
                adminName = adminName,
                action = "Employee Updated",
                targetEmployee = user.fullName,
                previousVal = "",
                newVal = "Role: ${user.role} | Dept: ${user.departmentName}"
            )
        }
    }

    suspend fun resetUserPassword(userId: Int, newPlainPassword: String, adminName: String) {
        val user = userDao.getUserById(userId) ?: return
        val updated = user.copy(passwordHash = AppDatabase.hashPassword(newPlainPassword.trim()))
        userDao.updateUser(updated)
        recordAudit(
            adminName = adminName,
            action = "Password Reset",
            targetEmployee = user.fullName,
            previousVal = "Protected",
            newVal = "Password Updated"
        )
    }

    suspend fun changePassword(userId: Int, oldPlainPass: String, newPlainPass: String): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        val oldHash = AppDatabase.hashPassword(oldPlainPass.trim())
        if (user.passwordHash != oldHash) return false
        val updated = user.copy(passwordHash = AppDatabase.hashPassword(newPlainPass.trim()))
        userDao.updateUser(updated)
        return true
    }

    suspend fun deleteUser(userId: Int, adminName: String) {
        val user = userDao.getUserById(userId) ?: return
        userDao.deleteUser(userId)
        recordAudit(
            adminName = adminName,
            action = "Employee Deleted",
            targetEmployee = user.fullName,
            previousVal = user.employeeId,
            newVal = "Deleted"
        )
    }

    suspend fun toggleUserActiveStatus(userId: Int, isActive: Boolean, adminName: String) {
        val user = userDao.getUserById(userId) ?: return
        userDao.setUserActiveStatus(userId, isActive)
        recordAudit(
            adminName = adminName,
            action = if (isActive) "Employee Activated" else "Employee Deactivated",
            targetEmployee = user.fullName,
            previousVal = (!isActive).toString(),
            newVal = isActive.toString()
        )
    }

    // Attendance Operations
    suspend fun getTodayAttendanceForUser(userId: Int): AttendanceEntity? {
        val todayStr = getCurrentDateString()
        return attendanceDao.getAttendanceForUserAndDate(userId, todayStr)
    }

    fun getAttendanceHistoryForUser(userId: Int): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceForUser(userId)
    }

    fun getTodayAttendanceList(): Flow<List<AttendanceEntity>> {
        val todayStr = getCurrentDateString()
        return attendanceDao.getAttendanceForDate(todayStr)
    }

    suspend fun markCheckIn(user: UserEntity, locationNote: String = "Office GPS Verified"): AttendanceEntity {
        val todayStr = getCurrentDateString()
        val now = Date()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(now)

        val settings = settingsDao.getSettingsOnce() ?: OrgSettingsEntity()
        // Determine status based on office start time and grace period
        val status = calculateCheckInStatus(now, settings)

        val entity = AttendanceEntity(
            userId = user.id,
            employeeId = user.employeeId,
            employeeName = user.fullName,
            departmentName = user.departmentName,
            date = todayStr,
            checkInTime = timeStr,
            checkInTimestamp = System.currentTimeMillis(),
            status = status,
            locationNote = locationNote
        )
        val id = attendanceDao.insertAttendance(entity)

        // Notification for user
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = user.id,
                targetRole = "STAFF",
                title = "Attendance Marked",
                message = "Your check-in at $timeStr was recorded ($status).",
                type = "SUCCESS",
                date = todayStr
            )
        )
        return entity.copy(id = id.toInt())
    }

    suspend fun markCheckOut(attendanceId: Int): AttendanceEntity? {
        val todayStr = getCurrentDateString()
        val allList = attendanceDao.getAllAttendance()
        // fetch existing record directly
        val existing = attendanceDao.getAttendanceForDate(todayStr) // placeholder, let's find record
        val now = Date()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(now)

        // Find attendance record
        val record = attendanceDao.getAttendanceForUserAndDate(0, todayStr) // we will use id query in DAO if needed
        return null
    }

    suspend fun markCheckOutForUser(userId: Int): AttendanceEntity? {
        val todayStr = getCurrentDateString()
        val record = attendanceDao.getAttendanceForUserAndDate(userId, todayStr) ?: return null
        if (record.checkOutTime != null) return record // already checked out

        val nowMillis = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(nowMillis)

        val inMillis = record.checkInTimestamp ?: nowMillis
        val diffMinutes = ((nowMillis - inMillis) / (1000 * 60)).toInt().coerceAtLeast(0)

        val updated = record.copy(
            checkOutTime = timeStr,
            checkOutTimestamp = nowMillis,
            workingHoursMinutes = diffMinutes,
            updatedAt = System.currentTimeMillis()
        )
        attendanceDao.updateAttendance(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = userId,
                targetRole = "STAFF",
                title = "Check-Out Recorded",
                message = "Checked out at $timeStr. Total time: ${diffMinutes / 60}h ${diffMinutes % 60}m.",
                type = "INFO",
                date = todayStr
            )
        )
        return updated
    }

    suspend fun adminUpdateAttendance(
        attendance: AttendanceEntity,
        newStatus: String,
        newCheckIn: String?,
        newCheckOut: String?,
        adminName: String
    ) {
        val oldStatus = attendance.status
        val updated = attendance.copy(
            status = newStatus,
            checkInTime = newCheckIn,
            checkOutTime = newCheckOut,
            isManualCorrection = true,
            updatedAt = System.currentTimeMillis()
        )
        attendanceDao.updateAttendance(updated)
        recordAudit(
            adminName = adminName,
            action = "Attendance Corrected",
            targetEmployee = attendance.employeeName,
            previousVal = oldStatus,
            newVal = newStatus
        )
    }

    suspend fun adminAddManualAttendance(
        user: UserEntity,
        dateStr: String,
        status: String,
        checkIn: String?,
        checkOut: String?,
        adminName: String
    ) {
        val entity = AttendanceEntity(
            userId = user.id,
            employeeId = user.employeeId,
            employeeName = user.fullName,
            departmentName = user.departmentName,
            date = dateStr,
            checkInTime = checkIn,
            checkOutTime = checkOut,
            status = status,
            isManualCorrection = true
        )
        attendanceDao.insertAttendance(entity)
        recordAudit(
            adminName = adminName,
            action = "Manual Attendance Created",
            targetEmployee = user.fullName,
            previousVal = "Not Marked",
            newVal = "$status on $dateStr"
        )
    }

    // Leave Management
    suspend fun submitLeaveRequest(
        user: UserEntity,
        startDate: String,
        endDate: String,
        leaveType: String,
        reason: String
    ) {
        val req = LeaveRequestEntity(
            userId = user.id,
            employeeId = user.employeeId,
            employeeName = user.fullName,
            departmentName = user.departmentName,
            startDate = startDate,
            endDate = endDate,
            leaveType = leaveType,
            reason = reason,
            status = "PENDING"
        )
        leaveDao.insertLeaveRequest(req)
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "New Leave Request",
                message = "${user.fullName} requested $leaveType ($startDate to $endDate).",
                type = "WARNING",
                date = getCurrentDateString()
            )
        )
    }

    suspend fun processLeaveRequest(
        leaveRequest: LeaveRequestEntity,
        approve: Boolean,
        adminComment: String,
        adminName: String
    ) {
        val newStatus = if (approve) "APPROVED" else "REJECTED"
        val updated = leaveRequest.copy(
            status = newStatus,
            adminComment = adminComment
        )
        leaveDao.updateLeaveRequest(updated)

        // If approved, automatically insert/update attendance record as LEAVE for the dates
        if (approve) {
            val user = userDao.getUserById(leaveRequest.userId)
            val deptName = user?.departmentName ?: leaveRequest.departmentName
            val empName = user?.fullName ?: leaveRequest.employeeName

            attendanceDao.insertAttendance(
                AttendanceEntity(
                    userId = leaveRequest.userId,
                    employeeId = leaveRequest.employeeId,
                    employeeName = empName,
                    departmentName = deptName,
                    date = leaveRequest.startDate,
                    status = "LEAVE",
                    locationNote = "Approved Leave"
                )
            )
        }

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = leaveRequest.userId,
                targetRole = "STAFF",
                title = "Leave Request $newStatus",
                message = "Your leave request for ${leaveRequest.startDate} was $newStatus.",
                type = if (approve) "SUCCESS" else "ALERT",
                date = getCurrentDateString()
            )
        )

        recordAudit(
            adminName = adminName,
            action = "Leave $newStatus",
            targetEmployee = leaveRequest.employeeName,
            previousVal = "PENDING",
            newVal = newStatus
        )
    }

    // Department Management
    suspend fun createDepartment(name: String, description: String, adminName: String) {
        departmentDao.insertDepartment(DepartmentEntity(name = name, description = description))
        recordAudit(adminName, "Department Created", name, "", name)
    }

    suspend fun updateDepartment(dept: DepartmentEntity, adminName: String) {
        departmentDao.updateDepartment(dept)
        recordAudit(adminName, "Department Updated", dept.name, "", dept.description)
    }

    suspend fun deleteDepartment(deptId: Int, deptName: String, adminName: String) {
        departmentDao.deleteDepartment(deptId)
        recordAudit(adminName, "Department Deleted", deptName, deptName, "Deleted")
    }

    // Settings
    suspend fun updateOrgSettings(settings: OrgSettingsEntity, adminName: String) {
        settingsDao.insertOrUpdateSettings(settings)
        recordAudit(adminName, "Org Settings Updated", "System", "Previous Rules", "Updated Rules")
    }

    // Audit Logging
    private suspend fun recordAudit(
        adminName: String,
        action: String,
        targetEmployee: String,
        previousVal: String,
        newVal: String
    ) {
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = action,
                targetEmployeeName = targetEmployee,
                previousValue = previousVal,
                newValue = newVal,
                dateString = getCurrentDateString()
            )
        )
    }

    fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun calculateCheckInStatus(now: Date, settings: OrgSettingsEntity): String {
        try {
            val cal = Calendar.getInstance()
            cal.time = now
            val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

            // Parse office start time e.g. "09:30 AM"
            val parts = settings.officeStartTime.split(" ", ":")
            if (parts.size >= 2) {
                var hour = parts[0].toInt()
                val min = parts[1].toInt()
                val amPm = if (parts.size > 2) parts[2].uppercase() else "AM"
                if (amPm == "PM" && hour < 12) hour += 12
                if (amPm == "AM" && hour == 12) hour = 0

                val startMinutes = hour * 60 + min
                val graceEndMinutes = startMinutes + settings.gracePeriodMinutes

                return if (currentMinutes <= graceEndMinutes) {
                    "PRESENT"
                } else {
                    "LATE"
                }
            }
        } catch (_: Exception) {}
        return "PRESENT"
    }
}
