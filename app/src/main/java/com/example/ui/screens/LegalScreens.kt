package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
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
                text = "Privacy Policy",
                color = EvoroWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "EVORO AI Privacy Policy",
                color = EvoroWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Last updated: September 2026",
                color = EvoroTextMuted,
                fontSize = 12.sp
            )

            SectionTitle("1. Information We Process")
            SectionBody("EVORO AI processes prompts, text queries, uploaded files, and images you provide solely to generate responses, create images, or edit visual content via official Google Gemini API endpoints.")

            SectionTitle("2. Local Data Storage")
            SectionBody("Your conversations, messages, and locally generated images are stored locally on your device in a secure SQLite database using Android Room architecture. You have complete control to clear your chat history or delete individual records at any time.")

            SectionTitle("3. Third-Party AI Services")
            SectionBody("To provide AI capabilities, user prompts and attachments are transmitted securely over HTTPS to the Gemini API. We do not sell, rent, or trade your personal information with external advertisers or unverified third parties.")

            SectionTitle("4. Media & Storage Access")
            SectionBody("EVORO AI only writes images to your device's photo gallery when you explicitly click 'Save to Gallery'. We adhere strictly to Android storage standards and do not request broad device storage permissions.")

            SectionTitle("5. Security & Deletion")
            SectionBody("You may delete all stored conversations, attachments, and account associations instantly through the Settings menu inside the application.")

            SectionTitle("6. Contact Us")
            SectionBody("If you have questions regarding this Privacy Policy, contact support@evoro.ai.")

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
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
                text = "Terms of Service",
                color = EvoroWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "EVORO AI Terms of Service",
                color = EvoroWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Last updated: September 2026",
                color = EvoroTextMuted,
                fontSize = 12.sp
            )

            SectionTitle("1. Acceptance of Terms")
            SectionBody("By downloading, accessing, or using EVORO AI, you agree to be bound by these Terms of Service. If you disagree with any part of the terms, you may not use the application.")

            SectionTitle("2. Acceptable Use")
            SectionBody("You agree not to use EVORO AI to generate unlawful, defamatory, abusive, or malicious content, or attempt to reverse-engineer or circumvent application limits.")

            SectionTitle("3. AI Output Disclaimer")
            SectionBody("EVORO AI utilizes generative artificial intelligence. While we aim for highest precision, responses should be evaluated critically for professional, legal, or medical decision-making.")

            SectionTitle("4. Intellectual Property")
            SectionBody("Images and text generated through your legitimate use of EVORO AI belong to you, subject to applicable AI model licenses and platform terms.")

            SectionTitle("5. Limitation of Liability")
            SectionBody("EVORO AI is provided 'as is' without warranties of any kind. Under no circumstances will EVORO be liable for indirect or consequential damages resulting from app usage.")

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = EvoroWhite,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SectionBody(text: String) {
    Text(
        text = text,
        color = EvoroTextSecondary,
        fontSize = 13.sp,
        lineHeight = 19.sp
    )
}
