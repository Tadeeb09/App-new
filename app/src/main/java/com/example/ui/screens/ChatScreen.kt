package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.components.EvoroComposer
import com.example.ui.components.EvoroHeader
import com.example.ui.components.ImageOptionsDialog
import com.example.ui.components.MessageBubble
import com.example.ui.components.SuggestionCards
import com.example.ui.theme.EvoroSurface0
import com.example.ui.viewmodel.ChatViewModel
import com.example.util.FileHelper
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenSettings: () -> Unit,
    onOpenAccount: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val activeConvId by viewModel.activeConversationId.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val attachments by viewModel.attachments.collectAsState()
    val imageOptions by viewModel.imageOptions.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    var showImageOptionsDialog by remember { mutableStateOf(false) }

    // Visual media picker (Photos/Images)
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val att = FileHelper.processUri(context, uri)
                viewModel.addAttachment(att)
            }
        }
    }

    // Document / File Picker
    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val att = FileHelper.processUri(context, uri)
                viewModel.addAttachment(att)
            }
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showImageOptionsDialog) {
        ImageOptionsDialog(
            initialOptions = imageOptions,
            onDismiss = { showImageOptionsDialog = false },
            onSave = {
                viewModel.setImageOptions(it)
                showImageOptionsDialog = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawer(
                conversations = conversations,
                activeConversationId = activeConvId,
                searchQuery = searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onSelectConversation = { id ->
                    viewModel.selectConversation(id)
                },
                onNewChat = {
                    viewModel.startNewChat()
                },
                onPinConversation = { id, isPinned ->
                    viewModel.pinConversation(id, isPinned)
                },
                onRenameConversation = { id, title ->
                    viewModel.renameConversation(id, title)
                },
                onDeleteConversation = { id ->
                    viewModel.deleteConversation(id)
                },
                onClearAllConversations = {
                    viewModel.clearAllConversations()
                },
                onOpenSettings = onOpenSettings,
                onOpenAccount = onOpenAccount,
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EvoroSurface0)
        ) {
            // Header
            EvoroHeader(
                currentModel = selectedModel,
                onSelectModel = { viewModel.setSelectedModel(it) },
                onMenuClick = {
                    coroutineScope.launch { drawerState.open() }
                },
                onNewChatClick = {
                    viewModel.startNewChat()
                },
                onSettingsClick = onOpenSettings,
                isOffline = !isOnline
            )

            // Main Message Content or Main Home Suggestions
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SuggestionCards(
                            onSelectSuggestion = { prompt ->
                                viewModel.setInputText(prompt)
                                viewModel.sendMessage()
                            },
                            onOpenFilePicker = {
                                docPicker.launch(arrayOf("*/*"))
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        items(messages, key = { it.id }) { message ->
                            MessageBubble(
                                message = message,
                                onEditMessage = { text ->
                                    viewModel.setInputText(text)
                                },
                                onRegenerate = {
                                    viewModel.regenerateMessage(message)
                                },
                                onDeleteMessage = {
                                    viewModel.deleteMessage(message.id)
                                },
                                onEditImage = { path ->
                                    viewModel.setInputText("Edit this image: ")
                                    // Process image file and add as attachment
                                    coroutineScope.launch {
                                        val uri = Uri.fromFile(java.io.File(path))
                                        val att = FileHelper.processUri(context, uri)
                                        viewModel.addAttachment(att)
                                    }
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }

            // Bottom Composer
            EvoroComposer(
                inputText = inputText,
                onInputChange = { viewModel.setInputText(it) },
                attachments = attachments,
                onRemoveAttachment = { viewModel.removeAttachment(it) },
                onAttachClick = {
                    // Open visual media picker or general file picker
                    try {
                        mediaPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } catch (_: Exception) {
                        docPicker.launch(arrayOf("image/*", "application/pdf", "text/*"))
                    }
                },
                onImageOptionsClick = {
                    showImageOptionsDialog = true
                },
                imageOptions = imageOptions,
                isLoading = isGenerating,
                onSend = {
                    viewModel.sendMessage()
                }
            )
        }
    }
}
