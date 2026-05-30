package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)
}

@Dao
interface DailyDetailsDao {
    @Query("SELECT * FROM daily_details ORDER BY dateStr DESC")
    fun getAllDailyDetails(): Flow<List<DailyDetailsEntity>>

    @Query("SELECT * FROM daily_details WHERE dateStr = :dateStr LIMIT 1")
    fun getDailyDetailByDate(dateStr: String): Flow<DailyDetailsEntity?>

    @Query("SELECT * FROM daily_details WHERE dateStr = :dateStr LIMIT 1")
    suspend fun getDailyDetailByDateSync(dateStr: String): DailyDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyDetail(details: DailyDetailsEntity)

    @Delete
    suspend fun deleteDailyDetail(details: DailyDetailsEntity)
}

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_item ORDER BY id ASC")
    fun getAllChecklistItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity)

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_item WHERE id = :id")
    suspend fun deleteChecklistItemById(id: Int)
}

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entry ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)
}
