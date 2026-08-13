package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        UserEntity::class,
        DepartmentEntity::class,
        AttendanceEntity::class,
        LeaveRequestEntity::class,
        NotificationEntity::class,
        OrgSettingsEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendly_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDemoData(database)
                    }
                }
            }
        }

        suspend fun seedDemoData(db: AppDatabase) {
            // Seed Org Settings
            if (db.settingsDao().getSettingsOnce() == null) {
                db.settingsDao().insertOrUpdateSettings(OrgSettingsEntity())
            }

            // Seed Departments
            val deptNames = listOf("IT", "HR", "Sales", "Accounts", "Operations", "Production", "Administration")
            for (name in deptNames) {
                db.departmentDao().insertDepartment(DepartmentEntity(name = name, description = "$name Department"))
            }

            // Password default hash for password123
            val defaultPassHash = hashPassword("password123")

            // Seed Admin User
            val adminId = db.userDao().insertUser(
                UserEntity(
                    username = "admin",
                    passwordHash = defaultPassHash,
                    role = "ADMIN",
                    fullName = "John Admin",
                    employeeId = "ADM-001",
                    departmentName = "Administration",
                    designation = "Chief Admin Officer",
                    joiningDate = "2023-01-15",
                    email = "admin@attendly.com",
                    phone = "+1 (555) 019-2831"
                )
            )

            // Seed 10 Staff Employees
            val staffList = listOf(
                UserEntity(
                    username = "rahul.sharma",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Rahul Sharma",
                    employeeId = "EMP-101",
                    departmentName = "IT",
                    designation = "Senior Software Engineer",
                    joiningDate = "2024-02-10",
                    email = "rahul.s@attendly.com",
                    phone = "+1 (555) 234-5678"
                ),
                UserEntity(
                    username = "sarah.connor",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Sarah Connor",
                    employeeId = "EMP-102",
                    departmentName = "HR",
                    designation = "HR Business Partner",
                    joiningDate = "2023-06-01",
                    email = "sarah.c@attendly.com",
                    phone = "+1 (555) 345-6789"
                ),
                UserEntity(
                    username = "john.smith",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "John Smith",
                    employeeId = "EMP-103",
                    departmentName = "Sales",
                    designation = "Account Executive",
                    joiningDate = "2024-01-20",
                    email = "john.s@attendly.com",
                    phone = "+1 (555) 456-7890"
                ),
                UserEntity(
                    username = "priya.patel",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Priya Patel",
                    employeeId = "EMP-104",
                    departmentName = "Accounts",
                    designation = "Financial Analyst",
                    joiningDate = "2023-09-15",
                    email = "priya.p@attendly.com",
                    phone = "+1 (555) 567-8901"
                ),
                UserEntity(
                    username = "alex.wong",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Alex Wong",
                    employeeId = "EMP-105",
                    departmentName = "IT",
                    designation = "DevOps Engineer",
                    joiningDate = "2024-03-01",
                    email = "alex.w@attendly.com",
                    phone = "+1 (555) 678-9012"
                ),
                UserEntity(
                    username = "maria.garcia",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Maria Garcia",
                    employeeId = "EMP-106",
                    departmentName = "Operations",
                    designation = "Operations Manager",
                    joiningDate = "2022-11-01",
                    email = "maria.g@attendly.com",
                    phone = "+1 (555) 789-0123"
                ),
                UserEntity(
                    username = "david.miller",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "David Miller",
                    employeeId = "EMP-107",
                    departmentName = "Production",
                    designation = "Production Lead",
                    joiningDate = "2023-04-12",
                    email = "david.m@attendly.com",
                    phone = "+1 (555) 890-1234"
                ),
                UserEntity(
                    username = "emma.watson",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Emma Watson",
                    employeeId = "EMP-108",
                    departmentName = "Sales",
                    designation = "Sales Manager",
                    joiningDate = "2023-08-22",
                    email = "emma.w@attendly.com",
                    phone = "+1 (555) 901-2345"
                ),
                UserEntity(
                    username = "michael.brown",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Michael Brown",
                    employeeId = "EMP-109",
                    departmentName = "IT",
                    designation = "UI/UX Designer",
                    joiningDate = "2024-05-10",
                    email = "michael.b@attendly.com",
                    phone = "+1 (555) 012-3456"
                ),
                UserEntity(
                    username = "lisa.ray",
                    passwordHash = defaultPassHash,
                    role = "STAFF",
                    fullName = "Lisa Ray",
                    employeeId = "EMP-110",
                    departmentName = "HR",
                    designation = "Talent Specialist",
                    joiningDate = "2024-06-01",
                    email = "lisa.r@attendly.com",
                    phone = "+1 (555) 123-4567"
                )
            )

            val insertedStaffIds = mutableListOf<Long>()
            for (staff in staffList) {
                val id = db.userDao().insertUser(staff)
                insertedStaffIds.add(id)
            }

            // Seed attendance records for past 7 days up to today
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            for (dayOffset in 0..6) {
                val calCopy = calendar.clone() as Calendar
                calCopy.add(Calendar.DAY_OF_YEAR, -dayOffset)
                val dateStr = dateFormat.format(calCopy.time)

                staffList.forEachIndexed { index, staff ->
                    val uId = insertedStaffIds[index].toInt()
                    val status: String
                    val checkIn: String?
                    val checkOut: String?
                    val hoursMinutes: Int

                    when ((index + dayOffset) % 5) {
                        0 -> {
                            status = "PRESENT"
                            checkIn = "09:28 AM"
                            checkOut = if (dayOffset == 0) null else "06:15 PM"
                            hoursMinutes = if (dayOffset == 0) 320 else 527
                        }
                        1 -> {
                            status = "LATE"
                            checkIn = "09:52 AM"
                            checkOut = if (dayOffset == 0) null else "06:10 PM"
                            hoursMinutes = if (dayOffset == 0) 296 else 498
                        }
                        2 -> {
                            status = "HALF_DAY"
                            checkIn = "09:30 AM"
                            checkOut = "01:30 PM"
                            hoursMinutes = 240
                        }
                        3 -> {
                            status = "LEAVE"
                            checkIn = null
                            checkOut = null
                            hoursMinutes = 0
                        }
                        else -> {
                            status = "PRESENT"
                            checkIn = "09:15 AM"
                            checkOut = if (dayOffset == 0) null else "06:00 PM"
                            hoursMinutes = if (dayOffset == 0) 330 else 525
                        }
                    }

                    db.attendanceDao().insertAttendance(
                        AttendanceEntity(
                            userId = uId,
                            employeeId = staff.employeeId,
                            employeeName = staff.fullName,
                            departmentName = staff.departmentName,
                            date = dateStr,
                            checkInTime = checkIn,
                            checkOutTime = checkOut,
                            checkInTimestamp = calCopy.timeInMillis,
                            checkOutTimestamp = if (checkOut != null) calCopy.timeInMillis + 31500000 else null,
                            workingHoursMinutes = hoursMinutes,
                            status = status,
                            locationNote = "Main HQ - Austin Office"
                        )
                    )
                }
            }

            // Seed Leave Requests
            db.leaveDao().insertLeaveRequest(
                LeaveRequestEntity(
                    userId = insertedStaffIds[0].toInt(),
                    employeeId = "EMP-101",
                    employeeName = "Rahul Sharma",
                    departmentName = "IT",
                    startDate = dateFormat.format(Date()),
                    endDate = dateFormat.format(Date()),
                    leaveType = "Casual Leave",
                    reason = "Personal family commitment",
                    status = "PENDING"
                )
            )
            db.leaveDao().insertLeaveRequest(
                LeaveRequestEntity(
                    userId = insertedStaffIds[1].toInt(),
                    employeeId = "EMP-102",
                    employeeName = "Sarah Connor",
                    departmentName = "HR",
                    startDate = "2026-08-10",
                    endDate = "2026-08-11",
                    leaveType = "Sick Leave",
                    reason = "Dental procedure and rest",
                    status = "APPROVED",
                    adminComment = "Approved by John Admin"
                )
            )

            // Seed Notifications
            val todayStr = dateFormat.format(Date())
            db.notificationDao().insertNotification(
                NotificationEntity(
                    targetRole = "ADMIN",
                    title = "New Leave Request",
                    message = "Rahul Sharma requested Casual Leave for today.",
                    type = "WARNING",
                    date = todayStr
                )
            )
            db.notificationDao().insertNotification(
                NotificationEntity(
                    targetUserId = insertedStaffIds[0].toInt(),
                    targetRole = "STAFF",
                    title = "Check-in Confirmed",
                    message = "Your check-in at 09:28 AM was recorded successfully.",
                    type = "SUCCESS",
                    date = todayStr
                )
            )

            // Seed Audit Log
            db.auditLogDao().insertAuditLog(
                AuditLogEntity(
                    adminName = "John Admin",
                    action = "Attendance Corrected",
                    targetEmployeeName = "Sarah Connor",
                    previousValue = "ABSENT",
                    newValue = "LEAVE",
                    dateString = todayStr
                )
            )
        }
    }
}
