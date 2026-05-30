package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PeriodPalViewModel
import com.example.ui.theme.*

@Composable
fun PinLockScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    var pinEntered by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val profileState by viewModel.userProfile.collectAsState()
    val userName = profileState?.name ?: "Friend"
    val isEnglish = profileState?.appLanguage != "Bahasa Melayu"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cream)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Upper Lock Illustration
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PaleRose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = DustyRose,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isEnglish) "Welcome back, $userName" else "Selamat kembali, $userName",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = CocoaBrown
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isEnglish) "Please enter your 4-digit PIN for privacy protection." else "Sila masukkan 4 angka Kod PIN peribadi anda.",
                style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        // Indicator Bubbles (4 circles)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 4) {
                val filled = pinEntered.length > i
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) DustyRose else WarmBeige.copy(alpha = 0.5f)
                        )
                        .border(
                            1.dp,
                            if (filled) DustyRose else WarmBeige,
                            CircleShape
                        )
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MutedRose,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        // Numerical Grid Keypad
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("Clear", "0", "Delete")
            )

            for (row in rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (key in row) {
                        KeypadButton(
                            value = key,
                            onClick = {
                                when (key) {
                                    "Clear" -> {
                                        pinEntered = ""
                                        errorMessage = null
                                    }
                                    "Delete" -> {
                                        if (pinEntered.isNotEmpty()) {
                                            pinEntered = pinEntered.dropLast(1)
                                            errorMessage = null
                                        }
                                    }
                                    else -> {
                                        if (pinEntered.length < 4) {
                                            pinEntered += key
                                            errorMessage = null
                                            if (pinEntered.length == 4) {
                                                val success = viewModel.unlockWithPin(pinEntered)
                                                if (!success) {
                                                    pinEntered = ""
                                                    errorMessage = if (isEnglish) "Incorrect PIN code. Try again." else "Kod PIN salah. Cuba lagi."
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    value: String,
    onClick: () -> Unit
) {
    if (value == "Clear" || value == "Delete") {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (value == "Clear") {
                Text(
                    text = "C",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SoftTaupe,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = SoftTaupe,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PaleRose.copy(alpha = 0.5f))
                .border(1.dp, WarmBeige.copy(alpha = 0.4f), CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CocoaBrown
                )
            )
        }
    }
}
