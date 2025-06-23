package com.example.quizzmaster.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzmaster.R

/**
 * Result screen composable.
 * Displays the final score and allows the user to save their score.
 * 
 * @param score The final score
 * @param totalQuestions The total number of questions
 * @param onSaveScore Callback for when the user saves their score
 * @param onPlayAgain Callback for when the user wants to play again
 */
@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    onSaveScore: (String) -> Unit,
    onPlayAgain: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background1),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Result title
            Text(
                text = "Résultat Final",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Score display
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        color = when {
                            score >= totalQuestions * 0.8 -> Color(0xFF4CAF50) // Green for excellent
                            score >= totalQuestions * 0.5 -> Color(0xFFFF9800) // Orange for good
                            else -> Color(0xFFF44336) // Red for needs improvement
                        }.copy(alpha = 0.8f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$score",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp
                    )

                    Text(
                        text = "sur $totalQuestions",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Performance message
            val performanceMessage = when {
                score >= totalQuestions * 0.8 -> "Excellent! Vous êtes un maître du quiz!"
                score >= totalQuestions * 0.5 -> "Bien joué! Continuez à vous améliorer!"
                else -> "Continuez à pratiquer pour améliorer votre score!"
            }

            Text(
                text = performanceMessage,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username input
            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    showError = false
                },
                label = { Text("Entrez votre nom") },
                placeholder = { Text("Votre nom") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                // Use default colors with white text
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text(
                            text = "Veuillez entrer votre nom",
                            color = Color.Red
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save score button
            Button(
                onClick = {
                    if (username.isBlank()) {
                        showError = true
                    } else {
                        onSaveScore(username)
                    }
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "SAUVEGARDER LE SCORE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Play again button
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .width(250.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "REJOUER",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
