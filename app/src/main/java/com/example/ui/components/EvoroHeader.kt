package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun EvoroHeader(
    currentModel: String,
    onSelectModel: (String) -> Unit,
    onMenuClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isOffline: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isModelMenuOpen by remember { mutableStateOf(false) }

    val modelDisplay = when (currentModel) {
        "gemini-3.1-pro-preview" -> "EVORO Pro"
        "gemini-2.5-flash-image" -> "EVORO Vision"
        else -> "EVORO Fast"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .border(width = 1.dp, color = EvoroBorder)
    ) {
        // Offline Notice Banner if offline
        if (isOffline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EvoroSurface2)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Offline Mode • Showing cached chats",
                    color = EvoroTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: History Drawer Button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("menu_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Chat History",
                    tint = EvoroWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Center: App Title + Model Selector
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EvoroSurface1)
                        .border(1.dp, EvoroBorder, RoundedCornerShape(20.dp))
                        .clickable { isModelMenuOpen = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("model_selector_dropdown"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EVORO",
                        color = EvoroWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EvoroSurface2)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = modelDisplay,
                            color = EvoroTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select model",
                        tint = EvoroTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = isModelMenuOpen,
                    onDismissRequest = { isModelMenuOpen = false },
                    modifier = Modifier
                        .background(EvoroSurface1)
                        .border(1.dp, EvoroBorder)
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("EVORO Fast", color = EvoroWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("High-speed conversational AI & coding", color = EvoroTextMuted, fontSize = 11.sp)
                            }
                        },
                        onClick = {
                            onSelectModel("gemini-3.5-flash")
                            isModelMenuOpen = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("EVORO Pro", color = EvoroWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Deep reasoning & advanced analysis", color = EvoroTextMuted, fontSize = 11.sp)
                            }
                        },
                        onClick = {
                            onSelectModel("gemini-3.1-pro-preview")
                            isModelMenuOpen = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("EVORO Vision", color = EvoroWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Specialized image creation & editing", color = EvoroTextMuted, fontSize = 11.sp)
                            }
                        },
                        onClick = {
                            onSelectModel("gemini-2.5-flash-image")
                            isModelMenuOpen = false
                        }
                    )
                }
            }

            // Right: New Chat & Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNewChatClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = EvoroWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("settings_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = EvoroTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
