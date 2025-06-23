package com.example.quizzmaster.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.quizzmaster.service.QuizzService
import com.example.quizzmaster.util.TranslationUtil

/**
 * Data class representing a difficulty level.
 */
data class DifficultyItem(
    val id: String,
    val name: String,
    val color: Color,
    val description: String
)

/**
 * Screen for selecting a difficulty level.
 * 
 * @param onDifficultySelected Callback for when a difficulty is selected
 * @param onBackClick Callback for when the back button is clicked
 */
@Composable
fun DifficultySelectionScreen(
    onDifficultySelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // List of available difficulties
    val difficulties = listOf(
        DifficultyItem(
            id = QuizzService.DIFFICULTY_EASY,
            name = TranslationUtil.translateDifficulty(QuizzService.DIFFICULTY_EASY).replaceFirstChar { it.uppercase() },
            color = Color(0xFF4CAF50),
            description = "Questions simples pour tous les niveaux"
        ),
        DifficultyItem(
            id = QuizzService.DIFFICULTY_MEDIUM,
            name = TranslationUtil.translateDifficulty(QuizzService.DIFFICULTY_MEDIUM).replaceFirstChar { it.uppercase() },
            color = Color(0xFFFF9800),
            description = "Questions de difficulté intermédiaire"
        ),
        DifficultyItem(
            id = QuizzService.DIFFICULTY_HARD,
            name = TranslationUtil.translateDifficulty(QuizzService.DIFFICULTY_HARD).replaceFirstChar { it.uppercase() },
            color = Color(0xFFF44336),
            description = "Questions difficiles pour les experts"
        )
    )

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
                .padding(16.dp)
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Choisissez une difficulté",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Difficulty cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                difficulties.forEach { difficulty ->
                    DifficultyCard(
                        difficulty = difficulty,
                        onClick = { onDifficultySelected(difficulty.id) }
                    )
                }
            }
        }
    }
}

/**
 * Card representing a difficulty level.
 */
@Composable
fun DifficultyCard(
    difficulty: DifficultyItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = difficulty.color.copy(alpha = 0.8f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = difficulty.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = difficulty.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}