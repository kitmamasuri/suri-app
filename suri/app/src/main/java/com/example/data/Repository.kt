package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeriodPalRepository(private val db: AppDatabase) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()
    val allDailyDetails: Flow<List<DailyDetailsEntity>> = db.dailyDetailsDao().getAllDailyDetails()
    val checklistItems: Flow<List<ChecklistItemEntity>> = db.checklistItemDao().getAllChecklistItems()
    val journalEntries: Flow<List<JournalEntryEntity>> = db.journalEntryDao().getAllJournalEntries()

    init {
        scope.launch {
            seedChecklistIfNeeded()
        }
    }

    private suspend fun seedChecklistIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val items = db.checklistItemDao().getAllChecklistItems().first()
            if (items.isEmpty()) {
                val defaultChecklist = listOf(
                    "Pads",
                    "Wipes",
                    "Extra underwear",
                    "Heat patch",
                    "Snacks",
                    "Medicine",
                    "Plastic bag"
                )
                for (itemName in defaultChecklist) {
                    db.checklistItemDao().insertChecklistItem(
                        ChecklistItemEntity(name = itemName, isChecked = false, isCustom = false)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // User Profile Actions
    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        db.userProfileDao().insertProfile(profile)
    }

    suspend fun getProfileSync(): UserProfileEntity? = withContext(Dispatchers.IO) {
        db.userProfileDao().getUserProfileSync()
    }

    // Daily Details Actions
    fun getDailyDetailFlow(dateStr: String): Flow<DailyDetailsEntity?> {
        return db.dailyDetailsDao().getDailyDetailByDate(dateStr)
    }

    suspend fun getDailyDetailSync(dateStr: String): DailyDetailsEntity? = withContext(Dispatchers.IO) {
        db.dailyDetailsDao().getDailyDetailByDateSync(dateStr)
    }

    suspend fun saveDailyDetail(detail: DailyDetailsEntity) = withContext(Dispatchers.IO) {
        db.dailyDetailsDao().insertOrUpdateDailyDetail(detail)
    }

    // Checklist Actions
    suspend fun addChecklistItem(name: String, isCustom: Boolean) = withContext(Dispatchers.IO) {
        db.checklistItemDao().insertChecklistItem(
            ChecklistItemEntity(name = name, isChecked = false, isCustom = isCustom)
        )
    }

    suspend fun toggleChecklistItem(item: ChecklistItemEntity) = withContext(Dispatchers.IO) {
        db.checklistItemDao().updateChecklistItem(item.copy(isChecked = !item.isChecked))
    }

    suspend fun deleteChecklistItem(id: Int) = withContext(Dispatchers.IO) {
        db.checklistItemDao().deleteChecklistItemById(id)
    }

    // Journal Actions
    suspend fun addJournalEntry(title: String, content: String, emojiMood: String, dateStr: String) = withContext(Dispatchers.IO) {
        db.journalEntryDao().insertJournalEntry(
            JournalEntryEntity(title = title, content = content, emojiMood = emojiMood, dateStr = dateStr)
        )
    }

    suspend fun deleteJournalEntry(entry: JournalEntryEntity) = withContext(Dispatchers.IO) {
        db.journalEntryDao().deleteJournalEntry(entry)
    }
}
