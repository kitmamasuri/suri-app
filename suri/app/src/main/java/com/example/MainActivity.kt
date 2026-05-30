package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.AppTab
import com.example.ui.PeriodPalViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.translation.Translations
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("CRASH_SURI", "Fatal crash on thread: ${thread.name}", throwable)
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTraceString = sw.toString()
            android.util.Log.e("CRASH_SURI_STACK", stackTraceString)
            System.err.println("--- SURI_FATAL_CRASH ---")
            System.err.println(stackTraceString)
            try {
                val logFile = java.io.File(filesDir, "suri_crash_log.txt")
                logFile.writeText("Thread: ${thread.name}\n\n$stackTraceString")
            } catch (ignored: Exception) {}
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
                java.lang.System.exit(10)
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val viewModel: PeriodPalViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val app = context.applicationContext as android.app.Application
                        return PeriodPalViewModel(app) as T
                    }
                }
            )
            val profileState by viewModel.userProfile.collectAsState()
            
            // Listen to selected theme
            val appTheme = profileState?.appTheme ?: "Blossom"

            MyApplicationTheme(themeName = appTheme) {
                val screenState by viewModel.currentScreen.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Crossfade(targetState = screenState, label = "ScreenTransition") { screen ->
                        when (screen) {
                            AppScreen.Splash -> SplashScreenView(onSplashComplete = {
                                // Check profile and PIN lock in VM synchronously to avoid flow race conditions
                                viewModel.initializeAppNavigation()
                            })
                            AppScreen.Onboarding -> OnboardingScreen(viewModel = viewModel)
                            AppScreen.PinLock -> PinLockScreen(viewModel = viewModel)
                            AppScreen.Main -> MainScreenFrame(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreenView(onSplashComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500) // Beautiful cinematic splash delay
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Soft Flower silhouette / logo representation
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PaleRose),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 48.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SURI",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    color = CocoaBrown,
                    letterSpacing = 4.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your safe, calming wellness companion",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SoftTaupe,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun MainScreenFrame(viewModel: PeriodPalViewModel) {
    val activeTab by viewModel.currentTab.collectAsState()
    val profileState by viewModel.userProfile.collectAsState()
    val showSettingsState by viewModel.showSettingsInJournal.collectAsState()
    val isEnglish = profileState?.appLanguage != "Bahasa Melayu"
    val isViewingSettings = (activeTab == AppTab.Journal && showSettingsState)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PaleRose),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌸", fontSize = 18.sp)
                    }
                    Text(
                        text = Translations.getString("app_title", isEnglish),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CocoaBrown,
                            letterSpacing = 1.sp
                        )
                    )
                }

                if (!isViewingSettings) {
                    IconButton(
                        onClick = {
                            viewModel.setShowSettingsInJournal(true)
                            viewModel.selectTab(AppTab.Journal)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PaleRose.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = DustyRose,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SoftCream,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars) // Strict Notch / Gesture protection
                    .fillMaxWidth()
            ) {
                val tabs = listOf(
                    Triple(AppTab.Home, Translations.getString("tab_home", isEnglish), Icons.Default.Home),
                    Triple(AppTab.Calendar, Translations.getString("tab_calendar", isEnglish), Icons.Default.CalendarMonth),
                    Triple(AppTab.Learn, Translations.getString("tab_learn", isEnglish), Icons.Default.School),
                    Triple(AppTab.Journal, Translations.getString("tab_journal", isEnglish), Icons.Default.AutoStories),
                    Triple(AppTab.Kit, Translations.getString("tab_kit", isEnglish), Icons.Default.LocalMall)
                )

                tabs.forEach { (tab, label, icon) ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { 
                            if (tab == AppTab.Journal) {
                                viewModel.setShowSettingsInJournal(false)
                            }
                            viewModel.selectTab(tab) 
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) DustyRose else SoftTaupe
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CocoaBrown else SoftTaupe
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PaleRose
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                AppTab.Home -> HomeScreen(viewModel = viewModel)
                AppTab.Calendar -> CalendarScreen(viewModel = viewModel)
                AppTab.Learn -> LearnScreen(viewModel = viewModel)
                AppTab.Journal -> JournalTabRouter(viewModel = viewModel)
                AppTab.Kit -> KitScreen(viewModel = viewModel)
            }
        }
    }
}

// Inner router to display either settings or journals for maximum feature coverage
@Composable
fun JournalTabRouter(viewModel: PeriodPalViewModel) {
    val showSettingsState by viewModel.showSettingsInJournal.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (showSettingsState) {
            SettingsScreen(viewModel = viewModel)
        } else {
            JournalScreen(viewModel = viewModel)
        }
    }
}
