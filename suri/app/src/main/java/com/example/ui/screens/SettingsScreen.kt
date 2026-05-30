package com.example.ui.screens

import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PeriodPalViewModel
import com.example.ui.theme.*
import com.example.ui.translation.Translations

@Composable
fun SettingsScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profileState by viewModel.userProfile.collectAsState()
    val isCheckingUpdates by viewModel.isCheckingUpdates.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateRedirectUrl.collect { url ->
            try {
                val targetUrl = if (url.contains("/releases/download/")) {
                    "https://github.com/kitmamasuri/suri-app/releases"
                } else {
                    url
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var editName by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf(12) }
    var modeStarted by remember { mutableStateOf(false) }
    var enablePin by remember { mutableStateOf(false) }
    var pinVal by remember { mutableStateOf("") }
    var activeTheme by remember { mutableStateOf("Blossom") }
    var reminderToggle by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English") }

    // Sync state once profile loads
    LaunchedEffect(profileState) {
        profileState?.let {
            editName = it.name
            editAge = it.age
            modeStarted = it.hasStartedPeriod
            enablePin = it.enablePrivacyPin
            pinVal = it.pinCode
            activeTheme = it.appTheme
            reminderToggle = it.wantReminders
            selectedLanguage = it.appLanguage
        }
    }

    val isEnglish = selectedLanguage != "Bahasa Melayu"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = Translations.getString("personal_settings", isEnglish),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = Translations.getString("fine_tune_suri", isEnglish),
            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 1. Profile Editing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = Translations.getString("my_identity", isEnglish),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                )

                // Name Input
                OutlinedTextField(
                    value = editName,
                    onValueChange = { if (it.length <= 15) editName = it },
                    label = { Text(Translations.getString("display_name", isEnglish), color = SoftTaupe) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DustyRose,
                        unfocusedBorderColor = WarmBeige,
                        focusedTextColor = CocoaBrown,
                        unfocusedTextColor = CocoaBrown
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Age slider or pill selectors
                Column {
                    Text(
                        text = "${Translations.getString("age_label", isEnglish)}: $editAge ${Translations.getString("years_old", isEnglish)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                    )
                    Slider(
                        value = editAge.toFloat(),
                        onValueChange = { editAge = it.toInt() },
                        valueRange = 9f..15f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = DustyRose,
                            activeTrackColor = DustyRose,
                            inactiveTrackColor = PaleRose
                        )
                    )
                }

                // Mode Selection A vs B
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.7f)) {
                        Text(
                            text = Translations.getString("period_has_started", isEnglish),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                        )
                        Text(
                            text = Translations.getString("enables_predictive", isEnglish),
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                        )
                    }
                    Switch(
                        checked = modeStarted,
                        onCheckedChange = { modeStarted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Cream,
                            checkedTrackColor = DustyRose,
                            uncheckedThumbColor = SoftTaupe,
                            uncheckedTrackColor = PaleRose
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Security and Reminders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = Translations.getString("preferences_privacy", isEnglish),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                )

                // Reminders Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Translations.getString("supportive_reminders", isEnglish),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                        )
                        Text(
                            text = Translations.getString("warm_notifications", isEnglish),
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                        )
                    }
                    Switch(
                        checked = reminderToggle,
                        onCheckedChange = { reminderToggle = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Cream,
                            checkedTrackColor = DustyRose,
                            uncheckedThumbColor = SoftTaupe,
                            uncheckedTrackColor = PaleRose
                        )
                    )
                }

                HorizontalDivider(color = WarmBeige.copy(alpha = 0.3f))

                // PIN Code Security toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Translations.getString("pin_protection", isEnglish),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                        )
                        Text(
                            text = Translations.getString("locks_app", isEnglish),
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                        )
                    }
                    Switch(
                        checked = enablePin,
                        onCheckedChange = { enablePin = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Cream,
                            checkedTrackColor = DustyRose,
                            uncheckedThumbColor = SoftTaupe,
                            uncheckedTrackColor = PaleRose
                        )
                    )
                }

                // If PIN active, text input
                AnimatedVisibility(
                    visible = enablePin,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Translations.getString("security_pin", isEnglish),
                            style = MaterialTheme.typography.bodyMedium.copy(color = CocoaBrown)
                        )
                        OutlinedTextField(
                            value = pinVal,
                            onValueChange = { inputPinVal ->
                                if (inputPinVal.all { char -> char.isDigit() } && inputPinVal.length <= 4) {
                                    pinVal = inputPinVal
                                }
                            },
                            placeholder = { Text("1234", color = SoftTaupe.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DustyRose,
                                unfocusedBorderColor = WarmBeige,
                                focusedTextColor = CocoaBrown,
                                unfocusedTextColor = CocoaBrown
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.width(100.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }

                HorizontalDivider(color = WarmBeige.copy(alpha = 0.3f))

                // Language Option Selector Card inside settings
                Column {
                    Text(
                        text = Translations.getString("app_lang", isEnglish),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                    )
                    Text(
                        text = Translations.getString("app_lang_desc", isEnglish),
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("English", "Bahasa Melayu").forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PaleRose else SoftCream)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) DustyRose else WarmBeige.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedLanguage = lang }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CocoaBrown
                                    )
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = WarmBeige.copy(alpha = 0.3f))

                // Theme Selection In-setting
                Column {
                    Text(
                        text = Translations.getString("app_theme", isEnglish),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Blossom", "Sand", "Forest", "Dark").forEach { themeName ->
                            val isSelected = activeTheme == themeName
                            val color = when(themeName) {
                                "Blossom" -> BrandBlushPink
                                "Sand" -> BrandGreyBlue
                                "Forest" -> BrandSage
                                else -> BrandForestGreen
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PaleRose else SoftCream)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) DustyRose else WarmBeige.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { activeTheme = themeName }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when(themeName) {
                                            "Blossom" -> Translations.getString("theme_blush", isEnglish)
                                            "Sand" -> Translations.getString("theme_blue", isEnglish)
                                            "Forest" -> Translations.getString("theme_sage", isEnglish)
                                            else -> Translations.getString("theme_dark", isEnglish)
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CocoaBrown
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Settings Actions Panel
        Button(
            onClick = {
                profileState?.let {
                    val updated = it.copy(
                        name = editName.trim().ifEmpty { "Friend" },
                        age = editAge,
                        hasStartedPeriod = modeStarted,
                        enablePrivacyPin = enablePin,
                        pinCode = if (enablePin) pinVal else "",
                        appTheme = activeTheme,
                        wantReminders = reminderToggle,
                        appLanguage = selectedLanguage
                    )
                    viewModel.updateProfile(updated)
                    val savedMsg = if (selectedLanguage == "Bahasa Melayu") "Tetapan berjaya disimpan dengan selamat 🌸" else "Settings saved securely 🌸"
                    Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DustyRose, contentColor = Cream),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(Translations.getString("update_settings", isEnglish))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. User-Facing App Update Center
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isEnglish) "App Update Center" else "Pusat Kemas Kini Aplikasi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                )
                Text(
                    text = if (isEnglish) {
                        "Ensure your Suri companion has the latest emotional support modules and optimal cycle logging database metrics."
                    } else {
                        "Pastikan pembantu Suri anda mempunyai modul sokongan emosi terkini dan metrik pangkalan data log kitaran optimum."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        viewModel.checkForUpdates()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmBeige, contentColor = CocoaBrown),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCheckingUpdates
                ) {
                    if (isCheckingUpdates) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CocoaBrown,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEnglish) "Checking..." else "Menyemak...",
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Check for Updates", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Check for Updates" else "Semak Kemas Kini",
                            fontSize = 14.sp
                        )
                    }
                }

                updateStatus?.let { status ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PaleRose.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = CocoaBrown
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
