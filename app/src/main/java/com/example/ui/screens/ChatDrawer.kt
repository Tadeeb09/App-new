package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite
import java.util.concurrent.TimeUnit

@Composable
fun ChatDrawer(
    conversations: List<ConversationEntity>,
    activeConversationId: String?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    onNewChat: () -> Unit,
    onPinConversation: (id: String, isPinned: Boolean) -> Unit,
    onRenameConversation: (id: String, newTitle: String) -> Unit,
    onDeleteConversation: (id: String) -> Unit,
    onClearAllConversations: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAccount: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var conversationToRename by remember { mutableStateOf<ConversationEntity?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var showClearAllConfirm by remember { mutableStateOf(false) }

    // Rename Dialog
    if (conversationToRename != null) {
        AlertDialog(
            onDismissRequest = { conversationToRename = null },
            title = { Text("Rename Chat", color = EvoroWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = EvoroSurface1,
                        unfocusedContainerColor = EvoroSurface1,
                        focusedTextColor = EvoroWhite,
                        unfocusedTextColor = EvoroWhite,
                        focusedIndicatorColor = EvoroWhite,
                        unfocusedIndicatorColor = EvoroBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("rename_input_field")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        conversationToRename?.let {
                            if (renameInput.isNotBlank()) onRenameConversation(it.id, renameInput.trim())
                        }
                        conversationToRename = null
                    }
                ) {
                    Text("Save", color = EvoroWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { conversationToRename = null }) {
                    Text("Cancel", color = EvoroTextSecondary)
                }
            },
            containerColor = EvoroSurface0,
            modifier = Modifier.border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
        )
    }

    // Clear All Confirmation Dialog
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("Clear All History", color = EvoroWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete all conversations? This action cannot be undone.", color = EvoroTextSecondary, fontSize = 13.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllConversations()
                        showClearAllConfirm = false
                    }
                ) {
                    Text("Clear All", color = EvoroWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("Cancel", color = EvoroTextSecondary)
                }
            },
            containerColor = EvoroSurface0,
            modifier = Modifier.border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(EvoroSurface0)
            .border(width = 1.dp, color = EvoroBorder)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Drawer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(EvoroSurface2)
                        .border(1.dp, EvoroBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("E", color = EvoroWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "EVORO AI",
                    color = EvoroWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            IconButton(onClick = onCloseDrawer) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EvoroTextSecondary)
            }
        }

        // New Chat Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(EvoroWhite)
                .clickable {
                    onNewChat()
                    onCloseDrawer()
                }
                .padding(vertical = 12.dp)
                .testTag("drawer_new_chat_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EvoroBlack, modifier = Modifier.size(18.dp))
                Text("New Chat", color = EvoroBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(EvoroSurface1)
                .border(1.dp, EvoroBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EvoroTextMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search chats…", color = EvoroTextMuted, fontSize = 13.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = EvoroWhite,
                    unfocusedTextColor = EvoroWhite
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().testTag("drawer_search_field")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grouped Conversation List
        val now = System.currentTimeMillis()
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        val sevenDaysAgo = now - TimeUnit.DAYS.toMillis(7)

        val pinnedList = conversations.filter { it.isPinned }
        val unpinned = conversations.filter { !it.isPinned }
        val todayList = unpinned.filter { it.updatedAt >= oneDayAgo }
        val past7DaysList = unpinned.filter { it.updatedAt in sevenDaysAgo until oneDayAgo }
        val olderList = unpinned.filter { it.updatedAt < sevenDaysAgo }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (pinnedList.isNotEmpty()) {
                item { SectionHeader("PINNED") }
                items(pinnedList, key = { it.id }) { conv ->
                    ConversationItem(
                        conversation = conv,
                        isActive = conv.id == activeConversationId,
                        onSelect = {
                            onSelectConversation(conv.id)
                            onCloseDrawer()
                        },
                        onPin = { onPinConversation(conv.id, !conv.isPinned) },
                        onRename = {
                            conversationToRename = conv
                            renameInput = conv.title
                        },
                        onDelete = { onDeleteConversation(conv.id) }
                    )
                }
            }

            if (todayList.isNotEmpty()) {
                item { SectionHeader("TODAY") }
                items(todayList, key = { it.id }) { conv ->
                    ConversationItem(
                        conversation = conv,
                        isActive = conv.id == activeConversationId,
                        onSelect = {
                            onSelectConversation(conv.id)
                            onCloseDrawer()
                        },
                        onPin = { onPinConversation(conv.id, !conv.isPinned) },
                        onRename = {
                            conversationToRename = conv
                            renameInput = conv.title
                        },
                        onDelete = { onDeleteConversation(conv.id) }
                    )
                }
            }

            if (past7DaysList.isNotEmpty()) {
                item { SectionHeader("PREVIOUS 7 DAYS") }
                items(past7DaysList, key = { it.id }) { conv ->
                    ConversationItem(
                        conversation = conv,
                        isActive = conv.id == activeConversationId,
                        onSelect = {
                            onSelectConversation(conv.id)
                            onCloseDrawer()
                        },
                        onPin = { onPinConversation(conv.id, !conv.isPinned) },
                        onRename = {
                            conversationToRename = conv
                            renameInput = conv.title
                        },
                        onDelete = { onDeleteConversation(conv.id) }
                    )
                }
            }

            if (olderList.isNotEmpty()) {
                item { SectionHeader("OLDER") }
                items(olderList, key = { it.id }) { conv ->
                    ConversationItem(
                        conversation = conv,
                        isActive = conv.id == activeConversationId,
                        onSelect = {
                            onSelectConversation(conv.id)
                            onCloseDrawer()
                        },
                        onPin = { onPinConversation(conv.id, !conv.isPinned) },
                        onRename = {
                            conversationToRename = conv
                            renameInput = conv.title
                        },
                        onDelete = { onDeleteConversation(conv.id) }
                    )
                }
            }

            if (conversations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No conversations yet", color = EvoroTextMuted, fontSize = 13.sp)
                    }
                }
            }
        }

        // Bottom Actions Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = EvoroBorder)
                .background(EvoroSurface1)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOpenAccount()
                        onCloseDrawer()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Account", color = EvoroWhite, fontSize = 13.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOpenSettings()
                        onCloseDrawer()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Settings", color = EvoroWhite, fontSize = 13.sp)
            }

            if (conversations.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showClearAllConfirm = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = null, tint = EvoroTextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Clear All Chats", color = EvoroTextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = EvoroTextMuted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 6.dp, top = 14.dp, bottom = 4.dp)
    )
}

