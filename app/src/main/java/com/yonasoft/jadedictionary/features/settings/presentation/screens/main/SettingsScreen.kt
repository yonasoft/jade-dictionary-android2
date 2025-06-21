package com.yonasoft.jadedictionary.features.settings.presentation.screens.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.utils.PermissionHelper
import com.yonasoft.jadedictionary.features.settings.presentation.components.dialogs.ReminderNotificationSettingsDialog
import com.yonasoft.jadedictionary.features.settings.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val uiState by settingsViewModel.uiState.collectAsState()
    val notificationSettings by settingsViewModel.settingsReminderState.collectAsState()
    val showNotificationDialog by settingsViewModel.showNotificationDialog.collectAsState()

    // Permission launcher for notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, enable notifications
            settingsViewModel.toggleNotifications(true)
        } else {
            // Permission denied, keep notifications disabled
            settingsViewModel.toggleNotifications(false)
        }
    }

    Scaffold(
        containerColor = CustomColor.DARK01.color,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customize Settings",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CustomColor.DARK02.color
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
//            // Theme Settings
//            SettingsItem(
//                icon = ImageVector.vectorResource(R.drawable.baseline_dark_mode_24),
//                title = "Dark Theme",
//                subtitle = if (uiState.isDarkMode) "Enabled" else "Disabled",
//                showSwitch = true,
//                switchChecked = uiState.isDarkMode,
//                onSwitchChange = { settingsViewModel.toggleTheme() },
//                showChevron = false
//            )

            // Notification Settings with permission handling
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Daily Practice Reminder",
                subtitle = if (notificationSettings.isEnabled) {
                    "Enabled • ${notificationSettings.selectedDays.size} days"
                } else {
                    "Disabled"
                },
                showSwitch = true,
                switchChecked = notificationSettings.isEnabled,
                onSwitchChange = { enabled ->
                    if (enabled) {
                        // Check permission before enabling
                        if (PermissionHelper.hasNotificationPermission(context)) {
                            settingsViewModel.toggleNotifications(true)
                        } else {
                            // Request permission on Android 13+
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                settingsViewModel.toggleNotifications(true)
                            }
                        }
                    } else {
                        settingsViewModel.toggleNotifications(false)
                    }
                },
                showChevron = true,
                onItemClick = { settingsViewModel.openNotificationDialog() }
            )
        }
    }

    // Show notification dialog
    if (showNotificationDialog) {
        ReminderNotificationSettingsDialog(
            settingsReminderState = notificationSettings,
            onDismiss = { settingsViewModel.closeNotificationDialog() },
            onSave = { settingsViewModel.saveNotificationSettings() },
            onTimeChange = { settingsViewModel.updateNotificationTime(it) },
            onDayToggle = { settingsViewModel.toggleDay(it) }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    showChevron: Boolean = false,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { modifier ->
                if (showChevron) {
                    modifier.clickable { onItemClick() }
                } else modifier
            },
        colors = CardDefaults.cardColors(
            containerColor = CustomColor.DARK02.color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        CustomColor.GREEN01.color.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            // Switch or Chevron
            if (showSwitch) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = switchChecked,
                        onCheckedChange = onSwitchChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CustomColor.GREEN01.color,
                            uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                            uncheckedTrackColor = CustomColor.DARK01.color
                        )
                    )

                    if (showChevron) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_chevron_right_24),
                            contentDescription = "Configure",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else if (showChevron) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_chevron_right_24),
                    contentDescription = "Configure",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}