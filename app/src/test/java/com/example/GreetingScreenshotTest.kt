package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.PlayerProfileEntity
import com.example.model.StickmanSkin
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun main_menu_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                MainMenuScreen(
                    profile = PlayerProfileEntity(level = 3, xp = 450, highScore = 12500),
                    selectedSkin = StickmanSkin.ALL_SKINS[0],
                    animationTick = 1.0f,
                    onNavigate = {},
                    onStartBattle = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/main_menu.png")
    }
}
