package com.example.quizzmaster.view.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzmaster.R
import com.example.quizzmaster.model.Question
import com.example.quizzmaster.viewmodel.QuizzViewModel
import kotlinx.coroutines.delay

/**
 * Game screen composable.
 * Displays quiz questions with a timer and handles user answers.
 * 
 * @param viewModel The QuizzViewModel
 */
@Composable
fun GameScreen(viewModel: QuizzViewModel) {
    val currentQuestion by viewModel.currentQuestion.observeAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.observeAsState(0)
    val score by viewModel.score.observeAsState(0)
    val timeRemaining by viewModel.timeRemaining.observeAsState(15)

    // State for tracking if an answer has been selected
    var answerSelected by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf(false) }

    // Timer animation
    val progress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / 15f,
        animationSpec = tween(300),
        label = "timer"
    )

    // Timer effect
    LaunchedEffect(currentQuestion, answerSelected) {
        if (currentQuestion != null && !answerSelected) {
            while (true) {
                delay(1000) // 1 second delay
                if (viewModel.updateTimer()) {
                    // Time's up, move to next question
                    delay(1000) // Show time's up for 1 second
                    viewModel.nextQuestion()
                    break
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with back button, score and question counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(
                    onClick = { viewModel.resetToHome() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }

                // Score
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Score: $score",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Question counter
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Question: ${currentQuestionIndex + 1}/10",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Timer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        timeRemaining > 10 -> Color(0xFF4CAF50) // Green
                        timeRemaining > 5 -> Color(0xFFFF9800) // Orange
                        else -> Color(0xFFF44336) // Red
                    },
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }

            // Time remaining indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when {
                            timeRemaining > 10 -> Color(0xFF4CAF50) // Green
                            timeRemaining > 5 -> Color(0xFFFF9800) // Orange
                            else -> Color(0xFFF44336) // Red
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$timeRemaining",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category
            currentQuestion?.let { question ->
                Text(
                    text = question.category,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Answer options
                val allAnswers = remember(question) {
                    val answers = question.incorrectAnswers.toMutableList()
                    answers.add(question.correctAnswer)
                    answers.shuffled()
                }

                // Kahoot-style colors
                val kahootColors = listOf(
                    Color(0xFFEB1D36), // Red
                    Color(0xFFFFC107), // Yellow
                    Color(0xFF4CAF50), // Green
                    Color(0xFF2196F3)  // Blue
                )

                // Display answer options
                allAnswers.forEachIndexed { index, answer ->
                    AnswerButton(
                        text = answer,
                        isSelected = answer == selectedAnswer,
                        isCorrect = if (answerSelected) answer == question.correctAnswer else null,
                        color = kahootColors[index % kahootColors.size],
                        onClick = {
                            if (!answerSelected) {
                                selectedAnswer = answer
                                isCorrect = viewModel.selectAnswer(answer)
                                answerSelected = true

                                // Wait a moment to show the answer result, then fetch a new question
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    viewModel.nextQuestion()
                                    answerSelected = false
                                    selectedAnswer = ""
                                }, 1500) // 1.5 seconds delay
                            }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Answer button composable.
 */
@Composable
fun AnswerButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6200EE) // Default color if none provided
) {
    val backgroundColor = when {
        isCorrect == true -> Color(0xFF4CAF50) // Green for correct
        isCorrect == false && isSelected -> Color(0xFFF44336) // Red for incorrect
        isSelected -> color.copy(alpha = 0.7f) // Dimmed version of the button color when selected
        else -> color // Use the provided color
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
