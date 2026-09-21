package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.model.StickmanSkin
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun main_menu_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                MainMenuScreen(
                    userProfile = com.example.data.firebase.UserProfile(
                        username = "Hero",
                        displayName = "Hero",
                        level = 3,
                        xp = 450,
                        totalScore = 12500L
                    ),
                    selectedSkin = StickmanSkin.ALL_SKINS[0],
                    animationTick = 1.0f,
                    onNavigate = {},
                    onStartBattle = {}
                )
            }
        }

        composeTestRule.onNodeWithText("MATHS WAR").assertIsDisplayed()
    }
}

