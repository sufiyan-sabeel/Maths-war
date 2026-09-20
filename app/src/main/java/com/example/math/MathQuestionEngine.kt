package com.example.math

import com.example.model.Difficulty
import com.example.model.MathConceptEffect
import com.example.model.MathQuestion
import com.example.model.MathTopic
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

object MathQuestionEngine {

    fun generateQuestion(
        topic: MathTopic? = null,
        difficulty: Difficulty = Difficulty.MEDIUM
    ): MathQuestion {
        val selectedTopic = topic ?: MathTopic.values().random()
        return when (selectedTopic) {
            MathTopic.ADDITION -> generateAddition(difficulty)
            MathTopic.SUBTRACTION -> generateSubtraction(difficulty)
            MathTopic.MULTIPLICATION -> generateMultiplication(difficulty)
            MathTopic.DIVISION -> generateDivision(difficulty)
            MathTopic.FRACTIONS -> generateFractions(difficulty)
            MathTopic.PERCENTAGES -> generatePercentages(difficulty)
            MathTopic.ORDER_OF_OPERATIONS -> generateOrderOfOperations(difficulty)
            MathTopic.BASIC_ALGEBRA -> generateBasicAlgebra(difficulty)
            MathTopic.LINEAR_EQUATIONS -> generateLinearEquations(difficulty)
            MathTopic.GEOMETRY -> generateGeometry(difficulty)
            MathTopic.COORDINATES -> generateCoordinates(difficulty)
        }
    }