@Composable
private fun ConversationItem(
    conversation: ConversationEntity,
    isActive: Boolean,
    onSelect: () -> Unit,
    onPin: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) EvoroSurface2 else Color.Transparent)
            .clickable(onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (conversation.isPinned) Icons.Default.PushPin else Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            tint = if (conversation.isPinned) EvoroWhite else EvoroTextMuted,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = conversation.title,
            color = if (isActive) EvoroWhite else EvoroTextSecondary,
            fontSize = 13.sp,
            maxLines = 1,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Box {
            IconButton(
                onClick = { menuOpen = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = EvoroTextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
                modifier = Modifier.background(EvoroSurface1).border(1.dp, EvoroBorder)
            ) {
                DropdownMenuItem(
                    text = { Text(if (conversation.isPinned) "Unpin" else "Pin to top", color = EvoroWhite, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (conversation.isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin,
                            contentDescription = null,
                            tint = EvoroWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = {
                        onPin()
                        menuOpen = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Rename", color = EvoroWhite, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = EvoroWhite, modifier = Modifier.size(16.dp))
                    },
                    onClick = {
                        onRename()
                        menuOpen = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = EvoroWhite, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = EvoroWhite, modifier = Modifier.size(16.dp))
                    },
                    onClick = {
                        onDelete()
                        menuOpen = false
                    }
                )
            }
        }
    }
}
