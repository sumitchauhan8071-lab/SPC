package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.UserEntity
import com.example.data.repository.AttendlyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val selectedRoleTab: Int = 0, // 0: Staff, 1: Admin
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val fullName: String = "",
    val confirmPassword: String = "",
    val isSetupMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val targetRole: String
        get() = if (selectedRoleTab == 0) "STAFF" else "ADMIN"
}

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: AttendlyRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        AttendlyRepository(
            userDao = db.userDao(),
            departmentDao = db.departmentDao(),
            attendanceDao = db.attendanceDao(),
            leaveDao = db.leaveDao(),
            notificationDao = db.notificationDao(),
            settingsDao = db.settingsDao(),
            auditLogDao = db.auditLogDao()
        )
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun selectRoleTab(tabIndex: Int) {
        _uiState.update {
            it.copy(
                selectedRoleTab = tabIndex,
                errorMessage = null
            )
        }
    }

    fun updateUsername(input: String) {
        _uiState.update { it.copy(username = input, errorMessage = null) }
    }

    fun updatePassword(input: String) {
        _uiState.update { it.copy(password = input, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun updateFullName(input: String) {
        _uiState.update { it.copy(fullName = input, errorMessage = null) }
    }

    fun updateConfirmPassword(input: String) {
        _uiState.update { it.copy(confirmPassword = input, errorMessage = null) }
    }

    fun setSetupMode(isSetup: Boolean) {
        _uiState.update { it.copy(isSetupMode = isSetup, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Executes role-based validation and authenticates user against repository.
     */
    fun login(
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val state = _uiState.value
        val username = state.username.trim()
        val password = state.password.trim()
        val expectedRole = state.targetRole

        if (username.isBlank() || password.isBlank()) {
            val msg = "Please enter both username and password"
            _uiState.update { it.copy(errorMessage = msg) }
            onError(msg)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                var user = repository.authenticateUser(username, password)
                
                // Fallback check for default PINs shown on UI footer (1234 for Admin, 0000 for Staff)
                if (user == null && (password == "1234" || password == "0000")) {
                    user = repository.authenticateUser(username, "password123")
                }

                if (user != null) {
                    // Role-based validation matching the user's selected entry point
                    if (!user.role.equals(expectedRole, ignoreCase = true)) {
                        val accessDeniedMsg = if (expectedRole.equals("ADMIN", ignoreCase = true)) {
                            "Access Denied: Account is a Staff account. Please tap Staff to login."
                        } else {
                            "Access Denied: Account is an Admin account. Please tap Admin to login."
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = accessDeniedMsg
                            )
                        }
                        onError(accessDeniedMsg)
                        return@launch
                    }

                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onSuccess(user)
                } else {
                    val invalidMsg = "Invalid credentials or account inactive"
                    _uiState.update { it.copy(isLoading = false, errorMessage = invalidMsg) }
                    onError(invalidMsg)
                }
            } catch (e: Exception) {
                val err = e.localizedMessage ?: "An error occurred during authentication"
                _uiState.update { it.copy(isLoading = false, errorMessage = err) }
                onError(err)
            }
        }
    }

    /**
     * Register initial Admin account.
     */
    fun setupFirstAdmin(
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val state = _uiState.value
        val fullName = state.fullName.trim()
        val username = state.username.trim()
        val password = state.password.trim()
        val confirmPass = state.confirmPassword.trim()

        if (fullName.isBlank() || username.isBlank() || password.isBlank()) {
            val msg = "Please fill all required fields"
            _uiState.update { it.copy(errorMessage = msg) }
            onError(msg)
            return
        }

        if (password != confirmPass) {
            val msg = "Passwords do not match"
            _uiState.update { it.copy(errorMessage = msg) }
            onError(msg)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val admin = repository.registerAdmin(fullName, username, password)
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                onSuccess(admin)
            } catch (e: Exception) {
                val err = e.localizedMessage ?: "Failed to create administrator account"
                _uiState.update { it.copy(isLoading = false, errorMessage = err) }
                onError(err)
            }
        }
    }
}
