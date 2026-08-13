package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AttendanceEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DepartmentEntity
import com.example.data.local.entities.LeaveRequestEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrgSettingsEntity
import com.example.data.local.entities.UserEntity
import com.example.data.repository.AttendlyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
    object Auth : Screen("Sign In")
    object AdminSetup : Screen("Set Up Organization")

    // Admin Screens
    object AdminDashboard : Screen("Admin Dashboard")
    object AdminEmployees : Screen("Employee Directory")
    object AdminAttendance : Screen("Attendance Management")
    object AdminCalendar : Screen("Monthly Calendar")
    object AdminLeave : Screen("Leave Requests")
    object AdminReports : Screen("Reports & Analytics")
    object AdminDepartments : Screen("Departments")
    object AdminNotifications : Screen("Notifications")
    object AdminAuditLogs : Screen("Audit Logs")
    object AdminSettings : Screen("Organization Settings")

    // Staff Screens
    object StaffHome : Screen("Today's Attendance")
    object StaffConfirmation : Screen("Confirm Attendance")
    object StaffHistory : Screen("Attendance History")
    object StaffLeave : Screen("Leave Requests")
    object StaffNotifications : Screen("Notifications")
    object StaffProfile : Screen("My Profile")
}

class AttendlyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AttendlyRepository(
        userDao = db.userDao(),
        departmentDao = db.departmentDao(),
        attendanceDao = db.attendanceDao(),
        leaveDao = db.leaveDao(),
        notificationDao = db.notificationDao(),
        settingsDao = db.settingsDao(),
        auditLogDao = db.auditLogDao()
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Auth)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _hasAdmin = MutableStateFlow<Boolean?>(null)
    val hasAdmin: StateFlow<Boolean?> = _hasAdmin.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Filters for Admin Attendance
    val attendanceSearchQuery = MutableStateFlow("")
    val attendanceDeptFilter = MutableStateFlow("All")
    val attendanceStatusFilter = MutableStateFlow("All")

    // All Users
    val allStaff: StateFlow<List<UserEntity>> = repository.allStaff.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allDepartments: StateFlow<List<DepartmentEntity>> = repository.allDepartments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAttendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allLeaveRequests: StateFlow<List<LeaveRequestEntity>> = repository.allLeaveRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val orgSettings: StateFlow<OrgSettingsEntity?> = repository.orgSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Staff today's attendance state
    private val _staffTodayAttendance = MutableStateFlow<AttendanceEntity?>(null)
    val staffTodayAttendance: StateFlow<AttendanceEntity?> = _staffTodayAttendance.asStateFlow()

    init {
        checkAdminExists()
    }

    fun checkAdminExists() {
        viewModelScope.launch {
            val exists = repository.hasAnyAdmin()
            _hasAdmin.value = exists
            if (!exists) {
                _currentScreen.value = Screen.AdminSetup
            } else if (_currentUser.value == null) {
                _currentScreen.value = Screen.Auth
            }
        }
    }

    fun navigateTo(screen: Screen) {
        // Enforce role-based safety
        val user = _currentUser.value
        if (user == null && screen !in listOf(Screen.Auth, Screen.AdminSetup)) {
            _currentScreen.value = Screen.Auth
            return
        }
        if (user?.role == "STAFF") {
            // Staff cannot navigate to admin screens
            val isAdminScreen = screen in listOf(
                Screen.AdminDashboard, Screen.AdminEmployees, Screen.AdminAttendance,
                Screen.AdminCalendar, Screen.AdminLeave, Screen.AdminReports,
                Screen.AdminDepartments, Screen.AdminAuditLogs, Screen.AdminSettings
            )
            if (isAdminScreen) {
                showToast("Access Denied: Staff cannot access admin features.")
                _currentScreen.value = Screen.StaffHome
                return
            }
        }
        _currentScreen.value = screen
    }

    fun login(usernameInput: String, passwordInput: String, expectedRole: String? = null) {
        viewModelScope.launch {
            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                showToast("Please enter both username and password")
                return@launch
            }
            val user = repository.authenticateUser(usernameInput, passwordInput)
            if (user != null) {
                if (expectedRole != null && !user.role.equals(expectedRole, ignoreCase = true)) {
                    if (expectedRole.equals("ADMIN", ignoreCase = true)) {
                        showToast("Access Denied: Account is a Staff account. Please use Staff Login.")
                    } else {
                        showToast("Access Denied: Account is an Admin account. Please use Admin Login.")
                    }
                    return@launch
                }
                _currentUser.value = user
                showToast("Welcome back, ${user.fullName}!")
                if (user.role == "ADMIN") {
                    _currentScreen.value = Screen.AdminDashboard
                } else {
                    refreshStaffTodayAttendance()
                    _currentScreen.value = Screen.StaffHome
                }
            } else {
                showToast("Invalid credentials or account inactive")
            }
        }
    }

    fun onUserAuthenticated(user: UserEntity) {
        _currentUser.value = user
        showToast("Welcome back, ${user.fullName}!")
        if (user.role == "ADMIN") {
            _hasAdmin.value = true
            _currentScreen.value = Screen.AdminDashboard
        } else {
            refreshStaffTodayAttendance()
            _currentScreen.value = Screen.StaffHome
        }
    }

    fun setupFirstAdmin(fullName: String, username: String, password: String, confirmPass: String) {
        viewModelScope.launch {
            if (fullName.isBlank() || username.isBlank() || password.isBlank()) {
                showToast("Please fill all fields")
                return@launch
            }
            if (password != confirmPass) {
                showToast("Passwords do not match")
                return@launch
            }
            val admin = repository.registerAdmin(fullName, username, password)
            _currentUser.value = admin
            _hasAdmin.value = true
            showToast("Admin account created successfully!")
            _currentScreen.value = Screen.AdminDashboard
        }
    }

    fun logout() {
        _currentUser.value = null
        _staffTodayAttendance.value = null
        _currentScreen.value = Screen.Auth
        showToast("Logged out successfully")
    }

    fun refreshStaffTodayAttendance() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _staffTodayAttendance.value = repository.getTodayAttendanceForUser(user.id)
        }
    }

    fun markAttendance() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val record = repository.markCheckIn(user)
            _staffTodayAttendance.value = record
            showToast("Attendance marked successfully!")
            _currentScreen.value = Screen.StaffHome
        }
    }

    fun performCheckOut() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = repository.markCheckOutForUser(user.id)
            if (updated != null) {
                _staffTodayAttendance.value = updated
                showToast("Checked out! Working hours recorded.")
            } else {
                showToast("Could not record check-out")
            }
        }
    }

    // Admin Employee Actions
    fun createEmployee(
        fullName: String,
        employeeId: String,
        username: String,
        tempPassword: String,
        departmentName: String,
        designation: String,
        joiningDate: String,
        workingHours: String
    ) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            if (fullName.isBlank() || employeeId.isBlank() || username.isBlank() || tempPassword.isBlank()) {
                showToast("Please fill all required employee fields")
                return@launch
            }
            repository.createStaffAccount(
                fullName = fullName,
                employeeId = employeeId,
                username = username,
                plainPassword = tempPassword,
                departmentName = departmentName,
                designation = designation,
                joiningDate = joiningDate,
                workingHours = workingHours,
                adminName = admin.fullName
            )
            showToast("Employee account created successfully!")
        }
    }

    fun resetEmployeePassword(userId: Int, newPass: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            if (newPass.isBlank()) {
                showToast("Password cannot be blank")
                return@launch
            }
            repository.resetUserPassword(userId, newPass, admin.fullName)
            showToast("Password reset successfully!")
        }
    }

    fun toggleEmployeeActive(userId: Int, isActive: Boolean) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleUserActiveStatus(userId, isActive, admin.fullName)
            showToast("Status updated!")
        }
    }

    fun deleteEmployee(userId: Int) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteUser(userId, admin.fullName)
            showToast("Employee deleted.")
        }
    }

    // Attendance Corrections
    fun adminCorrectAttendance(
        attendance: AttendanceEntity,
        newStatus: String,
        newCheckIn: String?,
        newCheckOut: String?
    ) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.adminUpdateAttendance(attendance, newStatus, newCheckIn, newCheckOut, admin.fullName)
            showToast("Attendance updated!")
        }
    }

    fun adminAddManualAttendance(
        user: UserEntity,
        dateStr: String,
        status: String,
        checkIn: String?,
        checkOut: String?
    ) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.adminAddManualAttendance(user, dateStr, status, checkIn, checkOut, admin.fullName)
            showToast("Manual attendance entry recorded.")
        }
    }

    // Leave Request
    fun submitLeave(startDate: String, endDate: String, leaveType: String, reason: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (startDate.isBlank() || reason.isBlank()) {
                showToast("Please enter leave date and reason")
                return@launch
            }
            repository.submitLeaveRequest(user, startDate, endDate, leaveType, reason)
            showToast("Leave request submitted for admin review.")
            _currentScreen.value = Screen.StaffLeave
        }
    }

    fun processLeave(leaveRequest: LeaveRequestEntity, approve: Boolean, comment: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.processLeaveRequest(leaveRequest, approve, comment, admin.fullName)
            showToast("Leave request ${if (approve) "APPROVED" else "REJECTED"}.")
        }
    }

    // Department CRUD
    fun addDepartment(name: String, description: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            if (name.isBlank()) {
                showToast("Department name required")
                return@launch
            }
            repository.createDepartment(name, description, admin.fullName)
            showToast("Department created.")
        }
    }

    fun updateDepartment(dept: DepartmentEntity) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateDepartment(dept, admin.fullName)
            showToast("Department updated.")
        }
    }

    fun deleteDepartment(deptId: Int, deptName: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteDepartment(deptId, deptName, admin.fullName)
            showToast("Department deleted.")
        }
    }

    // Org Rules
    fun saveOrgSettings(settings: OrgSettingsEntity) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateOrgSettings(settings, admin.fullName)
            showToast("Organization rules updated successfully!")
        }
    }

    // Export Reports to CSV/Text Share
    fun exportReport(context: Context, reportTitle: String, reportContent: String) {
        try {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, reportTitle)
                putExtra(Intent.EXTRA_TEXT, reportContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export $reportTitle")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
            showToast("Report exported!")
        } catch (_: Exception) {
            showToast("Export action opened.")
        }
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }
}
