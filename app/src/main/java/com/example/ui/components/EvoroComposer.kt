package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Attachment
import com.example.data.model.ImageOptions
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun EvoroComposer(
    inputText: String,
    onInputChange: (String) -> Unit,
    attachments: List<Attachment>,
    onRemoveAttachment: (Attachment) -> Unit,
    onAttachClick: () -> Unit,
    onImageOptionsClick: () -> Unit,
    imageOptions: ImageOptions,
    isLoading: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(EvoroSurface0)
            .border(width = 1.dp, color = EvoroBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Attachments Preview Row
        if (attachments.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (att in attachments) {
                    AttachmentChip(
                        attachment = att,
                        onRemove = { onRemoveAttachment(att) }
                    )
                }
            }
        }

        // Active image options indicator if ratio is non-standard
        if (imageOptions.aspectRatio != "1:1") {
            Row(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(EvoroSurface1)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = EvoroTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Image Mode: ${imageOptions.aspectRatio} • ${imageOptions.quality}",
                    color = EvoroTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Input Box & Actions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(EvoroSurface1)
                .border(1.dp, EvoroBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Picker Button
            IconButton(
                onClick = onAttachClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("attachment_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Attach file or image",
                    tint = EvoroTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Image Options Dialog Button
            IconButton(
                onClick = onImageOptionsClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("image_options_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Image Generation Options",
                    tint = if (imageOptions.aspectRatio != "1:1") EvoroWhite else EvoroTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Text Field
            TextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = "Ask EVORO anything…",
                        color = EvoroTextMuted,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .onPreviewKeyEvent { event ->
                        if (event.key == Key.Enter && !event.isShiftPressed) {
                            onSend()
                            true
                        } else {
                            false
                        }
                    }
                    .testTag("composer_text_field"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = EvoroWhite,
                    unfocusedTextColor = EvoroWhite
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )

            // Send Button or Loading Spinner
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank() || attachments.isNotEmpty()) EvoroWhite else EvoroSurface2
                    )
                    .clickable(
                        enabled = !isLoading && (inputText.isNotBlank() || attachments.isNotEmpty()),
                        onClick = onSend
                    )
                    .testTag("send_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = EvoroBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        tint = if (inputText.isNotBlank() || attachments.isNotEmpty()) EvoroBlack else EvoroTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentChip(
    attachment: Attachment,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EvoroSurface2)
            .border(1.dp, EvoroBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (attachment.isImage && attachment.uriString.isNotEmpty()) {
            AsyncImage(
                model = attachment.uriString,
                contentDescription = attachment.fileName,
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                contentDescription = null,
                tint = EvoroTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column {
            Text(
                text = attachment.fileName.take(18) + if (attachment.fileName.length > 18) "…" else "",
                color = EvoroWhite,
                fontSize = 11.sp,
                maxLines = 1
            )
            if (attachment.formattedSize.isNotEmpty()) {
                Text(
                    text = attachment.formattedSize,
                    color = EvoroTextMuted,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(EvoroSurface1)
                .clickable(onClick = onRemove)
                .testTag("remove_attachment_${attachment.id}"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove attachment",
                tint = EvoroTextSecondary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
