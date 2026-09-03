package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroBorder
import com.example.ui.theme.EvoroBorderLight
import com.example.ui.theme.EvoroSurface0
import com.example.ui.theme.EvoroSurface1
import com.example.ui.theme.EvoroSurface2
import com.example.ui.theme.EvoroTextMuted
import com.example.ui.theme.EvoroTextSecondary
import com.example.ui.theme.EvoroWhite

@Composable
fun AccountScreen(
    currentAccount: UserAccount,
    onLogin: (email: String, name: String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account", color = EvoroWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Deleting your account will erase your preferences, conversations, and access keys permanently. This action is irreversible.",
                    color = EvoroTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAccount()
                        showDeleteConfirm = false
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete Forever", color = EvoroWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = EvoroTextSecondary)
                }
            },
            containerColor = EvoroSurface0,
            modifier = Modifier.border(1.dp, EvoroBorder, RoundedCornerShape(16.dp))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvoroSurface0)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
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
                text = "Account",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(EvoroSurface1)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(EvoroSurface2)
                        .border(1.dp, EvoroBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentAccount.isGuest) "G" else currentAccount.name.take(1).uppercase(),
                        color = EvoroWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (currentAccount.isGuest) "Guest User" else currentAccount.name,
                        color = EvoroWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (currentAccount.isGuest) "Local Device Session" else (currentAccount.email ?: "Signed in"),
                        color = EvoroTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            if (currentAccount.isGuest) {
                // Sign In Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EvoroSurface1)
                        .border(1.dp, EvoroBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sign in to EVORO",
                        color = EvoroWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Sync your preferences and access personalized AI capabilities across devices.",
                        color = EvoroTextMuted,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("Email address", color = EvoroTextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(18.dp))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EvoroSurface2,
                            unfocusedContainerColor = EvoroSurface2,
                            focusedTextColor = EvoroWhite,
                            unfocusedTextColor = EvoroWhite,
                            focusedIndicatorColor = EvoroWhite,
                            unfocusedIndicatorColor = EvoroBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("account_email_input")
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = { Text("Password", color = EvoroTextMuted, fontSize = 13.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EvoroTextSecondary, modifier = Modifier.size(18.dp))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EvoroSurface2,
                            unfocusedContainerColor = EvoroSurface2,
                            focusedTextColor = EvoroWhite,
                            unfocusedTextColor = EvoroWhite,
                            focusedIndicatorColor = EvoroWhite,
                            unfocusedIndicatorColor = EvoroBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("account_password_input")
                    )

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                val name = emailInput.substringBefore("@").replaceFirstChar { it.uppercase() }
                                onLogin(emailInput.trim(), name)
                                Toast.makeText(context, "Welcome, $name", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoroWhite,
                            contentColor = EvoroBlack
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("account_login_button")
                    ) {
                        Text("Sign In with Email", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Google Sign-in alternative
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EvoroSurface2)
                            .border(1.dp, EvoroBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                onLogin("user@google.com", "Google User")
                                Toast.makeText(context, "Signed in with Google", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue with Google",
                            color = EvoroWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Logged In Options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EvoroSurface1)
                        .border(1.dp, EvoroBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Account Details",
                        color = EvoroWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status", color = EvoroTextSecondary, fontSize = 13.sp)
                        Text("Active Member", color = EvoroWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Security", color = EvoroTextSecondary, fontSize = 13.sp)
                        Text("End-to-End Secure", color = EvoroWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onLogout()
                            Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoroSurface2,
                            contentColor = EvoroWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", fontSize = 13.sp)
                    }
                }
            }

            // Danger Zone
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(EvoroSurface1)
                    .border(1.dp, EvoroBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Danger Zone",
                    color = EvoroWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Permanently purge your account, conversations, and local storage.",
                    color = EvoroTextMuted,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EvoroSurface2)
                        .border(1.dp, EvoroBorder, RoundedCornerShape(8.dp))
                        .clickable { showDeleteConfirm = true }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = EvoroWhite, modifier = Modifier.size(16.dp))
                        Text("Delete Account Permanently", color = EvoroWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
