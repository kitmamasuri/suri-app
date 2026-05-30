package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PeriodPalViewModel
import com.example.ui.AppTab
import com.example.ui.theme.*
import com.example.ui.translation.Translations
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val cycleStats by viewModel.cycleStats.collectAsState()
    val todayLog by viewModel.currentDailyDetail.collectAsState()
    val newUpdateAvailable by viewModel.newUpdateAvailable.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val isEnglish = profile?.appLanguage != "Bahasa Melayu"
    val userName = profile?.name ?: "Friend"
    val isModeB = profile?.hasStartedPeriod ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Personalized Header Vibe
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Translations.getString("welcome_back", isEnglish),
                    style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                )
                Text(
                    text = "$userName ✨",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = CocoaBrown,
                        fontWeight = FontWeight.Light
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PaleRose),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Auto Update Banner (Polished & Seamless M3 Card)
        AnimatedVisibility(visible = newUpdateAvailable != null) {
            newUpdateAvailable?.let { updateInfo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable {
                            try {
                                val targetUrl = if (updateInfo.url.contains("/releases/download/")) {
                                    "https://github.com/kitmamasuri/suri-app/releases"
                                } else {
                                    updateInfo.url
                                }
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(targetUrl)
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Update Available",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Translations.getString("update_available_title", isEnglish),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Translations.getString("update_available_desc", isEnglish),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Mode-Specific Prediction Widget
        if (isModeB) {
            ModeBCycleCard(cycleStats = cycleStats, isEnglish = isEnglish, onNavigateToCalendar = { viewModel.selectTab(AppTab.Calendar) })
        } else {
            ModeAPreparationCard(isEnglish = isEnglish, onNavigateToLearn = { viewModel.selectTab(AppTab.Learn) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Calming Daily Quote Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "“",
                    fontSize = 32.sp,
                    color = DustyRose,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.height(24.dp)
                )
                Text(
                    text = viewModel.getDailyQuote(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = CocoaBrown,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "— Suri Companion",
                    style = MaterialTheme.typography.labelMedium.copy(color = SoftTaupe)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Quick Symptom Logging Section
        QuickSymptomWidget(
            todayLog = todayLog,
            isEnglish = isEnglish,
            onSymptomToggle = { symptom, checked ->
                when (symptom) {
                    "cramps" -> viewModel.updateSelectedDateLog(cramps = checked)
                    "bloating" -> viewModel.updateSelectedDateLog(bloating = checked)
                    "acne" -> viewModel.updateSelectedDateLog(acne = checked)
                    "cravings" -> viewModel.updateSelectedDateLog(cravings = checked)
                    "headache" -> viewModel.updateSelectedDateLog(headache = checked)
                    "backPain" -> viewModel.updateSelectedDateLog(backPain = checked)
                    "breast" -> viewModel.updateSelectedDateLog(breastTenderness = checked)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Interactive Hydration Cup Logger
        HydrationWidget(
            glassesDrunk = todayLog?.waterIntakeCups ?: 0,
            isEnglish = isEnglish,
            onGlassesChange = { cups ->
                viewModel.updateSelectedDateLog(waterCups = cups.coerceIn(0, 8))
            }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ModeBCycleCard(
    cycleStats: com.example.ui.CyclePredictionStats,
    isEnglish: Boolean,
    onNavigateToCalendar: () -> Unit
) {
    val today = LocalDate.now()
    val daysUntilStart = ChronoUnit.DAYS.between(today, cycleStats.predictedNextPeriodStart)
    
    val dateString = cycleStats.predictedNextPeriodStart.format(
        DateTimeFormatter.ofPattern("MMMM d")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToCalendar() },
        colors = CardDefaults.cardColors(containerColor = PaleRose),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Translations.getString("next_predicted_period", isEnglish),
                style = MaterialTheme.typography.labelLarge.copy(color = SoftTaupe, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Large circular indicator
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(SoftCream, CircleShape)
                    .border(3.dp, DustyRose, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (daysUntilStart > 0) "$daysUntilStart" else if (daysUntilStart == 0L) "Today" else "Active",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CocoaBrown
                        )
                    )
                    Text(
                        text = if (daysUntilStart > 1) Translations.getString("days_left", isEnglish) else if (daysUntilStart == 1L) Translations.getString("day_left", isEnglish) else if (daysUntilStart == 0L) Translations.getString("is_here", isEnglish) else Translations.getString("period_days", isEnglish),
                        style = MaterialTheme.typography.labelMedium.copy(color = SoftTaupe)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${Translations.getString("starting_around", isEnglish)} $dateString",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CocoaBrown,
                    fontWeight = FontWeight.Medium
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Translations.getString("avg_cycle", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                    Text("${cycleStats.averageCycleLengthDays} ${Translations.getString("average_days", isEnglish)}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Translations.getString("avg_duration", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                    Text("${cycleStats.averagePeriodDurationDays} ${Translations.getString("average_days", isEnglish)}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Translations.getString("est_ovulation", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                    val ovString = cycleStats.ovulationDate.format(DateTimeFormatter.ofPattern("MMM d"))
                    Text(ovString, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown))
                }
            }
        }
    }
}

@Composable
fun ModeAPreparationCard(
    isEnglish: Boolean,
    onNavigateToLearn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToLearn() },
        colors = CardDefaults.cardColors(containerColor = PaleRose),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(SoftCream, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Translations.getString("waiting_first_period", isEnglish),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = CocoaBrown,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Translations.getString("grows_beautifully", isEnglish),
                style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToLearn,
                colors = ButtonDefaults.buttonColors(containerColor = DustyRose, contentColor = Cream),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(Translations.getString("emergency_prep", isEnglish))
            }
        }
    }
}

@Composable
fun QuickSymptomWidget(
    todayLog: com.example.data.DailyDetailsEntity?,
    isEnglish: Boolean,
    onSymptomToggle: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftCream),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Translations.getString("todays_symptoms", isEnglish),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CocoaBrown
                )
            )
            Text(
                text = Translations.getString("quick_check", isEnglish),
                style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Symptom Pill Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SymptomPill(
                    label = Translations.getString("cramps", isEnglish),
                    isSelected = todayLog?.cramps ?: false,
                    onToggle = { onSymptomToggle("cramps", it) }
                )
                SymptomPill(
                    label = Translations.getString("bloating", isEnglish),
                    isSelected = todayLog?.bloating ?: false,
                    onToggle = { onSymptomToggle("bloating", it) }
                )
                SymptomPill(
                    label = Translations.getString("acne", isEnglish),
                    isSelected = todayLog?.acne ?: false,
                    onToggle = { onSymptomToggle("acne", it) }
                )
                SymptomPill(
                    label = Translations.getString("headache", isEnglish),
                    isSelected = todayLog?.headache ?: false,
                    onToggle = { onSymptomToggle("headache", it) }
                )
                SymptomPill(
                    label = Translations.getString("back_pain", isEnglish),
                    isSelected = todayLog?.backPain ?: false,
                    onToggle = { onSymptomToggle("backPain", it) }
                )
                SymptomPill(
                    label = Translations.getString("tender_breast", isEnglish),
                    isSelected = todayLog?.breastTenderness ?: false,
                    onToggle = { onSymptomToggle("breast", it) }
                )
            }
        }
    }
}

@Composable
fun SymptomPill(
    label: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) DustyRose else PaleRose.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (isSelected) DustyRose else WarmBeige,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle(!isSelected) }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Cream else CocoaBrown
            )
        )
    }
}

@Composable
fun HydrationWidget(
    glassesDrunk: Int,
    isEnglish: Boolean,
    onGlassesChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftCream),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Translations.getString("hydration_tracker", isEnglish),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CocoaBrown
                        )
                    )
                    Text(
                        text = Translations.getString("aim_water", isEnglish),
                        style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PaleRose, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = "Water",
                        tint = DustyRose
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Cups grid (8 placeholders)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 1..8) {
                    val full = glassesDrunk >= i
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (full) DustyRose else PaleRose.copy(alpha = 0.6f)
                            )
                            .border(
                                1.dp,
                                if (full) DustyRose else WarmBeige,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (full && glassesDrunk == i) {
                                    // clicked on the highest full one, reduce by 1
                                    onGlassesChange(i - 1)
                                } else {
                                    onGlassesChange(i)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💧",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