    private fun generateAddition(difficulty: Difficulty): MathQuestion {
        val (a, b) = when (difficulty) {
            Difficulty.EASY -> Pair(Random.nextInt(5, 40), Random.nextInt(5, 40))
            Difficulty.MEDIUM -> Pair(Random.nextInt(25, 120), Random.nextInt(20, 95))
            Difficulty.HARD -> Pair(Random.nextInt(120, 850), Random.nextInt(90, 650))
            Difficulty.EXPERT -> Pair(Random.nextInt(850, 4500), Random.nextInt(750, 3900))
        }
        val ans = a + b
        val distractors = generateDistractors(ans, listOf(
            ans + 10,
            ans - 10,
            ans + 1,
            ans - 1,
            ans + Random.nextInt(2, 6) * if (Random.nextBoolean()) 1 else -1
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.ADDITION,
            difficulty = difficulty,
            prompt = "Compute the sum:",
            formulaDisplay = "$a + $b = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "$a + $b = $ans. Adding units and tens gives exact sum $ans.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.ADDITION
        )
    }

    private fun generateSubtraction(difficulty: Difficulty): MathQuestion {
        val (minVal, maxVal) = when (difficulty) {
            Difficulty.EASY -> Pair(15, 60)
            Difficulty.MEDIUM -> Pair(50, 200)
            Difficulty.HARD -> Pair(200, 900)
            Difficulty.EXPERT -> Pair(1000, 5000)
        }
        val a = Random.nextInt(minVal, maxVal)
        val b = Random.nextInt(5, a - 1)
        val ans = a - b
        val distractors = generateDistractors(ans, listOf(
            ans + 10,
            ans - 10,
            ans + 2,
            ans - 2,
            a + b // common confusion
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.SUBTRACTION,
            difficulty = difficulty,
            prompt = "Evaluate the difference:",
            formulaDisplay = "$a − $b = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "$a − $b = $ans. Subtracting $b from $a yields $ans.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.SUBTRACTION
        )
    }

    private fun generateMultiplication(difficulty: Difficulty): MathQuestion {
        val (a, b) = when (difficulty) {
            Difficulty.EASY -> Pair(Random.nextInt(2, 10), Random.nextInt(2, 10))
            Difficulty.MEDIUM -> Pair(Random.nextInt(6, 15), Random.nextInt(4, 13))
            Difficulty.HARD -> Pair(Random.nextInt(12, 28), Random.nextInt(7, 19))
            Difficulty.EXPERT -> Pair(Random.nextInt(25, 60), Random.nextInt(15, 45))
        }
        val ans = a * b
        val distractors = generateDistractors(ans, listOf(
            ans + a,
            ans - b,
            ans + 10,
            (a + 1) * b,
            a + b
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.MULTIPLICATION,
            difficulty = difficulty,
            prompt = "Solve the product:",
            formulaDisplay = "$a × $b = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "$a × $b = $ans. Multiplying $a by $b gives $ans.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.MULTIPLICATION
        )
    }

    private fun generateDivision(difficulty: Difficulty): MathQuestion {
        val (divisorRange, factorRange) = when (difficulty) {
            Difficulty.EASY -> Pair(2..9, 2..10)
            Difficulty.MEDIUM -> Pair(3..12, 5..18)
            Difficulty.HARD -> Pair(6..20, 11..35)
            Difficulty.EXPERT -> Pair(12..40, 20..65)
        }
        val b = divisorRange.random()
        val ans = factorRange.random()
        val a = b * ans
        val distractors = generateDistractors(ans, listOf(
            ans + 1,
            ans - 1,
            ans + 2,
            ans + b,
            max(1, ans - 2)
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.DIVISION,
            difficulty = difficulty,
            prompt = "Determine the quotient:",
            formulaDisplay = "$a ÷ $b = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "$a ÷ $b = $ans since $b × $ans = $a.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.DIVISION
        )
    }

    private fun generateFractions(difficulty: Difficulty): MathQuestion {
        val den = when (difficulty) {
            Difficulty.EASY -> listOf(2, 4, 8).random()
            Difficulty.MEDIUM -> listOf(3, 5, 6).random()
            Difficulty.HARD -> listOf(7, 9, 12).random()
            Difficulty.EXPERT -> listOf(11, 15, 16).random()
        }
        val n1 = Random.nextInt(1, den)
        val n2 = Random.nextInt(1, den)
        val sumN = n1 + n2
        val g = gcd(sumN, den)
        val simN = sumN / g
        val simD = den / g
        val correctStr = if (simD == 1) "$simN" else "$simN/$simD"

        val d1 = "$sumN/${den * 2}" // common mistake: adding denominators
        val d2 = "${abs(n1 - n2)}/$den"
        val d3 = "${sumN + 1}/$den"
        val distractors = listOf(d1, d2, d3).filter { it != correctStr }.distinct()
            .toMutableList()
        while (distractors.size < 3) {
            distractors.add("${sumN + distractors.size + 1}/$den")
        }

        val (choices, correctIdx) = shuffleChoices(correctStr, distractors.take(3))
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.FRACTIONS,
            difficulty = difficulty,
            prompt = "Add and simplify the fraction:",
            formulaDisplay = "$n1/$den + $n2/$den = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "Same denominator $den: ($n1 + $n2)/$den = $sumN/$den = $correctStr.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.DIVISION
        )
    }

    private fun generatePercentages(difficulty: Difficulty): MathQuestion {
        val pct = when (difficulty) {
            Difficulty.EASY -> listOf(10, 20, 25, 50).random()
            Difficulty.MEDIUM -> listOf(15, 30, 40, 75).random()
            Difficulty.HARD -> listOf(12, 35, 65, 80).random()
            Difficulty.EXPERT -> listOf(17, 33, 45, 85).random()
        }
        val base = when (difficulty) {
            Difficulty.EASY -> listOf(40, 60, 80, 100, 200).random()
            Difficulty.MEDIUM -> listOf(50, 80, 120, 160, 240).random()
            Difficulty.HARD -> listOf(75, 140, 180, 320, 450).random()
            Difficulty.EXPERT -> listOf(125, 260, 380, 540).random()
        }
        val ans = (pct * base) / 100
        val distractors = generateDistractors(ans, listOf(
            ans + 5,
            ans - 5,
            ans + (pct / 2),
            ans * 2,
            (pct * base) / 10 // missed 100 division
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.PERCENTAGES,
            difficulty = difficulty,
            prompt = "Calculate percentage value:",
            formulaDisplay = "$pct% of $base = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "$pct% × $base = ($pct/100) × $base = $ans.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.SQUARE_ROOT
        )
    }

    private fun generateOrderOfOperations(difficulty: Difficulty): MathQuestion {
        val a = Random.nextInt(2, 6)
        val b = Random.nextInt(2, 6)
        val c = Random.nextInt(2, 7)
        val (formula, ans, badAns) = when (difficulty) {
            Difficulty.EASY -> {
                // a + b * c
                Triple("$a + $b × $c", a + (b * c), (a + b) * c)
            }
            Difficulty.MEDIUM -> {
                // a * b - c * 2
                val d = Random.nextInt(1, 4)
                Triple("$a × $b − $c × $d", (a * b) - (c * d), a * (b - c) * d)
            }
            Difficulty.HARD -> {
                // (a + b)^2 - c
                val sum = a + b
                Triple("($a + $b)² − $c", (sum * sum) - c, (a * a) + (b * b) - c)
            }
            Difficulty.EXPERT -> {
                val sum = a + b
                val d = 2
                Triple("($a + $b)² ÷ $d + $c", ((sum * sum) / d) + c, (sum * sum) / (d + c))
            }
        }
        val distractors = generateDistractors(ans, listOf(
            badAns,
            ans + 2,
            ans - 2,
            ans + 5
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.ORDER_OF_OPERATIONS,
            difficulty = difficulty,
            prompt = "Evaluate using PEMDAS:",
            formulaDisplay = "$formula = ?",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "Follow PEMDAS (Parentheses, Exponents, Multiply/Divide, Add/Subtract): Result is $ans.",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.POWERS
        )
    }

    private fun generateBasicAlgebra(difficulty: Difficulty): MathQuestion {
        val x = Random.nextInt(2, 12)
        val (formula, ans, explanation) = when (difficulty) {
            Difficulty.EASY -> {
                val c = Random.nextInt(3, 15)
                val total = x + c
                Triple("x + $c = $total", x, "Subtract $c from both sides: x = $total − $c = $x.")
            }
            Difficulty.MEDIUM -> {
                val a = Random.nextInt(2, 6)
                val total = a * x
                Triple("${a}x = $total", x, "Divide both sides by $a: x = $total ÷ $a = $x.")
            }
            Difficulty.HARD -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(3, 12)
                val total = (a * x) + b
                Triple("${a}x + $b = $total", x, "Subtract $b: ${a}x = ${total - b}, then divide by $a: x = $x.")
            }
            Difficulty.EXPERT -> {
                val a = Random.nextInt(2, 5)
                val b = Random.nextInt(2, 6)
                val total = a * (x + b)
                Triple("$a(x + $b) = $total", x, "Divide by $a: x + $b = ${total / a}, subtract $b: x = $x.")
            }
        }
        val distractors = generateDistractors(ans, listOf(
            ans + 1,
            ans - 1,
            ans + 2,
            max(1, ans - 2),
            ans * 2
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.BASIC_ALGEBRA,
            difficulty = difficulty,
            prompt = "Solve for variable x:",
            formulaDisplay = formula,
            choices = choices,
            correctIndex = correctIdx,
            explanation = explanation,
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.EQUATION
        )
    }

    private fun generateLinearEquations(difficulty: Difficulty): MathQuestion {
        val x = Random.nextInt(2, 10)
        val m = Random.nextInt(2, 7)
        val b = Random.nextInt(1, 9)
        val y = (m * x) + b
        val prompt = "Find slope or value in line y = ${m}x + $b"
        val isSlopeQuestion = Random.nextBoolean()

        val (formula, ans, expl) = if (isSlopeQuestion) {
            Triple("y = ${m}x + $b  (What is slope m?)", m, "In slope-intercept form y = mx + b, slope m = $m.")
        } else {
            Triple("Given y = ${m}x + $b, find y when x = $x", y, "Substitute x = $x: y = ($m × $x) + $b = $y.")
        }
        val distractors = generateDistractors(ans, listOf(
            ans + 1,
            ans - 1,
            ans + m,
            max(1, ans - 2)
        ))
        val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.LINEAR_EQUATIONS,
            difficulty = difficulty,
            prompt = prompt,
            formulaDisplay = formula,
            choices = choices,
            correctIndex = correctIdx,
            explanation = expl,
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.EQUATION
        )
    }

    private fun generateGeometry(difficulty: Difficulty): MathQuestion {
        val shapes = listOf("rectangle", "triangle", "square", "pythagorean")
        val shape = shapes.random()
        return when (shape) {
            "rectangle" -> {
                val l = Random.nextInt(4, 12)
                val w = Random.nextInt(2, 8)
                val isArea = Random.nextBoolean()
                val ans = if (isArea) l * w else 2 * (l + w)
                val typeStr = if (isArea) "Area" else "Perimeter"
                val formula = if (isArea) "L = $l, W = $w → Area = ?" else "L = $l, W = $w → Perimeter = ?"
                val distractors = generateDistractors(ans, listOf(
                    if (isArea) 2 * (l + w) else l * w,
                    ans + 4,
                    ans - 4,
                    ans + 2
                ))
                val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    topic = MathTopic.GEOMETRY,
                    difficulty = difficulty,
                    prompt = "Find $typeStr of rectangle:",
                    formulaDisplay = formula,
                    choices = choices,
                    correctIndex = correctIdx,
                    explanation = if (isArea) "Area = L × W = $l × $w = $ans." else "Perimeter = 2(L + W) = 2($l + $w) = $ans.",
                    timeLimitSec = difficulty.baseTimeLimitSec,
                    conceptEffect = MathConceptEffect.SQUARE_ROOT
                )
            }
            "triangle" -> {
                val b = Random.nextInt(4, 14)
                val h = Random.nextInt(2, 10) * 2 // even for integer area
                val ans = (b * h) / 2
                val distractors = generateDistractors(ans, listOf(
                    b * h, // forgot divide by 2
                    ans + 2,
                    ans - 2,
                    ans + 5
                ))
                val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    topic = MathTopic.GEOMETRY,
                    difficulty = difficulty,
                    prompt = "Find Area of Triangle (Base = $b, Height = $h):",
                    formulaDisplay = "Area = ½ × $b × $h = ?",
                    choices = choices,
                    correctIndex = correctIdx,
                    explanation = "Triangle Area = ½ · base · height = ½ · $b · $h = $ans.",
                    timeLimitSec = difficulty.baseTimeLimitSec,
                    conceptEffect = MathConceptEffect.SQUARE_ROOT
                )
            }
            "pythagorean" -> {
                val triples = listOf(Pair(3, 4) to 5, Pair(6, 8) to 10, Pair(5, 12) to 13)
                val (legs, hyp) = triples.random()
                val (a, b) = legs
                val distractors = generateDistractors(hyp, listOf(hyp + 1, hyp - 1, a + b, hyp + 3))
                val (choices, correctIdx) = shuffleChoices(hyp.toString(), distractors.map { it.toString() })
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    topic = MathTopic.GEOMETRY,
                    difficulty = difficulty,
                    prompt = "Right triangle: Find hypotenuse c (legs $a and $b):",
                    formulaDisplay = "c² = $a² + $b² → c = ?",
                    choices = choices,
                    correctIndex = correctIdx,
                    explanation = "c = √($a² + $b²) = √(${a * a} + ${b * b}) = √${hyp * hyp} = $hyp.",
                    timeLimitSec = difficulty.baseTimeLimitSec,
                    conceptEffect = MathConceptEffect.POWERS
                )
            }
            else -> {
                val s = Random.nextInt(3, 12)
                val ans = s * s
                val distractors = generateDistractors(ans, listOf(4 * s, ans + 4, ans - 4, (s + 1) * (s + 1)))
                val (choices, correctIdx) = shuffleChoices(ans.toString(), distractors.map { it.toString() })
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    topic = MathTopic.GEOMETRY,
                    difficulty = difficulty,
                    prompt = "Find Area of Square with side length $s:",
                    formulaDisplay = "Area = $s² = ?",
                    choices = choices,
                    correctIndex = correctIdx,
                    explanation = "Square Area = side² = $s² = $ans.",
                    timeLimitSec = difficulty.baseTimeLimitSec,
                    conceptEffect = MathConceptEffect.POWERS
                )
            }
        }
    }

