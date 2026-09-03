package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImageOptions
import com.example.ui.components.ImageOptionsDialog
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun SettingsScreen(
    currentModel: String,
    onSelectModel: (String) -> Unit,
    imageOptions: ImageOptions,
    onUpdateImageOptions: (ImageOptions) -> Unit,
    onClearAllChats: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showImageOptionsDialog by remember { mutableStateOf(false) }
    var showClearChatsDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    if (showImageOptionsDialog) {
        ImageOptionsDialog(
            initialOptions = imageOptions,
            onDismiss = { showImageOptionsDialog = false },
            onSave = {
                onUpdateImageOptions(it)
                showImageOptionsDialog = false
            }
        )
    }

    if (showClearChatsDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatsDialog = false },
            title = { Text("Clear All Chats", color = EvoroWhite, fontWeight = FontWeight.Bold) },
            text = { Text("All conversation histories will be removed from this device permanently.", color = EvoroTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    onClearAllChats()
                    showClearChatsDialog = false
                    Toast.makeText(context, "Chats cleared", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Clear", color = EvoroWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearChatsDialog = false }) {
                    Text("Cancel", color = EvoroTextSecondary)
                }
            },
            containerColor = EvoroSurface0,
            modifier = Modifier.border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = EvoroBorder)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = EvoroWhite
                )
            }
            Text(
                text = "Settings",
                color = EvoroWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Section
            SectionHeader("GENERAL")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = "Account",
                    subtitle = "Profile, credentials & sync",
                    onClick = onOpenAccount
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Palette,
                    title = "Appearance",
                    subtitle = "Pure Monochrome (#000000)",
                    badge = "ACTIVE",
                    onClick = {
                        Toast.makeText(context, "EVORO AI is permanently styled in luxury monochrome", Toast.LENGTH_SHORT).show()
                    }
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "English (Auto-detect prompt)",
                    onClick = {}
                )
                RowDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Notifications", color = EvoroWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Generation alerts", color = EvoroTextMuted, fontSize = 12.sp)
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EvoroWhite,
                            checkedTrackColor = EvoroSurface2,
                            uncheckedThumbColor = EvoroTextMuted,
                            uncheckedTrackColor = EvoroSurface1
                        )
                    )
                }
            }

            // AI Section
            SectionHeader("AI ENGINE")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.Psychology,
                    title = "Active Model",
                    subtitle = when (currentModel) {
                        "gemini-3.1-pro-preview" -> "EVORO Pro (Reasoning)"
                        "gemini-2.5-flash-image" -> "EVORO Vision (Images)"
                        else -> "EVORO Fast (gemini-3.5-flash)"
                    },
                    onClick = {
                        val next = when (currentModel) {
                            "gemini-3.5-flash" -> "gemini-3.1-pro-preview"
                            "gemini-3.1-pro-preview" -> "gemini-2.5-flash-image"
                            else -> "gemini-3.5-flash"
                        }
                        onSelectModel(next)
                    }
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Image,
                    title = "Image Generation Options",
                    subtitle = "${imageOptions.aspectRatio} • ${imageOptions.quality} Quality",
                    onClick = { showImageOptionsDialog = true }
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Key,
                    title = "API Connectivity",
                    subtitle = "Gemini API via AI Studio Secrets",
                    badge = "ACTIVE",
                    onClick = {
                        Toast.makeText(context, "Keys are handled securely via environment variables", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Data Section
            SectionHeader("DATA & STORAGE")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.Storage,
                    title = "Local Database",
                    subtitle = "Room SQLite on private device partition",
                    onClick = {}
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.ClearAll,
                    title = "Clear Chat History",
                    subtitle = "Remove all local sessions",
                    onClick = { showClearChatsDialog = true }
                )
            }

            // About Section
            SectionHeader("ABOUT")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "EVORO AI",
                    subtitle = "Version 1.0.0 (Production Release)",
                    onClick = {}
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "Privacy Policy",
                    subtitle = "Data processing & security details",
                    onClick = onOpenPrivacyPolicy
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "Terms of Service",
                    subtitle = "Usage rules and disclaimers",
                    onClick = onOpenTermsOfService
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "Contact Support",
                    subtitle = "support@evoro.ai",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@evoro.ai")
                            putExtra(Intent.EXTRA_SUBJECT, "EVORO AI Support Inquiry")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Contact: support@evoro.ai", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = EvoroTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EvoroSurface1)
            .border(1.dp, EvoroBorder, RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, color = EvoroWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = EvoroTextMuted, fontSize = 12.sp, maxLines = 1)
            }
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(EvoroSurface2)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = badge, color = EvoroWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = EvoroTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(EvoroBorder)
    )
}
