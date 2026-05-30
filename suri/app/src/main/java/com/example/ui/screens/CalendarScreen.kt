package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.ui.translation.Translations
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    val selectedDateStr by viewModel.selectedDate.collectAsState()
    val selectedDate = remember(selectedDateStr) {
        try {
            LocalDate.parse(selectedDateStr)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
    
    val allLogs by viewModel.allDailyDetails.collectAsState()
    val activeLog by viewModel.currentDailyDetail.collectAsState()
    val cycleStats by viewModel.cycleStats.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val isEnglish = profile?.appLanguage != "Bahasa Melayu"

    // Map logs for quick lookup by date string
    val loggedBeedingDaysMap = remember(allLogs) {
        allLogs.filter { it.flowIntensity != "None" }.associateBy { it.dateStr }
    }
    
    val loggedSymptomsMap = remember(allLogs) {
        allLogs.associateBy { it.dateStr }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // 1. Month Picker Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = CocoaBrown)
            }
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")) + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = CocoaBrown,
                    letterSpacing = 1.sp
                )
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = CocoaBrown)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // 2. Custom Elegant Calendar Month Grid
        CalendarMonthGrid(
            month = currentMonth,
            selectedDate = selectedDate,
            loggedBleeding = loggedBeedingDaysMap,
            loggedDaily = loggedSymptomsMap,
            cycleStats = cycleStats,
            profile = profile,
            onDateSelected = { date ->
                viewModel.selectDate(date.toString())
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2.5 Period Cycle Configuration Card (For those who have started their period)
        val isModeB = profile?.hasStartedPeriod ?: false

        if (isModeB) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = SoftCream),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌸", fontSize = 20.sp)
                        Text(
                            text = Translations.getString("config_title", isEnglish),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CocoaBrown
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Translations.getString("config_desc", isEnglish),
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Assignment Actions
                    val localProfile = profile
                    val startDateText = if (localProfile?.lastPeriodStartDate?.isNotEmpty() == true) {
                        try {
                            LocalDate.parse(localProfile.lastPeriodStartDate).format(DateTimeFormatter.ofPattern("MMM d", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")))
                        } catch (e: Exception) {
                            localProfile.lastPeriodStartDate
                        }
                    } else {
                        Translations.getString("not_recorded", isEnglish)
                    }

                    val endDateText = if (localProfile?.lastPeriodEndDate?.isNotEmpty() == true) {
                        try {
                            LocalDate.parse(localProfile.lastPeriodEndDate).format(DateTimeFormatter.ofPattern("MMM d", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")))
                        } catch (e: Exception) {
                            localProfile.lastPeriodEndDate
                        }
                    } else {
                        Translations.getString("not_recorded", isEnglish)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Set Start Date button
                        Button(
                            onClick = {
                                viewModel.setManualPeriodStart(selectedDateStr)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DustyRose, contentColor = Cream),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(Translations.getString("mark_start", isEnglish), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Cream))
                                Text(startDateText, style = MaterialTheme.typography.labelSmall.copy(color = Cream.copy(alpha = 0.8f)))
                            }
                        }

                        // Set End Date button
                        Button(
                            onClick = {
                                viewModel.setManualPeriodEnd(selectedDateStr)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmBeige, contentColor = CocoaBrown),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(Translations.getString("mark_end", isEnglish), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown))
                                Text(endDateText, style = MaterialTheme.typography.labelSmall.copy(color = CocoaBrown.copy(alpha = 0.8f)))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = WarmBeige.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Current Configuration Summary
                    Text(
                        text = Translations.getString("current_cycle_settings", isEnglish),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val prof = profile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(Translations.getString("cycle_start_date_label", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                            Text(
                                text = if (prof?.lastPeriodStartDate?.isNotEmpty() == true) {
                                    try {
                                        LocalDate.parse(prof.lastPeriodStartDate).format(DateTimeFormatter.ofPattern("MMMM d, yyyy", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")))
                                    } catch (e: Exception) {
                                        Translations.getString("not_recorded", isEnglish)
                                    }
                                } else Translations.getString("not_recorded", isEnglish),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(Translations.getString("cycle_end_date_label", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                            Text(
                                text = if (prof?.lastPeriodEndDate?.isNotEmpty() == true) {
                                    try {
                                        LocalDate.parse(prof.lastPeriodEndDate).format(DateTimeFormatter.ofPattern("MMMM d, yyyy", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")))
                                    } catch (e: Exception) {
                                        Translations.getString("not_recorded", isEnglish)
                                    }
                                } else Translations.getString("not_recorded", isEnglish),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = CocoaBrown)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cycle Days Selection (Days of cycle)
                    val cycleDays = profile?.cycleLengthDays ?: 28
                    var localCycleDays by remember(cycleDays) { mutableStateOf(cycleDays.toFloat()) }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Translations.getString("set_cycle_length", isEnglish),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                            )
                            Text(
                                text = "${localCycleDays.toInt()} ${Translations.getString("days_suffix", isEnglish)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DustyRose)
                            )
                        }
                        Slider(
                            value = localCycleDays,
                            onValueChange = { localCycleDays = it },
                            onValueChangeFinished = {
                                viewModel.setCycleDays(localCycleDays.toInt())
                            },
                            valueRange = 21f..40f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = DustyRose,
                                activeTrackColor = DustyRose,
                                inactiveTrackColor = PaleRose
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Typical Period Length Selection (Duration of bleeding)
                    val periodLength = profile?.periodLengthDays ?: 5
                    var localPeriodLength by remember(periodLength) { mutableStateOf(periodLength.toFloat()) }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Translations.getString("set_period_duration", isEnglish),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown)
                            )
                            Text(
                                text = "${localPeriodLength.toInt()} ${Translations.getString("days_suffix", isEnglish)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DustyRose)
                            )
                        }
                        Slider(
                            value = localPeriodLength,
                            onValueChange = { localPeriodLength = it },
                            onValueChangeFinished = {
                                viewModel.setPeriodLengthDays(localPeriodLength.toInt())
                            },
                            valueRange = 3f..10f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = DustyRose,
                                activeTrackColor = DustyRose,
                                inactiveTrackColor = PaleRose
                            )
                        )
                    }

                    if (profile?.lastPeriodStartDate?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.clearManualPeriodDates() }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp), tint = SoftTaupe)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(Translations.getString("reset_configs", isEnglish), style = MaterialTheme.typography.labelSmall.copy(color = SoftTaupe))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Symptoms and Flow Logging Dashboard (For selected date)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Selected Date label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY"))),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = Translations.getString("daily_wellness_log", isEnglish),
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe)
                        )
                    }
                    if (activeLog != null) {
                        IconButton(onClick = { viewModel.deleteCurrentLog() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear Log", tint = SoftTaupe)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Flow Intensity Selector (Only if started period or toggling)
                if (isModeB) {
                    Text(
                        text = Translations.getString("period_flow_intensity", isEnglish),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("None", "Light", "Medium", "Heavy").forEach { intensity ->
                            val isSelected = (activeLog?.flowIntensity ?: "None") == intensity
                            val label = when(intensity) {
                                "None" -> Translations.getString("flow_none", isEnglish)
                                "Light" -> Translations.getString("flow_light", isEnglish)
                                "Medium" -> Translations.getString("flow_medium", isEnglish)
                                else -> Translations.getString("flow_heavy", isEnglish)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) DustyRose else PaleRose.copy(alpha = 0.4f))
                                    .border(1.dp, if (isSelected) DustyRose else WarmBeige, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateSelectedDateLog(flowIntensity = intensity) }
                                    .padding(vertical = 10.dp),
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Mood Tracker Selector
                Text(
                    text = Translations.getString("daily_mood_check", isEnglish),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val moods = listOf(
                    "Calm" to "😌",
                    "Emotional" to "🥺",
                    "Tired" to "😴",
                    "Anxious" to "😟",
                    "Happy" to "😊",
                    "Overwhelmed" to "🤯"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moods.forEach { (moodName, emoji) ->
                        val isSelected = activeLog?.mood == moodName
                        val label = when(moodName) {
                            "Calm" -> Translations.getString("mood_calm", isEnglish)
                            "Emotional" -> Translations.getString("mood_emotional", isEnglish)
                            "Tired" -> Translations.getString("mood_tired", isEnglish)
                            "Anxious" -> Translations.getString("mood_anxious", isEnglish)
                            "Happy" -> Translations.getString("mood_happy", isEnglish)
                            else -> Translations.getString("mood_overwhelmed", isEnglish)
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) DustyRose else PaleRose.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) DustyRose else WarmBeige, RoundedCornerShape(16.dp))
                                .clickable { viewModel.updateSelectedDateLog(mood = moodName) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$emoji $label",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Cream else CocoaBrown
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Cervical Discharge Tracker
                Text(
                    text = Translations.getString("discharge_check", isEnglish),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val dischargeTypes = listOf("Dry", "Slight Wetness", "White / Cloudy")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dischargeTypes.forEach { type ->
                        val isSelected = (activeLog?.discharge ?: "None") == type
                        val label = when (type) {
                            "Dry" -> Translations.getString("disc_dry", isEnglish)
                            "Slight Wetness" -> Translations.getString("disc_slight_wetness", isEnglish)
                            "White / Cloudy" -> Translations.getString("disc_white_cloudy", isEnglish)
                            else -> type
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) DustyRose else PaleRose.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) DustyRose else WarmBeige, RoundedCornerShape(16.dp))
                                .clickable { viewModel.updateSelectedDateLog(discharge = type) }
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
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Physical Symptoms Checkbox Row
                Text(
                    text = Translations.getString("physical_symptoms_logger", isEnglish),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val symptomsList = listOf(
                        Triple(Translations.getString("symp_cramps", isEnglish), activeLog?.cramps ?: false, "cramps"),
                        Triple(Translations.getString("symp_bloating", isEnglish), activeLog?.bloating ?: false, "bloating"),
                        Triple(Translations.getString("symp_acne", isEnglish), activeLog?.acne ?: false, "acne"),
                        Triple(Translations.getString("symp_cravings", isEnglish), activeLog?.cravings ?: false, "cravings"),
                        Triple(Translations.getString("symp_headache", isEnglish), activeLog?.headache ?: false, "headache"),
                        Triple(Translations.getString("symp_back_pain", isEnglish), activeLog?.backPain ?: false, "backPain"),
                        Triple(Translations.getString("symp_breast_tenderness", isEnglish), activeLog?.breastTenderness ?: false, "breast")
                    )

                    symptomsList.forEach { (label, checked, code) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (code) {
                                        "cramps" -> viewModel.updateSelectedDateLog(cramps = !checked)
                                        "bloating" -> viewModel.updateSelectedDateLog(bloating = !checked)
                                        "acne" -> viewModel.updateSelectedDateLog(acne = !checked)
                                        "cravings" -> viewModel.updateSelectedDateLog(cravings = !checked)
                                        "headache" -> viewModel.updateSelectedDateLog(headache = !checked)
                                        "backPain" -> viewModel.updateSelectedDateLog(backPain = !checked)
                                        "breast" -> viewModel.updateSelectedDateLog(breastTenderness = !checked)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { value ->
                                    when (code) {
                                        "cramps" -> viewModel.updateSelectedDateLog(cramps = value)
                                        "bloating" -> viewModel.updateSelectedDateLog(bloating = value)
                                        "acne" -> viewModel.updateSelectedDateLog(acne = value)
                                        "cravings" -> viewModel.updateSelectedDateLog(cravings = value)
                                        "headache" -> viewModel.updateSelectedDateLog(headache = value)
                                        "backPain" -> viewModel.updateSelectedDateLog(backPain = value)
                                        "breast" -> viewModel.updateSelectedDateLog(breastTenderness = value)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DustyRose,
                                    uncheckedColor = WarmBeige
                                )
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge.copy(color = CocoaBrown)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Private logs/Notes textbox
                Text(
                    text = Translations.getString("reflections_notes", isEnglish),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = activeLog?.notes ?: "",
                    onValueChange = { viewModel.updateSelectedDateLog(notes = it) },
                    placeholder = { 
                        Text(
                            text = if (isEnglish) "How does your body or mind feel right now?" else "Bagaimanakah minda dan fizikal tubuh anda rasa saat ini?", 
                            color = SoftTaupe.copy(alpha = 0.5f)
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DustyRose,
                        unfocusedBorderColor = WarmBeige,
                        focusedTextColor = CocoaBrown,
                        unfocusedTextColor = CocoaBrown
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = {
                        val savedMsg = Translations.getString("saved_toast", isEnglish)
                        android.widget.Toast.makeText(context, savedMsg, android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DustyRose, contentColor = Cream),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Save Wellness Log ✨",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cream
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    loggedBleeding: Map<String, com.example.data.DailyDetailsEntity>,
    loggedDaily: Map<String, com.example.data.DailyDetailsEntity>,
    cycleStats: com.example.ui.CyclePredictionStats,
    profile: com.example.data.UserProfileEntity?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = month.atDay(1)
    val totalDays = month.lengthOfMonth()
    
    // Day of the week of first day (1 = Monday, 7 = Sunday)
    // Adjust to let Sunday be first column (0 = Sun, 6 = Sat)
    val firstDayOfWeekIndex = (firstDayOfMonth.dayOfWeek.value % 7)

    // Day of the week header symbols
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SoftCream)
            .border(1.dp, WarmBeige.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Week Header
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SoftTaupe,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid rows
        var currentDayCounter = 1
        val numRows = (totalDays + firstDayOfWeekIndex + 6) / 7

        for (row in 0 until numRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val isDayActive = cellIndex >= firstDayOfWeekIndex && currentDayCounter <= totalDays
                    
                    if (isDayActive) {
                        val cellDate = month.atDay(currentDayCounter)
                        val dateStr = cellDate.toString()
                        
                        val isSelected = cellDate == selectedDate
                        val isToday = cellDate == LocalDate.now()
                        
                        // Condition checks for indicators
                        val hasBleedingLogged = loggedBleeding.containsKey(dateStr)
                        val hasStartedPeriod = profile?.hasStartedPeriod ?: false
                        
                        // Check if predicted period day (inclusive bounds of prediction)
                        val isPredictedPeriod = hasStartedPeriod && cycleStats.hasHistoricalData && 
                            !cellDate.isBefore(cycleStats.predictedNextPeriodStart) && 
                            !cellDate.isAfter(cycleStats.predictedNextPeriodEnd)
                            
                        // Check estimated ovulation day
                        val isOvulationDay = hasStartedPeriod && cycleStats.hasHistoricalData && cellDate == cycleStats.ovulationDate

                        // Check if symptoms logged (any notes or details)
                        val hasSymptomIndicator = loggedDaily[dateStr]?.let {
                            it.mood.isNotEmpty() || it.discharge != "None" ||
                            it.cramps || it.bloating || it.acne || it.headache ||
                            it.backPain || it.breastTenderness || it.notes.isNotEmpty()
                        } ?: false

                        val isManualStart = hasStartedPeriod && profile?.lastPeriodStartDate == dateStr
                        val isManualEnd = hasStartedPeriod && profile?.lastPeriodEndDate == dateStr
                        
                        val isActualPeriodRange = if (hasStartedPeriod && profile?.lastPeriodStartDate?.isNotEmpty() == true && profile?.lastPeriodEndDate?.isNotEmpty() == true) {
                            try {
                                val s = LocalDate.parse(profile.lastPeriodStartDate)
                                val e = LocalDate.parse(profile.lastPeriodEndDate)
                                !cellDate.isBefore(s) && !cellDate.isAfter(e)
                            } catch(ex: Exception) { false }
                        } else false

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected && isActualPeriodRange -> BrandBlushPink
                                        isSelected && hasStartedPeriod && hasBleedingLogged -> BrandBlushPink
                                        isSelected -> DustyRose
                                        isActualPeriodRange -> BrandBlushPink
                                        hasStartedPeriod && hasBleedingLogged -> BrandBlushPink
                                        hasStartedPeriod && isPredictedPeriod -> BrandBlushPink.copy(alpha = 0.25f)
                                        isToday -> DustyRose.copy(alpha = 0.25f)
                                        else -> androidx.compose.ui.graphics.Color.Transparent
                                    }
                                )
                                .border(
                                    width = when {
                                        isSelected -> 2.dp
                                        isToday -> 1.5.dp
                                        hasStartedPeriod && (isManualStart || isManualEnd) -> 2.dp
                                        hasStartedPeriod && isPredictedPeriod -> 1.dp
                                        else -> 0.dp
                                    },
                                    color = when {
                                        isSelected -> DustyRose
                                        isToday -> DustyRose
                                        hasStartedPeriod && (isManualStart || isManualEnd) -> BrandBlushPink
                                        hasStartedPeriod && isPredictedPeriod -> BrandBlushPink.copy(alpha = 0.4f)
                                        else -> androidx.compose.ui.graphics.Color.Transparent
                                    },
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(cellDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentDayCounter.toString(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isToday || isSelected || isActualPeriodRange || isManualStart || isManualEnd || (hasStartedPeriod && hasBleedingLogged)) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected && !(isActualPeriodRange || (hasStartedPeriod && hasBleedingLogged)) -> Cream
                                            isSelected -> CocoaBrown
                                            isActualPeriodRange -> CocoaBrown
                                            hasStartedPeriod && hasBleedingLogged -> CocoaBrown
                                            else -> CocoaBrown
                                        }
                                    )
                                )
                                
                                // Tiny Indicators Panel (Ovulation 🌸 or Symptom Log Dot)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                               ) {
                                    if (isOvulationDay) {
                                        Text(
                                            "🌸", 
                                            fontSize = 6.sp,
                                            modifier = Modifier.padding(horizontal = 0.5.dp)
                                        )
                                    } else if (hasSymptomIndicator) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.1.dp)
                                                .clip(CircleShape)
                                                .background(SoftTaupe)
                                                .padding(horizontal = 0.5.dp)
                                        )
                                    }
                                }
                            }
                        }
                        currentDayCounter++
                    } else {
                        // Empty padding cell
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
