package com.example.model

/**
 * Supported mathematics topics across all game modes.
 */
enum class MathTopic(val displayName: String, val symbol: String) {
    ADDITION("Addition", "+"),
    SUBTRACTION("Subtraction", "−"),
    MULTIPLICATION("Multiplication", "×"),
    DIVISION("Division", "÷"),
    FRACTIONS("Fractions", "½"),
    PERCENTAGES("Percentages", "%"),
    ORDER_OF_OPERATIONS("Order of Operations", "()²"),
    BASIC_ALGEBRA("Basic Algebra", "x+y"),
    LINEAR_EQUATIONS("Linear Equations", "ax=b"),
    GEOMETRY("Geometry", "△"),
    COORDINATES("Coordinates", "(x,y)")
}

/**
 * Question difficulty level.
 */
enum class Difficulty(val multiplier: Float, val baseTimeLimitSec: Int) {
    EASY(1.0f, 15),
    MEDIUM(1.5f, 12),
    HARD(2.0f, 9),
    EXPERT(3.0f, 7)
}

/**
 * Represents a programmatically generated mathematical question.
 */
data class MathQuestion(
    val id: String,
    val topic: MathTopic,
    val difficulty: Difficulty,
    val prompt: String,
    val formulaDisplay: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val timeLimitSec: Int,
    val conceptEffect: MathConceptEffect
)

/**
 * Special combat effect associated with mathematical concepts.
 */
enum class MathConceptEffect(
    val effectName: String,
    val symbol: String,
    val description: String
) {
    ADDITION("Energy Surge", "+", "Builds combo meter and boosts energy"),
    SUBTRACTION("Shield Piercer", "−", "Slices enemy shields"),
    MULTIPLICATION("Multi-Impact", "×", "Launches a volley of mathematical strikes"),
    DIVISION("Disruptor Wave", "÷", "Splits incoming hazards and weakens enemy power"),
    POWERS("Exponential Blast", "x²", "Deploys an explosive area shockwave"),
    SQUARE_ROOT("Root Breaker", "√", "Pierces dense defenses instantly"),
    EQUATION("Equation Breaker", "=", "Unleashes the ultimate mathematical formula beam")
}
