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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.AuthManager
import com.example.data.firebase.AuthState
import com.example.ui.components.ArcadeButton
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    authManager: AuthManager,
    authState: AuthState,
    onAuthSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.SIGN_UP) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var usernameStatus by remember { mutableStateOf<String?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    var isCheckingUsername by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        val trimmed = username.trim().lowercase()
        if (mode == AuthMode.SIGN_UP && trimmed.length >= 3) {
            isCheckingUsername = true
            val available = authManager.isUsernameAvailable(trimmed)
            isCheckingUsername = false
            usernameStatus = if (available) "✓ Username available" else "✗ Already taken"
        } else {
            usernameStatus = null
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        // Top Bar Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .testTag("auth_btn_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFF0F0F5)
                )
            }

            Text(
                text = "FIREBASE AUTHENTICATION",
                color = Color(0xFF8A8D98),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // Horizontal Landscape Split View
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Panel: Branding & Security Badge
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Column {
                    Text(
                        text = "MATHS WAR",
                        color = Color(0xFFF0F0F5),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PUBLIC WARRIOR IDENTITY",
                        color = Color(0xFFE67E22),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Create your unique username to compete on the Global Top 300 Leaderboard, synchronize combat scores, and unlock competitive rank progression.",
                        color = Color(0xFFB0B3BC),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x20374151))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security",
                            tint = Color(0xFF27AE60),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURE CLOUD SYNC",
                            color = Color(0xFF27AE60),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Passwords are never stored directly. Profile access is strictly secured via Firebase Security Rules.",
                        color = Color(0xFF8A8D98),
                        fontSize = 9.sp,
                        lineHeight = 13.sp
                    )
                }

                // Guest / Offline Play button
                ArcadeButton(
                    text = "CONTINUE AS GUEST",
                    onClick = {
                        authManager.playAsGuest(username.ifEmpty { "Warrior" })
                        onAuthSuccess()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = Color(0xFF2A2D37),
                    testTag = "btn_guest_play"
                )
            }

            // Right Panel: Form Fields & Mode Switcher
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mode Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D0E12))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (mode == AuthMode.SIGN_UP) Color(0xFFE67E22) else Color.Transparent)
                            .clickable { mode = AuthMode.SIGN_UP; localError = null }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SIGN UP",
                            color = if (mode == AuthMode.SIGN_UP) Color(0xFF0D0E12) else Color(0xFF8A8D98),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (mode == AuthMode.LOGIN) Color(0xFFE67E22) else Color.Transparent)
                            .clickable { mode = AuthMode.LOGIN; localError = null }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOGIN",
                            color = if (mode == AuthMode.LOGIN) Color(0xFF0D0E12) else Color(0xFF8A8D98),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (mode == AuthMode.FORGOT_PASSWORD) Color(0xFFE67E22) else Color.Transparent)
                            .clickable { mode = AuthMode.FORGOT_PASSWORD; localError = null }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RESET",
                            color = if (mode == AuthMode.FORGOT_PASSWORD) Color(0xFF0D0E12) else Color(0xFF8A8D98),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Error message banner
                val displayError = localError ?: (authState as? AuthState.Error)?.message
                if (displayError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x30C0392B))
                            .border(1.dp, Color(0xFFC0392B), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = displayError,
                            color = Color(0xFFFF6B6B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Unique Username Field (for Sign Up)
                if (mode == AuthMode.SIGN_UP) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                        label = { Text("Unique Username (3-20 chars)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFE67E22)) },
                        trailingIcon = {
                            if (isCheckingUsername) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFFE67E22), strokeWidth = 2.dp)
                            } else if (usernameStatus?.startsWith("✓") == true) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF27AE60))
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE67E22),
                            unfocusedBorderColor = Color(0x556B7280),
                            focusedTextColor = Color(0xFFF0F0F5),
                            unfocusedTextColor = Color(0xFFF0F0F5),
                            focusedLabelColor = Color(0xFFE67E22),
                            unfocusedLabelColor = Color(0xFF8A8D98)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_username")
                    )
                    if (usernameStatus != null) {
                        Text(
                            text = usernameStatus!!,
                            color = if (usernameStatus!!.startsWith("✓")) Color(0xFF27AE60) else Color(0xFFE74C3C),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFE67E22)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE67E22),
                        unfocusedBorderColor = Color(0x556B7280),
                        focusedTextColor = Color(0xFFF0F0F5),
                        unfocusedTextColor = Color(0xFFF0F0F5),
                        focusedLabelColor = Color(0xFFE67E22),
                        unfocusedLabelColor = Color(0xFF8A8D98)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_input_email")
                )

                // Password Field (Hidden in forgot password)
                if (mode != AuthMode.FORGOT_PASSWORD) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE67E22)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE67E22),
                            unfocusedBorderColor = Color(0x556B7280),
                            focusedTextColor = Color(0xFFF0F0F5),
                            unfocusedTextColor = Color(0xFFF0F0F5),
                            focusedLabelColor = Color(0xFFE67E22),
                            unfocusedLabelColor = Color(0xFF8A8D98)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_password")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Submit Action Button
                val isSubmitting = authState is AuthState.Loading
                ArcadeButton(
                    text = when {
                        isSubmitting -> "CONNECTING..."
                        mode == AuthMode.SIGN_UP -> "CREATE ACCOUNT"
                        mode == AuthMode.LOGIN -> "SIGN IN"
                        else -> "SEND RESET LINK"
                    },
                    onClick = {
                        localError = null
                        if (email.isBlank()) {
                            localError = "Please enter an email address."
                            return@ArcadeButton
                        }
                        coroutineScope.launch {
                            when (mode) {
                                AuthMode.SIGN_UP -> {
                                    if (username.length < 3) {
                                        localError = "Username must be at least 3 characters."
                                        return@launch
                                    }
                                    if (password.length < 6) {
                                        localError = "Password must be at least 6 characters."
                                        return@launch
                                    }
                                    authManager.signUpWithEmail(email.trim(), password, username.trim())
                                }
                                AuthMode.LOGIN -> {
                                    if (password.isBlank()) {
                                        localError = "Please enter your password."
                                        return@launch
                                    }
                                    authManager.loginWithEmail(email.trim(), password)
                                }
                                AuthMode.FORGOT_PASSWORD -> {
                                    authManager.sendPasswordReset(email.trim())
                                    localError = "Password reset email sent if account exists."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = Color(0xFFE67E22),
                    enabled = !isSubmitting,
                    testTag = "auth_btn_submit"
                )
            }
        }
    }
}
