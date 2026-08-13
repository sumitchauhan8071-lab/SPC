package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.AdminAttendanceScreen
import com.example.ui.screens.AdminAuditLogsScreen
import com.example.ui.screens.AdminCalendarScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminDepartmentsScreen
import com.example.ui.screens.AdminEmployeesScreen
import com.example.ui.screens.AdminLeaveScreen
import com.example.ui.screens.AdminNotificationsScreen
import com.example.ui.screens.AdminReportsScreen
import com.example.ui.screens.AdminSettingsScreen
import com.example.ui.screens.AdminSetupScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.StaffAttendanceConfirmationScreen
import com.example.ui.screens.StaffDashboardScreen
import com.example.ui.screens.StaffHistoryScreen
import com.example.ui.screens.StaffLeaveScreen
import com.example.ui.screens.StaffNotificationsScreen
import com.example.ui.screens.StaffProfileScreen
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSCardSurface
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AttendlyViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: AttendlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AttendlyMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendlyMainApp(viewModel: AttendlyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allStaff by viewModel.allStaff.collectAsState()
    val hasAdmin = allStaff.any { it.role == "ADMIN" }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Observe toast messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(viewModel.getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }

    if (currentScreen is Screen.Auth) {
        LoginScreen(viewModel = viewModel, hasAdmin = hasAdmin)
        return
    }

    if (currentScreen is Screen.AdminSetup) {
        AdminSetupScreen(viewModel = viewModel)
        return
    }

    // Role check
    val isAdmin = currentUser?.role == "ADMIN"

    val adminNavItems = listOf(
        NavItem(Screen.AdminDashboard, "Dashboard", Icons.Default.Home),
        NavItem(Screen.AdminEmployees, "Employees", Icons.Default.People),
        NavItem(Screen.AdminAttendance, "Attendance", Icons.Default.CheckCircle),
        NavItem(Screen.AdminCalendar, "Calendar", Icons.Default.CalendarMonth),
        NavItem(Screen.AdminLeave, "Leaves", Icons.Default.DateRange),
        NavItem(Screen.AdminReports, "Reports", Icons.Default.Analytics),
        NavItem(Screen.AdminDepartments, "Departments", Icons.Default.Business),
        NavItem(Screen.AdminNotifications, "Notifications", Icons.Default.Notifications),
        NavItem(Screen.AdminAuditLogs, "Audit Logs", Icons.Default.Assignment),
        NavItem(Screen.AdminSettings, "Settings", Icons.Default.Settings)
    )

    val staffBottomItems = listOf(
        NavItem(Screen.StaffHome, "Attendance", Icons.Default.CheckCircle),
        NavItem(Screen.StaffHistory, "History", Icons.Default.History),
        NavItem(Screen.StaffLeave, "Leave", Icons.Default.DateRange),
        NavItem(Screen.StaffNotifications, "Alerts", Icons.Default.Notifications),
        NavItem(Screen.StaffProfile, "Profile", Icons.Default.Person)
    )

    val adminBottomItems = listOf(
        NavItem(Screen.AdminDashboard, "Home", Icons.Default.Home),
        NavItem(Screen.AdminEmployees, "Staff", Icons.Default.People),
        NavItem(Screen.AdminAttendance, "Attendance", Icons.Default.CheckCircle),
        NavItem(Screen.AdminLeave, "Leaves", Icons.Default.DateRange),
        NavItem(Screen.AdminReports, "Reports", Icons.Default.Analytics)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAdmin,
        drawerContent = {
            if (isAdmin) {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    drawerContentColor = IOSTextPrimary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Admin Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(IOSBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.fullName?.take(1) ?: "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser?.fullName ?: "Admin",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = IOSTextPrimary
                                )
                                Text(
                                    text = "Administrator",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IOSBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        adminNavItems.forEach { item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentScreen == item.screen,
                                onClick = {
                                    viewModel.navigateTo(item.screen)
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = IOSBlue.copy(alpha = 0.12f),
                                    selectedIconColor = IOSBlue,
                                    selectedTextColor = IOSBlue,
                                    unselectedIconColor = IOSTextSecondary,
                                    unselectedTextColor = IOSTextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(24.dp))

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Log Out") },
                            label = { Text("Log Out") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.logout()
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedIconColor = Color.Red,
                                unselectedTextColor = Color.Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentScreen.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = IOSTextPrimary
                            )
                            if (currentUser != null) {
                                Text(
                                    text = "${currentUser?.fullName} • ${if (isAdmin) "Admin" else "Staff"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IOSTextSecondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (isAdmin) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Drawer", tint = IOSTextPrimary)
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_btn")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = IOSTextSecondary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = IOSTextPrimary
                    )
                )
            },
            bottomBar = {
                val bottomItems = if (isAdmin) adminBottomItems else staffBottomItems
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = IOSTextSecondary
                ) {
                    bottomItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IOSBlue,
                                selectedTextColor = IOSBlue,
                                indicatorColor = IOSBlue.copy(alpha = 0.12f),
                                unselectedIconColor = IOSTextSecondary,
                                unselectedTextColor = IOSTextSecondary
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(IOSBackground)
            ) {
                when (currentScreen) {
                    // Admin Screens
                    is Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                    is Screen.AdminEmployees -> AdminEmployeesScreen(viewModel = viewModel)
                    is Screen.AdminAttendance -> AdminAttendanceScreen(viewModel = viewModel)
                    is Screen.AdminCalendar -> AdminCalendarScreen(viewModel = viewModel)
                    is Screen.AdminLeave -> AdminLeaveScreen(viewModel = viewModel)
                    is Screen.AdminReports -> AdminReportsScreen(viewModel = viewModel)
                    is Screen.AdminDepartments -> AdminDepartmentsScreen(viewModel = viewModel)
                    is Screen.AdminNotifications -> AdminNotificationsScreen(viewModel = viewModel)
                    is Screen.AdminAuditLogs -> AdminAuditLogsScreen(viewModel = viewModel)
                    is Screen.AdminSettings -> AdminSettingsScreen(viewModel = viewModel)

                    // Staff Screens
                    is Screen.StaffHome -> StaffDashboardScreen(viewModel = viewModel)
                    is Screen.StaffConfirmation -> StaffAttendanceConfirmationScreen(viewModel = viewModel)
                    is Screen.StaffHistory -> StaffHistoryScreen(viewModel = viewModel)
                    is Screen.StaffLeave -> StaffLeaveScreen(viewModel = viewModel)
                    is Screen.StaffNotifications -> StaffNotificationsScreen(viewModel = viewModel)
                    is Screen.StaffProfile -> StaffProfileScreen(viewModel = viewModel)

                    else -> LoginScreen(viewModel = viewModel, hasAdmin = hasAdmin)
                }
            }
        }
    }
}

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)
