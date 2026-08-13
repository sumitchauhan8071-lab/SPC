package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AuthScreen(
    viewModel: AttendlyViewModel,
    hasAdmin: Boolean
) {
    LoginScreen(viewModel = viewModel, hasAdmin = hasAdmin)
}
