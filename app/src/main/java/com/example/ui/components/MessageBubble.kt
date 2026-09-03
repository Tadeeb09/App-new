package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MessageEntity
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroBorderLight
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite
import org.json.JSONArray
import java.io.File

@Composable
fun MessageBubble(
    message: MessageEntity,
    onEditMessage: (String) -> Unit,
    onRegenerate: () -> Unit,
    onDeleteMessage: () -> Unit,
    onEditImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUser = message.role == "user"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            // User Message
            Column(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                    .background(EvoroSurface2)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                    .padding(12.dp)
            ) {
                // Attachments preview inside user bubble
                if (!message.attachmentsJson.isNullOrEmpty()) {
                    RenderUserAttachments(attachmentsJson = message.attachmentsJson)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (message.content.isNotBlank()) {
                    Text(
                        text = message.content,
                        color = EvoroWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                // Small user action row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("EVORO User Message", message.content))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp).testTag("copy_user_msg")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = EvoroTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onEditMessage(message.content) },
                        modifier = Modifier.size(24.dp).testTag("edit_user_msg")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit message",
                            tint = EvoroTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        } else {
            // AI Assistant Message
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // EVORO Emblem
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(EvoroSurface2)
                        .border(1.dp, EvoroBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        color = EvoroWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                ) {
                    if (message.isImage) {
                        ImageMessageView(
                            message = message,
                            onEdit = onEditImage,
                            onRegenerate = onRegenerate,
                            onDelete = onDeleteMessage
                        )
                    } else if (message.status == "LOADING" && message.content.isBlank()) {
                        // Loading pulse dots
                        PulsingLoadingDots()
                    } else if (message.status == "ERROR") {
                        // Sanitized Error View
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EvoroSurface1, RoundedCornerShape(10.dp))
                                .border(1.dp, EvoroBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = message.errorMessage ?: "Something went wrong. Please try again.",
                                color = EvoroTextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(EvoroSurface2)
                                    .border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
                                    .clickable { onRegenerate() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = EvoroWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Retry",
                                    color = EvoroWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Regular Markdown Content
                        MarkdownText(
                            text = message.content,
                            textColor = EvoroWhite
                        )

                        // If streaming currently, show small indicator
                        if (message.status == "LOADING") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "▋",
                                color = EvoroTextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        // Response Action Bar
                        if (message.status == "SUCCESS") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("EVORO AI Response", message.content))
                                        Toast.makeText(context, "Response copied", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp).testTag("copy_ai_response")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy response",
                                        tint = EvoroTextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onRegenerate,
                                    modifier = Modifier.size(28.dp).testTag("regenerate_response")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = EvoroTextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onDeleteMessage,
                                    modifier = Modifier.size(28.dp).testTag("delete_ai_message")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = EvoroTextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class UserAttachmentItem(val name: String, val isImage: Boolean, val uriStr: String)

@Composable
private fun RenderUserAttachments(attachmentsJson: String) {
    val attachmentList = remember(attachmentsJson) {
        val list = mutableListOf<UserAttachmentItem>()
        try {
            val arr = JSONArray(attachmentsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    UserAttachmentItem(
                        name = obj.optString("name", "File"),
                        isImage = obj.optBoolean("isImage", false),
                        uriStr = obj.optString("uri", "")
                    )
                )
            }
        } catch (_: Exception) {}
        list
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (item in attachmentList) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EvoroSurface1)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isImage && item.uriStr.isNotEmpty()) {
                    AsyncImage(
                        model = item.uriStr,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = item.name,
                        tint = EvoroTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.name,
                    color = EvoroWhite,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PulsingLoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    val alpha1 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), repeatMode = RepeatMode.Reverse),
        label = "a1"
    )
    val alpha2 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse),
        label = "a2"
    )
    val alpha3 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse),
        label = "a3"
    )

    Row(
        modifier = Modifier
            .background(EvoroSurface1, RoundedCornerShape(12.dp))
            .border(1.dp, EvoroBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EvoroWhite.copy(alpha = alpha1)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EvoroWhite.copy(alpha = alpha2)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EvoroWhite.copy(alpha = alpha3)))
    }
}
