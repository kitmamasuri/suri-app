package com.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PinLockScreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { 
      MyApplicationTheme { 
        Text("Suri - Calming Wellness Space") 
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun splash_screen_composition() {
    composeTestRule.setContent { 
      MyApplicationTheme { 
        SplashScreenView(onSplashComplete = {})
      } 
    }
    composeTestRule.waitForIdle()
  }

  @Test
  fun test_main_activity_ui_composition() {
    val application = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.PeriodPalViewModel(application)
    composeTestRule.setContent {
      MyApplicationTheme(themeName = "Blossom") {
        androidx.compose.material3.Surface(
          modifier = androidx.compose.ui.Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          androidx.compose.animation.Crossfade(targetState = com.example.ui.AppScreen.Onboarding, label = "ScreenTransition") { screen ->
            when (screen) {
              com.example.ui.AppScreen.Splash -> SplashScreenView(onSplashComplete = {})
              com.example.ui.AppScreen.Onboarding -> OnboardingScreen(viewModel = viewModel)
              com.example.ui.AppScreen.PinLock -> PinLockScreen(viewModel = viewModel)
              com.example.ui.AppScreen.Main -> MainScreenFrame(viewModel = viewModel)
            }
          }
        }
      }
    }
    composeTestRule.waitForIdle()
  }

  @Test
  fun test_main_screen_frame_ui_composition() {
    val application = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.PeriodPalViewModel(application)
    composeTestRule.setContent {
      MyApplicationTheme(themeName = "Blossom") {
        androidx.compose.material3.Surface(
          modifier = androidx.compose.ui.Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          MainScreenFrame(viewModel = viewModel)
        }
      }
    }
    composeTestRule.waitForIdle()

    // 1. Switch to Calendar Tab
    viewModel.selectTab(com.example.ui.AppTab.Calendar)
    composeTestRule.waitForIdle()

    // 2. Switch to Learn Tab
    viewModel.selectTab(com.example.ui.AppTab.Learn)
    composeTestRule.waitForIdle()

    // 3. Switch to Journal Tab
    viewModel.selectTab(com.example.ui.AppTab.Journal)
    composeTestRule.waitForIdle()

    // 4. Toggle Settings Screen in Journal Tab
    viewModel.setShowSettingsInJournal(true)
    composeTestRule.waitForIdle()

    // 5. Switch to Kit Tab
    viewModel.selectTab(com.example.ui.AppTab.Kit)
    composeTestRule.waitForIdle()
  }
}
