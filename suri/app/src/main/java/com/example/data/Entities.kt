package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Only 1 profile row
    val name: String,
    val age: Int,
    val hasStartedPeriod: Boolean,
    val wantReminders: Boolean,
    val enablePrivacyPin: Boolean,
    val pinCode: String = "",
    val appTheme: String = "Blossom", // Blossom (dusty rose), Sand (warm beige), Forest (taupe)
    val lastOnboardedTimestamp: Long = System.currentTimeMillis(),
    val lastPeriodStartDate: String = "",
    val lastPeriodEndDate: String = "",
    val cycleLengthDays: Int = 28,
    val periodLengthDays: Int = 5,
    val appLanguage: String = "English" // English or Bahasa Melayu (Malay)
)

@Entity(tableName = "daily_details")
data class DailyDetailsEntity(
    @PrimaryKey val dateStr: String, // format "YYYY-MM-DD" matching the local date
    val flowIntensity: String = "None", // "None", "Light", "Medium", "Heavy"
    val mood: String = "", // "Calm", "Emotional", "Tired", "Anxious", "Happy", "Overwhelmed"
    val discharge: String = "None", // "None", "Dry", "Sticky", "Creamy", "Watery", "Stretchy"
    val cramps: Boolean = false,
    val bloating: Boolean = false,
    val acne: Boolean = false,
    val cravings: Boolean = false,
    val headache: Boolean = false,
    val backPain: Boolean = false,
    val breastTenderness: Boolean = false,
    val waterIntakeCups: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "checklist_item")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isChecked: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "journal_entry")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateStr: String, // format "YYYY-MM-DD"
    val title: String,
    val content: String,
    val emojiMood: String,
    val timestamp: Long = System.currentTimeMillis()
)