    private fun generateCoordinates(difficulty: Difficulty): MathQuestion {
        val x1 = Random.nextInt(-5, 6)
        val y1 = Random.nextInt(-5, 6)
        val dx = Random.nextInt(1, 5)
        val dy = Random.nextInt(1, 5)
        val x2 = x1 + (dx * 2)
        val y2 = y1 + (dy * 2)
        // Midpoint
        val midX = (x1 + x2) / 2
        val midY = (y1 + y2) / 2
        val ansStr = "($midX, $midY)"
        val distractors = listOf(
            "(${midX + 1}, $midY)",
            "($midX, ${midY - 1})",
            "(${midX - 1}, ${midY + 1})",
            "(${x1 + x2}, ${y1 + y2})"
        ).filter { it != ansStr }.distinct().take(3)

        val (choices, correctIdx) = shuffleChoices(ansStr, distractors)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = MathTopic.COORDINATES,
            difficulty = difficulty,
            prompt = "Find the Midpoint between A($x1, $y1) and B($x2, $y2):",
            formulaDisplay = "M = ((x₁+x₂)/2, (y₁+y₂)/2)",
            choices = choices,
            correctIndex = correctIdx,
            explanation = "Midpoint = (($x1 + $x2)/2, ($y1 + $y2)/2) = ($midX, $midY).",
            timeLimitSec = difficulty.baseTimeLimitSec,
            conceptEffect = MathConceptEffect.EQUATION
        )
    }

    private fun generateDistractors(correctVal: Int, candidates: List<Int>): List<Int> {
        val set = mutableSetOf<Int>()
        for (c in candidates) {
            if (c != correctVal && c >= 0) {
                set.add(c)
            }
            if (set.size == 3) break
        }
        var delta = 1
        while (set.size < 3) {
            val altPlus = correctVal + delta
            val altMinus = max(0, correctVal - delta)
            if (altPlus != correctVal) set.add(altPlus)
            if (set.size < 3 && altMinus != correctVal) set.add(altMinus)
            delta++
        }
        return set.toList()
    }

    private fun shuffleChoices(correct: String, distractors: List<String>): Pair<List<String>, Int> {
        val pool = (distractors.take(3) + correct).distinct().toMutableList()
        while (pool.size < 4) {
            pool.add("${correct.toIntOrNull()?.plus(pool.size) ?: (correct + "*")}")
        }
        val shuffled = pool.shuffled()
        val correctIndex = shuffled.indexOf(correct)
        return Pair(shuffled, correctIndex)
    }

    private fun gcd(a: Int, b: Int): Int {
        var n1 = abs(a)
        var n2 = abs(b)
        while (n2 != 0) {
            val t = n2
            n2 = n1 % n2
            n1 = t
        }
        return if (n1 == 0) 1 else n1
    }
}
