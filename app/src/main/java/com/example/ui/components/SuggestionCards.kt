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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun SuggestionCards(
    onSelectSuggestion: (prompt: String) -> Unit,
    onOpenFilePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // EVORO Monogram
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EvoroSurface1)
                .border(1.dp, EvoroBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "E",
                color = EvoroWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EVORO AI",
            color = EvoroWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "How can I help?",
            color = EvoroWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Ask EVORO anything.",
            color = EvoroTextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Clean Suggestion Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SuggestionItem(
                icon = Icons.Default.Lightbulb,
                title = "Explain something",
                subtitle = "Concepts, research, or simple summaries",
                onClick = { onSelectSuggestion("Explain quantum computing in simple terms with analogies.") },
                testTag = "suggestion_explain"
            )

            SuggestionItem(
                icon = Icons.Default.Code,
                title = "Write code",
                subtitle = "Algorithms, debugging, and architectures",
                onClick = { onSelectSuggestion("Write a clean Kotlin Jetpack Compose state hoist example.") },
                testTag = "suggestion_code"
            )

            SuggestionItem(
                icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                title = "Analyze a file",
                subtitle = "Upload documents, code files, or data",
                onClick = onOpenFilePicker,
                testTag = "suggestion_file"
            )

            SuggestionItem(
                icon = Icons.Default.Image,
                title = "Create an image",
                subtitle = "Generate original visual art from natural prompts",
                onClick = { onSelectSuggestion("Create a professional minimalist gaming logo with letter E on pure black background") },
                testTag = "suggestion_image"
            )

            SuggestionItem(
                icon = Icons.Default.AutoFixHigh,
                title = "Edit an image",
                subtitle = "Upload an image and describe modifications",
                onClick = onOpenFilePicker,
                testTag = "suggestion_edit_image"
            )
        }
    }
}

@Composable
private fun SuggestionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EvoroSurface1)
            .border(1.dp, EvoroBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(EvoroSurface2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EvoroWhite,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = EvoroWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = EvoroTextMuted,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
