package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Chat with AI",
                description = "Ask questions, write, learn, brainstorm, and code with ultra-fast precision and markdown rendering.",
                icon = Icons.AutoMirrored.Filled.Chat,
                badge = "INTELLIGENCE"
            ),
            OnboardingStep(
                title = "Create Images",
                description = "Generate original high-definition images using natural language prompts and customizable aspect ratios.",
                icon = Icons.Default.Image,
                badge = "CREATION"
            ),
            OnboardingStep(
                title = "Edit Images",
                description = "Upload an image and describe exactly what you want changed. Remove backgrounds, recolor, and refine.",
                icon = Icons.Default.AutoFixHigh,
                badge = "VISION"
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }
    val isLast = currentStep == steps.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Row: Brand & Skip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(EvoroSurface1)
                        .border(1.dp, EvoroBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("E", color = EvoroWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EVORO AI",
                    color = EvoroWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (!isLast) {
                Text(
                    text = "Skip",
                    color = EvoroTextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { onFinishOnboarding() }
                        .padding(8.dp)
                        .testTag("onboarding_skip_button")
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        // Center Content
        val step = steps[currentStep]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Emblem Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(EvoroSurface1)
                    .border(1.5.dp, EvoroBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = EvoroWhite,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(EvoroSurface2)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = step.badge,
                    color = EvoroTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = step.title,
                color = EvoroWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = step.description,
                color = EvoroTextSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Bottom Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in steps.indices) {
                    val active = i == currentStep
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (active) 24.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (active) EvoroWhite else EvoroSurface2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (isLast) {
                        onFinishOnboarding()
                    } else {
                        currentStep++
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EvoroWhite,
                    contentColor = EvoroBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (isLast) "Get Started" else "Continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
