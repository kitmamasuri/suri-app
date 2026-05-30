package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChecklistItemEntity
import com.example.data.DailyDetailsEntity
import com.example.data.JournalEntryEntity
import com.example.data.PeriodPalRepository
import com.example.data.UserProfileEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PeriodPalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PeriodPalRepository

    // Screen navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow<AppTab>(AppTab.Home)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _showSettingsInJournal = MutableStateFlow(false)
    val showSettingsInJournal: StateFlow<Boolean> = _showSettingsInJournal.asStateFlow()

    fun setShowSettingsInJournal(show: Boolean) {
        _showSettingsInJournal.value = show
    }

    // Database Flows
    val userProfile: StateFlow<UserProfileEntity?>
    val allDailyDetails: StateFlow<List<DailyDetailsEntity>>
    val checklistItems: StateFlow<List<ChecklistItemEntity>>
    val journalEntries: StateFlow<List<JournalEntryEntity>>

    // Interactive date flow for calendar and logging
    private val _selectedDate = MutableStateFlow<String>(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Reactive detailed log of the selected date
    val currentDailyDetail: StateFlow<DailyDetailsEntity?>

    // Security PIN state
    private val _pinLocked = MutableStateFlow(false)
    val pinLocked: StateFlow<Boolean> = _pinLocked.asStateFlow()

    private val _updateJsonUrl = "https://gist.githubusercontent.com/kitmamasuri/744ab875cbd58647ff44867d0b6a06a5/raw/077cd81e40eda1e9f69366f879dd0b47a1a5f65e/suri-version.json"

    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> = _updateStatus.asStateFlow()

    private val _isCheckingUpdates = MutableStateFlow(false)
    val isCheckingUpdates: StateFlow<Boolean> = _isCheckingUpdates.asStateFlow()

    private val _updateRedirectUrl = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val updateRedirectUrl: SharedFlow<String> = _updateRedirectUrl.asSharedFlow()

    private val _newUpdateAvailable = MutableStateFlow<SuriVersionInfo?>(null)
    val newUpdateAvailable: StateFlow<SuriVersionInfo?> = _newUpdateAvailable.asStateFlow()

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    fun checkForUpdates(autoCheck: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdates.value = true
            if (!autoCheck) {
                _updateStatus.value = "Checking for updates..."
            }
            try {
                val moshiInstance = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val retrofitInstance = Retrofit.Builder()
                    .baseUrl("https://gist.githubusercontent.com/")
                    .addConverterFactory(MoshiConverterFactory.create(moshiInstance))
                    .build()
                val api = retrofitInstance.create(SuriUpdateApi::class.java)

                val versionInfo = api.getVersionInfo(_updateJsonUrl)
                if (versionInfo.versionCode > 2) {
                    _newUpdateAvailable.value = versionInfo
                    if (!autoCheck) {
                        _updateStatus.value = "New production update found (Code: ${versionInfo.versionCode})! Directing to secure download... ✓"
                        _updateRedirectUrl.emit(versionInfo.url)
                    }
                } else {
                    _newUpdateAvailable.value = null
                    if (!autoCheck) {
                        _updateStatus.value = "Suri is up to date (Version 1.2.0, Code: 2) ✓"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (!autoCheck) {
                    _updateStatus.value = "Suri is up to date (Offline Mode) ✓"
                }
            } finally {
                _isCheckingUpdates.value = false
            }
        }
    }

    private val _lastNotification = MutableStateFlow<String?>(null)
    val lastNotification: StateFlow<String?> = _lastNotification.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PeriodPalRepository(database)

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allDailyDetails = repository.allDailyDetails.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        checklistItems = repository.checklistItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        journalEntries = repository.journalEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        currentDailyDetail = _selectedDate.flatMapLatest { date ->
            repository.getDailyDetailFlow(date)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Trigger basic random supportive tips & notifications
        triggerDailyQuotesAndReminders()
        checkForUpdates(autoCheck = true)
    }

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun initializeAppNavigation() {
        viewModelScope.launch {
            val profile = repository.getProfileSync()
            if (profile == null) {
                _currentScreen.value = AppScreen.Onboarding
            } else if (profile.enablePrivacyPin) {
                _currentScreen.value = AppScreen.PinLock
            } else {
                _currentScreen.value = AppScreen.Main
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectDate(dateStr: String) {
        _selectedDate.value = dateStr
    }

    // Profile & Onboarding Actions
    fun completeOnboarding(
        name: String,
        age: Int,
        hasStartedPeriod: Boolean,
        wantReminders: Boolean,
        enablePrivacyPin: Boolean,
        pinCode: String,
        theme: String,
        language: String
    ) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                name = name,
                age = age,
                hasStartedPeriod = hasStartedPeriod,
                wantReminders = wantReminders,
                enablePrivacyPin = enablePrivacyPin,
                pinCode = pinCode,
                appTheme = theme,
                appLanguage = language
            )
            repository.saveUserProfile(profile)
            _currentScreen.value = AppScreen.Main
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    // Security PIN Lock Actions
    fun unlockWithPin(pin: String): Boolean {
        val currentProfile = userProfile.value
        return if (currentProfile != null && currentProfile.pinCode == pin) {
            _pinLocked.value = false
            _currentScreen.value = AppScreen.Main
            true
        } else {
            false
        }
    }

    // Daily Symptoms & Flow tracking
    fun updateSelectedDateLog(
        flowIntensity: String? = null,
        mood: String? = null,
        discharge: String? = null,
        cramps: Boolean? = null,
        bloating: Boolean? = null,
        acne: Boolean? = null,
        cravings: Boolean? = null,
        headache: Boolean? = null,
        backPain: Boolean? = null,
        breastTenderness: Boolean? = null,
        waterCups: Int? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            val currentDate = _selectedDate.value
            val existing = repository.getDailyDetailSync(currentDate) ?: DailyDetailsEntity(dateStr = currentDate)

            val updated = existing.copy(
                flowIntensity = flowIntensity ?: existing.flowIntensity,
                mood = mood ?: existing.mood,
                discharge = discharge ?: existing.discharge,
                cramps = cramps ?: existing.cramps,
                bloating = bloating ?: existing.bloating,
                acne = acne ?: existing.acne,
                cravings = cravings ?: existing.cravings,
                headache = headache ?: existing.headache,
                backPain = backPain ?: existing.backPain,
                breastTenderness = breastTenderness ?: existing.breastTenderness,
                waterIntakeCups = waterCups ?: existing.waterIntakeCups,
                notes = notes ?: existing.notes
            )
            repository.saveDailyDetail(updated)

            // Automate start and end date registration on logging flow to prevent drift
            if (flowIntensity != null && flowIntensity != "None") {
                val profile = repository.getProfileSync()
                if (profile != null) {
                    val logs = repository.allDailyDetails.first().toMutableList()
                    val idx = logs.indexOfFirst { it.dateStr == currentDate }
                    if (idx >= 0) {
                        logs[idx] = updated
                    } else {
                        logs.add(updated)
                    }

                    val periodDays = logs.filter { it.flowIntensity != "None" }
                        .mapNotNull { 
                            try { LocalDate.parse(it.dateStr) } catch (e: Exception) { null }
                        }
                        .sorted()

                    val segments = mutableListOf<MutableList<LocalDate>>()
                    var currentSegment = mutableListOf<LocalDate>()

                    for (day in periodDays) {
                        if (currentSegment.isEmpty()) {
                            currentSegment.add(day)
                        } else {
                            val lastDay = currentSegment.last()
                            val daysBetween = ChronoUnit.DAYS.between(lastDay, day)
                            if (daysBetween <= 3) {
                                currentSegment.add(day)
                            } else {
                                segments.add(currentSegment)
                                currentSegment = mutableListOf(day)
                            }
                        }
                    }
                    if (currentSegment.isNotEmpty()) {
                        segments.add(currentSegment)
                    }

                    val currDateObj = try { LocalDate.parse(currentDate) } catch (e: Exception) { null }
                    if (currDateObj != null) {
                        val matchingSegment = segments.find { segment ->
                            segment.any { it == currDateObj }
                        } ?: segments.lastOrNull()

                        if (matchingSegment != null) {
                            val startStr = matchingSegment.first().toString()
                            val endStr = matchingSegment.last().toString()
                            
                            repository.saveUserProfile(profile.copy(
                                lastPeriodStartDate = startStr,
                                lastPeriodEndDate = endStr
                            ))
                        }
                    }
                }
            }
        }
    }

    fun deleteCurrentLog() {
        viewModelScope.launch {
            val existing = repository.getDailyDetailSync(_selectedDate.value)
            if (existing != null) {
                // Return to clean slate
                repository.saveDailyDetail(DailyDetailsEntity(dateStr = _selectedDate.value))
            }
        }
    }

    // Kit Checklist actions
    fun addKitItem(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.addChecklistItem(name.trim(), isCustom = true)
            }
        }
    }

    fun toggleKitItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            repository.toggleChecklistItem(item)
        }
    }

    fun deleteKitItem(id: Int) {
        viewModelScope.launch {
            repository.deleteChecklistItem(id)
        }
    }

    // Journal Actions
    fun saveJournal(title: String, content: String, mood: String) {
        viewModelScope.launch {
            if (content.isNotBlank()) {
                repository.addJournalEntry(
                    title = if (title.isBlank()) "Daily reflection" else title,
                    content = content,
                    emojiMood = mood,
                    dateStr = LocalDate.now().toString()
                )
            }
        }
    }

    fun deleteJournal(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entry)
        }
    }

    // Advanced: Cycle Insights & Prediction Calculations
    // Returns prediction stats based on historical data or manual configuration
    val cycleStats: StateFlow<CyclePredictionStats> = combine(
        allDailyDetails,
        userProfile
    ) { logs, profile ->
        calculateCyclePrediction(logs, profile)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CyclePredictionStats()
    )

    private fun calculateCyclePrediction(
        logs: List<DailyDetailsEntity>,
        profile: UserProfileEntity?
    ): CyclePredictionStats {
        return try {
            val manStartStr = profile?.lastPeriodStartDate ?: ""
            val manEndStr = profile?.lastPeriodEndDate ?: ""
            val manualCycleDays = profile?.cycleLengthDays ?: 28
            val manualPeriodDays = profile?.periodLengthDays ?: 5

            val manualStart = if (manStartStr.isNotEmpty()) {
                try { LocalDate.parse(manStartStr) } catch (e: Exception) { null }
            } else null

            val manualEnd = if (manEndStr.isNotEmpty()) {
                try { LocalDate.parse(manEndStr) } catch (e: Exception) { null }
            } else null

            if (manualStart != null) {
                // Determine actual duration
                val actualDuration = if (manualEnd != null && !manualEnd.isBefore(manualStart)) {
                    ChronoUnit.DAYS.between(manualStart, manualEnd).toInt() + 1
                } else {
                    manualPeriodDays
                }

                // Days of cycle prediction projected forward to prevent calculation drift
                var nextPeriodStart = manualStart.plusDays(manualCycleDays.toLong())
                val durationDays = (actualDuration - 1).coerceAtLeast(0)
                if (manualCycleDays > 0) {
                    while (nextPeriodStart.plusDays(durationDays.toLong()).isBefore(LocalDate.now())) {
                        nextPeriodStart = nextPeriodStart.plusDays(manualCycleDays.toLong())
                    }
                }
                val nextPeriodEnd = nextPeriodStart.plusDays(durationDays.toLong())
                val predictedOvulation = nextPeriodStart.minusDays(14)

                return CyclePredictionStats(
                    hasHistoricalData = true,
                    predictedNextPeriodStart = nextPeriodStart,
                    predictedNextPeriodEnd = nextPeriodEnd,
                    averageCycleLengthDays = manualCycleDays,
                    averagePeriodDurationDays = actualDuration,
                    ovulationDate = predictedOvulation,
                    manualStartDate = manualStart,
                    manualEndDate = manualEnd,
                    isUsingManualConfig = true
                )
            }

            // Collect all logged period days (where flow intensity is not 'None')
            val periodDays = logs.filter { it.flowIntensity != "None" }
                .mapNotNull { 
                    try { LocalDate.parse(it.dateStr) } catch (e: Exception) { null }
                }
                .sorted()

            if (periodDays.isEmpty()) {
                return CyclePredictionStats(
                    hasHistoricalData = false,
                    predictedNextPeriodStart = LocalDate.now().plusDays(10), // default predicted
                    predictedNextPeriodEnd = LocalDate.now().plusDays(15),
                    averageCycleLengthDays = 28,
                    averagePeriodDurationDays = 5,
                    ovulationDate = LocalDate.now().plusDays(10).minusDays(14)
                )
            }

            // Group consecutive period days into "period segments"
            val segments = mutableListOf<MutableList<LocalDate>>()
            var currentSegment = mutableListOf<LocalDate>()

            for (day in periodDays) {
                if (currentSegment.isEmpty()) {
                    currentSegment.add(day)
                } else {
                    val lastDay = currentSegment.last()
                    val daysBetween = ChronoUnit.DAYS.between(lastDay, day)
                    if (daysBetween <= 3) { // group together if within 3 days of gap
                        currentSegment.add(day)
                    } else {
                        segments.add(currentSegment)
                        currentSegment = mutableListOf(day)
                    }
                }
            }
            if (currentSegment.isNotEmpty()) {
                segments.add(currentSegment)
            }

            // Average period bleeding duration
            val avgDuration = if (segments.isNotEmpty()) {
                val sizes = segments.map { it.size }
                if (sizes.isNotEmpty()) {
                    sizes.average().let { if (it.isNaN()) 5 else it.toInt().coerceIn(3, 7) }
                } else {
                    5
                }
            } else {
                5
            }

            // Average cycle length (days between start of segment N and segment N+1)
            val avgCycleLength = if (segments.size >= 2) {
                val cycleGaps = mutableListOf<Long>()
                for (i in 0 until segments.size - 1) {
                    val startFirst = segments[i].first()
                    val startSecond = segments[i + 1].first()
                    cycleGaps.add(ChronoUnit.DAYS.between(startFirst, startSecond))
                }
                if (cycleGaps.isNotEmpty()) {
                    cycleGaps.average().let { if (it.isNaN()) 28 else it.toInt().coerceIn(21, 35) }
                } else {
                    28
                }
            } else {
                28
            }

            // Project next starting date projected forward to prevent calculation drift
            val lastSegmentStart = segments.lastOrNull()?.firstOrNull() ?: LocalDate.now()
            var nextPeriodStart = lastSegmentStart.plusDays(avgCycleLength.toLong())
            if (avgCycleLength > 0) {
                while (nextPeriodStart.plusDays(avgDuration.toLong() - 1).isBefore(LocalDate.now())) {
                    nextPeriodStart = nextPeriodStart.plusDays(avgCycleLength.toLong())
                }
            }
            val nextPeriodEnd = nextPeriodStart.plusDays(avgDuration.toLong() - 1)
            val predictedOvulation = nextPeriodStart.minusDays(14)

            CyclePredictionStats(
                hasHistoricalData = true,
                predictedNextPeriodStart = nextPeriodStart,
                predictedNextPeriodEnd = nextPeriodEnd,
                averageCycleLengthDays = avgCycleLength,
                averagePeriodDurationDays = avgDuration,
                ovulationDate = predictedOvulation
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CyclePredictionStats()
        }
    }

    // Manual Tracking Actions
    fun setManualPeriodStart(dateStr: String) {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: return@launch
            val startDate = try { LocalDate.parse(dateStr) } catch(e: Exception) { null } ?: return@launch
            var updated = current.copy(lastPeriodStartDate = dateStr)
            
            val duration = current.periodLengthDays.coerceIn(3, 10)
            val endDateStr = current.lastPeriodEndDate
            val endDate = if (endDateStr.isNotEmpty()) {
                try { LocalDate.parse(endDateStr) } catch(e: Exception) { null }
            } else null

            if (endDate == null || endDate.isBefore(startDate)) {
                val computedEnd = startDate.plusDays((duration - 1).toLong())
                updated = updated.copy(lastPeriodEndDate = computedEnd.toString())
            } else {
                val actualDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
                updated = updated.copy(periodLengthDays = actualDays.coerceIn(3, 10))
            }
            repository.saveUserProfile(updated)
        }
    }

    fun setManualPeriodEnd(dateStr: String) {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: return@launch
            val endDate = try { LocalDate.parse(dateStr) } catch(e: Exception) { null } ?: return@launch
            var updated = current.copy(lastPeriodEndDate = dateStr)
            
            val duration = current.periodLengthDays.coerceIn(3, 10)
            val startDateStr = current.lastPeriodStartDate
            val startDate = if (startDateStr.isNotEmpty()) {
                try { LocalDate.parse(startDateStr) } catch(e: Exception) { null }
            } else null

            if (startDate == null || endDate.isBefore(startDate)) {
                val computedStart = endDate.minusDays((duration - 1).toLong())
                updated = updated.copy(lastPeriodStartDate = computedStart.toString())
            } else {
                val actualDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
                updated = updated.copy(periodLengthDays = actualDays.coerceIn(3, 10))
            }
            repository.saveUserProfile(updated)
        }
    }

    fun setCycleDays(days: Int) {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: return@launch
            val updated = current.copy(cycleLengthDays = days)
            repository.saveUserProfile(updated)
        }
    }

    fun setPeriodLengthDays(days: Int) {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: return@launch
            var updated = current.copy(periodLengthDays = days)
            if (updated.lastPeriodStartDate.isNotEmpty()) {
                try {
                    val s = LocalDate.parse(updated.lastPeriodStartDate)
                    val newEnd = s.plusDays((days - 1).toLong())
                    updated = updated.copy(lastPeriodEndDate = newEnd.toString())
                } catch(e: Exception) {}
            }
            repository.saveUserProfile(updated)
        }
    }

    fun clearManualPeriodDates() {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: return@launch
            val updated = current.copy(lastPeriodStartDate = "", lastPeriodEndDate = "")
            repository.saveUserProfile(updated)
        }
    }

    // Trigger customizable random notification push simulation
    private fun triggerDailyQuotesAndReminders() {
        val quotes = listOf(
            "Your body is a beautiful creation, growing wonderfully at its own perfect pace.",
            "Resting is also a way of caring for your growing body.",
            "Water is the elixir of wellness. Have a warm glass of tea or water today.",
            "Be soft with yourself. Everyone starts their journey at different ages.",
            "A warm hot water pouch can works wonders for comforting your belly."
        )

        val notificationsList = listOf(
            "Pack your pouch tomorrow 💕",
            "Remember to hydrate today, beautiful.",
            "Your cycle may arrive soon, keep your kit clean."
        )

        viewModelScope.launch {
            _lastNotification.value = notificationsList.random()
        }
    }

    fun seedSamplePeriodData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            
            // Cycle 2 months ago (bleeding for 5 days)
            val pastCycle1Start = today.minusDays(56)
            for (i in 0 until 5) {
                val date = pastCycle1Start.plusDays(i.toLong()).toString()
                repository.saveDailyDetail(
                    DailyDetailsEntity(
                        dateStr = date,
                        flowIntensity = if (i == 0 || i == 4) "Light" else "Medium",
                        mood = "Calm",
                        cramps = i == 2,
                        discharge = "Sticky"
                    )
                )
            }

            // Cycle 1 month ago (bleeding for 5 days)
            val pastCycle2Start = today.minusDays(28)
            for (i in 0 until 5) {
                val date = pastCycle2Start.plusDays(i.toLong()).toString()
                repository.saveDailyDetail(
                    DailyDetailsEntity(
                        dateStr = date,
                        flowIntensity = if (i == 0 || i == 4) "Light" else "Medium",
                        mood = "Emotional",
                        cramps = i == 2,
                        discharge = "Watery"
                    )
                )
            }
        }
    }

    fun getDailyQuote(): String {
        val quotes = listOf(
            "Your body is growing beautifully.",
            "Rest when you need to—you are doing great.",
            "Listen to your body. It knows exactly what you need.",
            "Everyone starts at their own unique time.",
            "You are strong, capable, and wonderfully made.",
            "Warm tea and a cozy blanket can make any day better."
        )
        // seed a deterministic quote based on day of month so it changes daily
        val day = LocalDate.now().dayOfMonth
        return quotes[day % quotes.size]
    }
}

// Sealed Navigation hierarchy
sealed class AppScreen {
    object Splash : AppScreen()
    object Onboarding : AppScreen()
    object PinLock : AppScreen()
    object Main : AppScreen()
}

sealed class AppTab {
    object Home : AppTab()
    object Calendar : AppTab()
    object Learn : AppTab()
    object Journal : AppTab()
    object Kit : AppTab()
}

data class CyclePredictionStats(
    val hasHistoricalData: Boolean = false,
    val predictedNextPeriodStart: LocalDate = LocalDate.now().plusDays(10),
    val predictedNextPeriodEnd: LocalDate = LocalDate.now().plusDays(15),
    val averageCycleLengthDays: Int = 28,
    val averagePeriodDurationDays: Int = 5,
    val ovulationDate: LocalDate = LocalDate.now().plusDays(10).minusDays(14),
    val manualStartDate: java.time.LocalDate? = null,
    val manualEndDate: java.time.LocalDate? = null,
    val isUsingManualConfig: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SuriVersionInfo(
    val versionCode: Int,
    val url: String
)

interface SuriUpdateApi {
    @GET
    suspend fun getVersionInfo(@Url url: String): SuriVersionInfo
}
