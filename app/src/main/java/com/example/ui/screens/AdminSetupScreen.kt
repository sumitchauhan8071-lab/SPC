package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminSetupScreen(viewModel: AttendlyViewModel) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        IOSCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Welcome to Attendly",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )
                Text(
                    text = "No administrator accounts found. Please set up the primary Administrator account to manage your organization.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IOSTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                IOSTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Admin Full Name",
                    placeholder = "e.g. Sarah Jenkins"
                )

                IOSTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Admin Username",
                    placeholder = "e.g. admin"
                )

                IOSTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation()
                )

                IOSTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(12.dp))

                IOSButton(
                    text = "Create Admin Account",
                    onClick = {
                        viewModel.setupFirstAdmin(fullName, username, password, confirmPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_admin_btn")
                )
            }
        }
    }
}
