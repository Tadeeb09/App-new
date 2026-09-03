package com.example.ui.components

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MessageEntity
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite
import com.example.util.FileHelper
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImageMessageView(
    message: MessageEntity,
    onEdit: (imagePath: String) -> Unit,
    onRegenerate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isFullscreenOpen by remember { mutableStateOf(false) }

    val ratioFloat = when (message.aspectRatio) {
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        "4:3" -> 4f / 3f
        else -> 1f
    }

    if (isFullscreenOpen && !message.imageUriOrPath.isNullOrEmpty()) {
        FullscreenImageViewer(
            filePath = message.imageUriOrPath,
            onDismiss = { isFullscreenOpen = false },
            onSaveSuccess = { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EvoroSurface1)
            .border(1.dp, EvoroBorder, RoundedCornerShape(12.dp))
    ) {
        if (message.status == "LOADING") {
            // Loading Shimmer Placeholder
            val transition = rememberInfiniteTransition(label = "shimmer")
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratioFloat)
                    .background(EvoroSurface2.copy(alpha = alpha)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = EvoroWhite,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Creating your image…",
                        color = EvoroWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (message.status == "ERROR") {
            // Error State
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = message.errorMessage ?: "I couldn't create that image. Please try again.",
                    color = EvoroTextSecondary,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EvoroSurface2)
                        .border(1.dp, EvoroBorder, RoundedCornerShape(20.dp))
                        .clickable { onRegenerate() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = EvoroWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Retry",
                        color = EvoroWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else if (!message.imageUriOrPath.isNullOrEmpty()) {
            // Success Image State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratioFloat)
                    .clickable { isFullscreenOpen = true }
            ) {
                AsyncImage(
                    model = File(message.imageUriOrPath),
                    contentDescription = message.imagePrompt ?: "EVORO AI Generated Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Aspect Ratio Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = message.aspectRatio ?: "1:1",
                        color = EvoroTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }

            // Description if any
            if (!message.content.isNullOrBlank() && message.content != "Image generated successfully") {
                Text(
                    text = message.content,
                    color = EvoroTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // Action Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { isFullscreenOpen = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("fullscreen_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = EvoroTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val res = FileHelper.saveImageToMediaStore(context, message.imageUriOrPath)
                                res.fold(
                                    onSuccess = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                                    onFailure = { Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show() }
                                )
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("save_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save to device",
                            tint = EvoroTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            FileHelper.shareImage(context, message.imageUriOrPath)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = EvoroTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onEdit(message.imageUriOrPath) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("edit_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Edit Image",
                            tint = EvoroWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = EvoroTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
