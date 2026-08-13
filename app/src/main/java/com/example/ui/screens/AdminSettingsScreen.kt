package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.OrgSettingsEntity
import com.example.ui.components.IOSButton
import com.example.ui.components.IOSCard
import com.example.ui.components.IOSTextField
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.viewmodel.AttendlyViewModel

@Composable
fun AdminSettingsScreen(viewModel: AttendlyViewModel) {
    val orgSettingsState by viewModel.orgSettings.collectAsState()

    var companyName by remember { mutableStateOf("Attendly Corp") }
    var officeStartTime by remember { mutableStateOf("09:00 AM") }
    var officeEndTime by remember { mutableStateOf("05:00 PM") }
    var gracePeriodMins by remember { mutableStateOf("15") }
    var halfDayMins by remember { mutableStateOf("240") }
    var companyAddress by remember { mutableStateOf("100 Tech Park, Suite 500") }
    var contactEmail by remember { mutableStateOf("hr@attendly.com") }

    LaunchedEffect(orgSettingsState) {
        orgSettingsState?.let { settings ->
            companyName = settings.companyName
            officeStartTime = settings.officeStartTime
            officeEndTime = settings.officeEndTime
            gracePeriodMins = settings.gracePeriodMinutes.toString()
            halfDayMins = settings.halfDayThresholdMinutes.toString()
            companyAddress = settings.companyAddress
            contactEmail = settings.contactEmail
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IOSBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Organization Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = IOSTextPrimary
            )
            Text(
                text = "Configure work schedules & organization profile",
                style = MaterialTheme.typography.bodyMedium,
                color = IOSTextSecondary
            )
        }

        IOSCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Company Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )

                IOSTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = "Company Name",
                    placeholder = "e.g. Acme Corp"
                )

                IOSTextField(
                    value = companyAddress,
                    onValueChange = { companyAddress = it },
                    label = "Office Address",
                    placeholder = "100 Corporate Plaza"
                )

                IOSTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = "Contact HR Email",
                    placeholder = "hr@company.com"
                )
            }
        }

        IOSCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Work Hours & Thresholds",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = IOSTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        IOSTextField(
                            value = officeStartTime,
                            onValueChange = { officeStartTime = it },
                            label = "Office Start Time",
                            placeholder = "09:00 AM"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        IOSTextField(
                            value = officeEndTime,
                            onValueChange = { officeEndTime = it },
                            label = "Office End Time",
                            placeholder = "05:00 PM"
                        )
                    }
                }

                IOSTextField(
                    value = gracePeriodMins,
                    onValueChange = { gracePeriodMins = it },
                    label = "Grace Period (Minutes)",
                    placeholder = "15"
                )

                IOSTextField(
                    value = halfDayMins,
                    onValueChange = { halfDayMins = it },
                    label = "Half-Day Threshold (Minutes)",
                    placeholder = "240"
                )
            }
        }

        IOSButton(
            text = "Save Settings",
            onClick = {
                val grace = gracePeriodMins.toIntOrNull() ?: 15
                val halfDay = halfDayMins.toIntOrNull() ?: 240
                val updated = OrgSettingsEntity(
                    id = orgSettingsState?.id ?: 1,
                    companyName = companyName,
                    companyAddress = companyAddress,
                    contactEmail = contactEmail,
                    officeStartTime = officeStartTime,
                    officeEndTime = officeEndTime,
                    gracePeriodMinutes = grace,
                    halfDayThresholdMinutes = halfDay
                )
                viewModel.saveOrgSettings(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_settings_btn")
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
