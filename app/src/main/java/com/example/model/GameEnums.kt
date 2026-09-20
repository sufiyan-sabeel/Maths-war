package com.example.model

/**
 * Game Modes available in MATH//BRAWL.
 */
enum class GameMode(val title: String, val description: String) {
    STORY("Story Mode", "Progress through the 5 mathematical worlds and vanquish their guardians"),
    WAVE("Wave Mode", "Endure relentless waves of scaling mathematical challenges"),
    PRACTICE("Practice Mode", "Master specific mathematical topics at your own pace with step-by-step guides"),
    ENDLESS("Endless Mode", "Battle non-stop against infinite equations to set the global high score"),
    BOSS_BATTLES("Boss Battles", "Face off directly against legendary multi-phase mathematical titans")
}

/**
 * Navigation screen routes.
 */
enum class GameScreen {
    MAIN_MENU,
    AUTH,
    LEVEL_SELECT,
    MODE_SELECT,
    WORLD_MAP,
    BATTLE,
    PRACTICE_CONFIG,
    BOSS_SELECT,
    CHARACTERS,
    ACHIEVEMENTS,
    LEADERBOARD,
    PROFILE,
    SETTINGS
}

/**
 * In-game achievement definition.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int = 0,
    val unlocked: Boolean = false,
    val rewardXp: Int = 100
) {
    companion object {
        val DEFAULT_ACHIEVEMENTS = listOf(
            Achievement("first_blood", "First Proof", "Solve your first mathematical challenge in combat", 1),
            Achievement("combo_3", "Calculated Precision", "Achieve a Combo of x3", 3),
            Achievement("combo_5", "Math Rage", "Unleash Math Rage with 5 consecutive correct answers", 5),
            Achievement("combo_10", "Equation Breaker", "Reach Combo x10 and trigger Equation Breaker", 10),
            Achievement("wave_5", "Wave Survivor", "Clear Wave 5 in Wave Mode", 5),
            Achievement("wave_10", "Algebraic Vanguard", "Clear Wave 10 in Wave Mode", 10),
            Achievement("boss_defeat", "Titan Tamer", "Defeat any Mathematical Boss", 1),
            Achievement("speed_solver", "Flash Solver", "Answer 10 questions in under 3 seconds each", 10),
            Achievement("flawless_world", "Master of Plains", "Clear World 1 with 3 stars on all stages", 15),
            Achievement("hundred_correct", "Century of Logic", "Solve 100 mathematical equations correctly", 100)
        )
    }
}
