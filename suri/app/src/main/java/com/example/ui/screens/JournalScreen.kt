package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HeartBroken
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
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    val journalEntries by viewModel.journalEntries.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val isEnglish = profile?.appLanguage != "Bahasa Melayu"

    var journalTitle by remember { mutableStateOf("") }
    var journalContent by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("😌") }

    val emojis = listOf("😌", "✨", "🌸", "🥺", "⛈️", "🕯️", "😴", "💖")

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
            text = Translations.getString("journal_title", isEnglish),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = Translations.getString("journal_tagline", isEnglish),
            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 1. Write Entry Form Card
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
                Text(
                    text = if (isEnglish) "Reflect on Today" else "Refleksi Hari Ini",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CocoaBrown
                    )
                )
                Text(
                    text = Translations.getString("how_do_feel", isEnglish),
                    style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Emoji Mood Row
                Text(
                    text = if (isEnglish) "Select Emoji Mood" else "Pilih Mood Emoji",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojis.forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) DustyRose else PaleRose.copy(alpha = 0.5f))
                                .border(
                                    1.dp,
                                    if (isSelected) DustyRose else WarmBeige,
                                    CircleShape
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = journalTitle,
                    onValueChange = { if (it.length <= 30) journalTitle = it },
                    placeholder = { 
                        Text(
                            text = if (isEnglish) "Title of my reflection (optional)" else "Tajuk refleksi saya (pilihan)", 
                            color = SoftTaupe.copy(alpha = 0.5f)
                        ) 
                    },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Content Note Input
                OutlinedTextField(
                    value = journalContent,
                    onValueChange = { journalContent = it },
                    placeholder = { Text(Translations.getString("reflection_placeholder", isEnglish), color = SoftTaupe.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DustyRose,
                        unfocusedBorderColor = WarmBeige,
                        focusedTextColor = CocoaBrown,
                        unfocusedTextColor = CocoaBrown
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (journalContent.isNotBlank()) {
                            viewModel.saveJournal(journalTitle, journalContent, selectedEmoji)
                            // reset inputs
                            journalTitle = ""
                            journalContent = ""
                            selectedEmoji = "😌"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DustyRose, contentColor = Cream),
                    shape = RoundedCornerShape(16.dp),
                    enabled = journalContent.isNotBlank()
                ) {
                    Text(Translations.getString("save_journal", isEnglish))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Historical Reflections Listing
        Text(
            text = Translations.getString("private_reflections", isEnglish),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Start
        )

        if (journalEntries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftCream),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🕯️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEnglish) "Your box of reflections is empty" else "Kotak refleksi anda masih kosong",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = CocoaBrown)
                    )
                    Text(
                        text = if (isEnglish) {
                            "Writing down your moods is a lovely way to understand yourself on your cycle journey."
                        } else {
                            "Menulis emosi dan mood anda adalah cara yang manis untuk memahami kitaran fasa kehidupan anda."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                journalEntries.forEach { entry ->
                    JournalItemCard(entry = entry, isEnglish = isEnglish, onDelete = { viewModel.deleteJournal(entry) })
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun JournalItemCard(
    entry: com.example.data.JournalEntryEntity,
    isEnglish: Boolean,
    onDelete: () -> Unit
) {
    val date = remember(entry.dateStr) {
        try {
            LocalDate.parse(entry.dateStr)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
    val formattedDate = remember(date, isEnglish) {
        try {
            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", if (isEnglish) Locale.ENGLISH else Locale("ms", "MY")))
        } catch (e: Exception) {
            ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftCream),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PaleRose, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(entry.emojiMood, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (entry.title == "Daily reflection" && !isEnglish) "Refleksi harian" else entry.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium.copy(color = SoftTaupe)
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = SoftTaupe)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = WarmBeige.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium.copy(color = CocoaBrown, lineHeight = 20.sp)
            )
        }
    }
}
