package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PeriodPalViewModel
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    
    // Inputs state
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(12) }
    var hasStartedPeriod by remember { mutableStateOf<Boolean?>(null) }
    var wantReminders by remember { mutableStateOf(true) }
    var enablePrivacyPin by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf("Blossom") }
    var selectedLanguage by remember { mutableStateOf("English") }

    val totalSteps = 5
    val isEnglish = selectedLanguage != "Bahasa Melayu"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Elegant Top Header with step progress
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SURI",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Step Progress Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..totalSteps) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= step) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                                )
                        )
                    }
                }
            }

            // Central Animated Steps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> StepWelcome(
                        name = name,
                        onNameChange = { name = it },
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = { selectedLanguage = it }
                    )
                    2 -> StepAge(
                        selectedAge = age,
                        onAgeChange = { age = it },
                        isEnglish = isEnglish
                    )
                    3 -> StepPeriodStatus(
                        selectedStatus = hasStartedPeriod,
                        onStatusChange = { hasStartedPeriod = it },
                        isEnglish = isEnglish
                    )
                    4 -> StepPreferences(
                        wantReminders = wantReminders,
                        onRemindersChange = { wantReminders = it },
                        enablePrivacyPin = enablePrivacyPin,
                        onPinToggle = { enablePrivacyPin = it },
                        pinCode = pinCode,
                        onPinChange = { pinCode = it },
                        isEnglish = isEnglish
                    )
                    5 -> StepThemeSelection(
                        selectedTheme = selectedTheme,
                        onThemeChange = { selectedTheme = it },
                        isEnglish = isEnglish
                    )
                }
            }

            // Bottom Navigation Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                if (step > 1) {
                    IconButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                                RoundedCornerShape(24.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous step",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Proceed Button
                Button(
                    onClick = {
                        if (step < totalSteps) {
                            if (step == 1 && name.trim().isEmpty()) {
                                name = if (isEnglish) "Friend" else "Sahabat"
                            }
                            if (step == 3 && hasStartedPeriod == null) {
                                hasStartedPeriod = false
                            }
                            step++
                        } else {
                            viewModel.completeOnboarding(
                                name = name.trim().ifEmpty { if (isEnglish) "Friend" else "Sahabat" },
                                age = age,
                                hasStartedPeriod = hasStartedPeriod ?: false,
                                wantReminders = wantReminders,
                                enablePrivacyPin = enablePrivacyPin,
                                pinCode = if (enablePrivacyPin) pinCode else "",
                                theme = selectedTheme,
                                language = selectedLanguage
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (step == totalSteps) {
                                if (isEnglish) "Complete" else "Selesai"
                            } else {
                                if (isEnglish) "Continue" else "Seterusnya"
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (step == totalSteps) Icons.Default.Done else Icons.Default.ChevronRight,
                            contentDescription = "Proceed"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepWelcome(
    name: String,
    onNameChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val isEnglish = selectedLanguage != "Bahasa Melayu"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(PaleRose, RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("✨", fontSize = 42.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Language toggle pill layout inside step 1 welcome
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PaleRose.copy(alpha = 0.5f))
                .border(1.dp, WarmBeige, RoundedCornerShape(14.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf("English", "Bahasa Melayu").forEach { lang ->
                val isSelected = selectedLanguage == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DustyRose else Color.Transparent)
                        .clickable { onLanguageChange(lang) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Cream else CocoaBrown
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (isEnglish) "Welcome to Suri" else "Selamat Datang ke Suri",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isEnglish) {
                "A calming space to learn about your body's beautiful growth with premium, supportive care."
            } else {
                "Satu ruang yang tenang untuk memahami pertumbuhan tubuh anda yang penuh bermakna dengan bimbingan istimewa dan penuh mesra."
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 15) onNameChange(it) },
            label = { Text(if (isEnglish) "What is your name?" else "Siapa nama panggilan anda?", color = SoftTaupe) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DustyRose,
                unfocusedBorderColor = WarmBeige,
                focusedLabelColor = CocoaBrown,
                focusedTextColor = CocoaBrown,
                unfocusedTextColor = CocoaBrown
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}

@Composable
fun StepAge(
    selectedAge: Int,
    onAgeChange: (Int) -> Unit,
    isEnglish: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "How old are you?" else "Berapakah umur anda?",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isEnglish) {
                "First periods usually happen between 9 and 15. Knowing your age helps us provide relevant gentle guidance."
            } else {
                "Haid pertama kebiasaannya datang bermula seawal usia 9 hingga 15 tahun. Penentuan usia anda membantu memberikan bimbingan yang bersesuaian."
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = SoftTaupe),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Age selectors (Horizontal capsule list)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (age in 9..12) {
                AgeChip(
                    age = age,
                    isSelected = selectedAge == age,
                    onClick = { onAgeChange(age) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (age in 13..15) {
                AgeChip(
                    age = age,
                    isSelected = selectedAge == age,
                    onClick = { onAgeChange(age) }
                )
            }
        }
    }
}

@Composable
fun AgeChip(
    age: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(if (isSelected) DustyRose else PaleRose)
            .border(
                1.dp,
                if (isSelected) DustyRose else WarmBeige,
                RoundedCornerShape(32.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = age.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Cream else CocoaBrown
            )
        )
    }
}

@Composable
fun StepPeriodStatus(
    selectedStatus: Boolean?,
    onStatusChange: (Boolean) -> Unit,
    isEnglish: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "Have you started your period yet?" else "Adakah haid pertama anda sudah bermula?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isEnglish) {
                "Everyone is different! We customize the entire interface to match your current step in life."
            } else {
                "Semua orang berbeza dan unik! Kami menyusun rekaan antara muka aplikasi yang bersesuaian dengan ritma fasa tubuh semasa anda."
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = SoftTaupe),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Large Premium Choice Buttons
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode A Option
            Card(
                onClick = { onStatusChange(false) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedStatus == false) PaleRose else SoftCream
                ),
                shape = RoundedCornerShape(20.dp),
                border = BoxBorder(selectedStatus == false)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌸", fontSize = 28.sp, modifier = Modifier.padding(end = 16.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Not yet, I'm waiting" else "Belum lagi, masih menanti & bersedia",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = if (isEnglish) {
                                "Gentle preparation tips, symptom signs tracking, and friendly reminders."
                            } else {
                                "Pelajari tip persediaan ringan, tanda-tanda kematangan, penjejakan simptom, dan nota mesra."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                        )
                    }
                }
            }

            // Mode B Option
            Card(
                onClick = { onStatusChange(true) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedStatus == true) PaleRose else SoftCream
                ),
                shape = RoundedCornerShape(20.dp),
                border = BoxBorder(selectedStatus == true)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🗓️", fontSize = 28.sp, modifier = Modifier.padding(end = 16.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Yes, I have started" else "Ya, haid pertama saya sudah mula",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = if (isEnglish) {
                                "Period logging calendar, flow intensity tracking, cycle trends, and support."
                            } else {
                                "Gunakan kalendar log haid peribadi, corak aliran bulanan, fasa kesucian, serta bimbingan khusus."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepPreferences(
    wantReminders: Boolean,
    onRemindersChange: (Boolean) -> Unit,
    enablePrivacyPin: Boolean,
    onPinToggle: (Boolean) -> Unit,
    pinCode: String,
    onPinChange: (String) -> Unit,
    isEnglish: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "Your Privacy & Support" else "Sokongan & Privasi Peribadi",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (isEnglish) {
                "Personalize your reminders and keep your private notes entirely protected."
            } else {
                "Sesuaikan bantuan peringatan anda dan lindungi diari peribadi anda dengan selamat."
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reminders Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = DustyRose,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = if (isEnglish) "Calming Reminders" else "Peringatan Menenangkan",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                            )
                            Text(
                                text = if (isEnglish) "Supportive wellness nudges" else "Nasihat dan cetusan semangat harian",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                            )
                        }
                    }
                    Switch(
                        checked = wantReminders,
                        onCheckedChange = onRemindersChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Cream,
                            checkedTrackColor = DustyRose,
                            uncheckedThumbColor = SoftTaupe,
                            uncheckedTrackColor = PaleRose
                        )
                    )
                }

                HorizontalDivider(color = WarmBeige.copy(alpha = 0.5f))

                // Privacy PIN Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = DustyRose,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = if (isEnglish) "Privacy PIN Code" else "Kod PIN keselamatan",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                            )
                            Text(
                                text = if (isEnglish) "Lock the app for absolute privacy" else "Kunci aplikasi demi privasi mutlak",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                            )
                        }
                    }
                    Switch(
                        checked = enablePrivacyPin,
                        onCheckedChange = onPinToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Cream,
                            checkedTrackColor = DustyRose,
                            uncheckedThumbColor = SoftTaupe,
                            uncheckedTrackColor = PaleRose
                        )
                    )
                }

                // If PIN enabled, show text field nicely
                AnimatedVisibility(
                    visible = enablePrivacyPin,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "Enter a 4-digit security PIN" else "Masukkan 4 angka Kod PIN peribadi",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = { inputVal ->
                                if (inputVal.all { char -> char.isDigit() } && inputVal.length <= 4) {
                                    onPinChange(inputVal)
                                }
                            },
                            placeholder = { Text("1234", color = SoftTaupe.copy(alpha = 0.5f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DustyRose,
                                unfocusedBorderColor = WarmBeige,
                                focusedTextColor = CocoaBrown,
                                unfocusedTextColor = CocoaBrown
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.width(120.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepThemeSelection(
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    isEnglish: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "Choose Your Vibe" else "Pilih Suasana Anda",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isEnglish) {
                "Select a gentle editorial color palette that mirrors your style."
            } else {
                "Pilih palet ton warna lembut kegemaran yang bersesuaian dengan citarasa unik anda."
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = SoftTaupe),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeCard(
                name = "Blossom",
                desc = if (isEnglish) "Delicate muted blush pink (Cherry Blossom vibes)" else "Warna merah jambu lembut berseri ceria",
                accentColor = BrandBlushPink,
                isSelected = selectedTheme == "Blossom",
                onClick = { onThemeChange("Blossom") }
            )

            ThemeCard(
                name = "Sand",
                desc = if (isEnglish) "Sophisticated, calming grey-blue (Ocean Slate vibes)" else "Warna kelabu jingga kebiruan langit damai",
                accentColor = BrandGreyBlue,
                isSelected = selectedTheme == "Sand",
                onClick = { onThemeChange("Sand") }
            )

            ThemeCard(
                name = "Forest",
                desc = if (isEnglish) "Restorative organic sage & forest green (Deep Sage vibes)" else "Warna herba hijau daun sage penyembuh",
                accentColor = BrandSage,
                isSelected = selectedTheme == "Forest",
                onClick = { onThemeChange("Forest") }
            )

            ThemeCard(
                name = "Dark",
                desc = if (isEnglish) "Soothing deep forest midnight (Cozy Dark Mode)" else "Sensasi malam sejuk redup menyamankan (Mod Gelap)",
                accentColor = BrandForestGreen,
                isSelected = selectedTheme == "Dark",
                onClick = { onThemeChange("Dark") }
            )
        }
    }
}

@Composable
fun ThemeCard(
    name: String,
    desc: String,
    accentColor: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PaleRose else SoftCream
        ),
        shape = RoundedCornerShape(16.dp),
        border = BoxBorder(isSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor)
                    .border(1.dp, WarmBeige, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = CocoaBrown
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                )
            }
        }
    }
}

@Composable
private fun BoxBorder(isSelected: Boolean): androidx.compose.foundation.BorderStroke? {
    return if (isSelected) {
        androidx.compose.foundation.BorderStroke(2.dp, DustyRose)
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
    }
}
