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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ImageOptions
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroBorderLight
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun ImageOptionsDialog(
    initialOptions: ImageOptions,
    onDismiss: () -> Unit,
    onSave: (ImageOptions) -> Unit
) {
    var selectedRatio by remember { mutableStateOf(initialOptions.aspectRatio) }
    var selectedQuality by remember { mutableStateOf(initialOptions.quality) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EvoroSurface1)
                .border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Image Generation Options",
                color = EvoroWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Aspect Ratio Section
            Text(
                text = "ASPECT RATIO",
                color = EvoroTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1:1", "16:9", "9:16", "4:3").forEach { ratio ->
                    val isSelected = selectedRatio == ratio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EvoroWhite else EvoroSurface2)
                            .border(1.dp, if (isSelected) EvoroWhite else EvoroBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedRatio = ratio }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ratio,
                            color = if (isSelected) EvoroBlack else EvoroWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quality Section
            Text(
                text = "QUALITY",
                color = EvoroTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Standard", "High").forEach { quality ->
                    val isSelected = selectedQuality == quality
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EvoroWhite else EvoroSurface2)
                            .border(1.dp, if (isSelected) EvoroWhite else EvoroBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedQuality = quality }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quality,
                            color = if (isSelected) EvoroBlack else EvoroWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Number of images (1 by default)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EvoroSurface2, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Images generated", color = EvoroTextSecondary, fontSize = 13.sp)
                Text(text = "1 image", color = EvoroWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel", color = EvoroTextSecondary)
                }
                TextButton(
                    onClick = {
                        onSave(ImageOptions(aspectRatio = selectedRatio, quality = selectedQuality, count = 1))
                        onDismiss()
                    },
                    modifier = Modifier.testTag("apply_image_options_button")
                ) {
                    Text(text = "Apply", color = EvoroWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
