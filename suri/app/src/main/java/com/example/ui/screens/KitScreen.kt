package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalMall
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

@Composable
fun KitScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.checklistItems.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val isEnglish = profile?.appLanguage != "Bahasa Melayu"
    var customItemName by remember { mutableStateOf("") }

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
            text = Translations.getString("kit_title", isEnglish),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = Translations.getString("kit_desc", isEnglish),
            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 20.dp)
        )

        // 1. Kit Visualization Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaleRose),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SoftCream, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalMall,
                        contentDescription = "Pouch Icon",
                        tint = DustyRose,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val completedCount = items.count { it.isChecked }
                    val totalCount = items.size
                    Text(
                        text = Translations.getString("pouch_status_header", isEnglish),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CocoaBrown
                        )
                    )
                    Text(
                        text = if (totalCount == 0) {
                            Translations.getString("pouch_ready", isEnglish)
                        } else {
                            "$completedCount ${Translations.getString("of", isEnglish)} $totalCount ${Translations.getString("items_packed", isEnglish)}"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Add New Custom Item Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customItemName,
                onValueChange = { if (it.length <= 25) customItemName = it },
                placeholder = { Text(Translations.getString("add_item_hint_detailed", isEnglish), color = SoftTaupe.copy(alpha = 0.5f)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DustyRose,
                    unfocusedBorderColor = WarmBeige,
                    focusedTextColor = CocoaBrown,
                    unfocusedTextColor = CocoaBrown
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (customItemName.isNotBlank()) {
                        viewModel.addKitItem(customItemName)
                        customItemName = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(DustyRose, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item",
                    tint = Cream
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Interactive Checklist Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                // Map default checklist names to dynamic Bahasa Melayu translations
                val translatedChecklistName = if (!isEnglish && !item.isCustom) {
                    when (item.name) {
                        "Pads" -> "Tuala Wanita (Pads)"
                        "Wipes" -> "Tisu Basah (Wipes)"
                        "Extra underwear" -> "Seluar Dalam Bersih"
                        "Heat patch" -> "Tampalan Hangat (Heat patch)"
                        "Snacks" -> "Makanan Ringan (Snacks)"
                        "Medicine" -> "Ubat-ubatan"
                        "Plastic bag" -> "Beg Plastik Salinan"
                        else -> item.name
                    }
                } else {
                    item.name
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isChecked) PaleRose.copy(alpha = 0.4f) else SoftCream
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (item.isChecked) DustyRose.copy(alpha = 0.3f) else WarmBeige.copy(alpha = 0.4f)
                    ),
                    onClick = { viewModel.toggleKitItem(item) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(0.85f)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { viewModel.toggleKitItem(item) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DustyRose,
                                    uncheckedColor = WarmBeige
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = translatedChecklistName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (item.isChecked) CocoaBrown.copy(alpha = 0.5f) else CocoaBrown,
                                    textDecoration = if (item.isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                )
                            )
                        }

                        if (item.isCustom) {
                            IconButton(
                                onClick = { viewModel.deleteKitItem(item.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Item",
                                    tint = SoftTaupe
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
