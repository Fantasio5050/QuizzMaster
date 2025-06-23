package com.example.quizzmaster.view.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzmaster.R
import com.example.quizzmaster.data.ScoreEntry
import com.example.quizzmaster.data.ScoreRepository

/**
 * Scoreboard screen composable.
 * Displays the top scores with a podium for the top 3 players.
 * 
 * @param onBackClick Callback for when the back button is clicked
 */
@Composable
fun ScoreboardScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scoreRepository = remember { ScoreRepository(context) }
    var scores by remember { mutableStateOf<List<ScoreEntry>>(emptyList()) }
    
    // Load scores when the screen is first displayed
    LaunchedEffect(Unit) {
        scores = scoreRepository.getAllScores()
    }
    
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
                    text = "Tableau des Scores",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Podium for top 3 players
            if (scores.isNotEmpty()) {
                PodiumView(scores.take(3))
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // List of all scores
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tous les Scores",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Header row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rang",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Joueur",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = "Score",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Score list
                        LazyColumn {
                            items(scores) { scoreEntry ->
                                val index = scores.indexOf(scoreEntry) + 1
                                ScoreRow(index, scoreEntry)
                            }
                        }
                    }
                }
            } else {
                // No scores yet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun score enregistré",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Podium view for the top 3 players.
 */
@Composable
fun PodiumView(topScores: List<ScoreEntry>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Second place
            if (topScores.size >= 2) {
                PodiumPillar(
                    name = topScores[1].username,
                    score = topScores[1].score,
                    height = 140.dp,
                    position = 2,
                    color = Color(0xFFBDBDBD) // Silver
                )
            }
            
            // First place
            if (topScores.isNotEmpty()) {
                PodiumPillar(
                    name = topScores[0].username,
                    score = topScores[0].score,
                    height = 180.dp,
                    position = 1,
                    color = Color(0xFFFFD700) // Gold
                )
            }
            
            // Third place
            if (topScores.size >= 3) {
                PodiumPillar(
                    name = topScores[2].username,
                    score = topScores[2].score,
                    height = 100.dp,
                    position = 3,
                    color = Color(0xFFCD7F32) // Bronze
                )
            }
        }
    }
}

/**
 * Podium pillar for a player.
 */
@Composable
fun PodiumPillar(
    name: String,
    score: Int,
    height: androidx.compose.ui.unit.Dp,
    position: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player name and score
        Text(
            text = name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp)
        )
        
        Text(
            text = "$score",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Position badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$position",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Pillar
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color)
        )
    }
}

/**
 * Score row for the score list.
 */
@Composable
fun ScoreRow(index: Int, scoreEntry: ScoreEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = "$index",
            fontWeight = if (index <= 3) FontWeight.Bold else FontWeight.Normal,
            color = when (index) {
                1 -> Color(0xFFFFD700) // Gold
                2 -> Color(0xFFBDBDBD) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> Color.Black
            },
            modifier = Modifier.weight(1f)
        )
        
        // Player name
        Text(
            text = scoreEntry.username,
            fontWeight = if (index <= 3) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(2f)
        )
        
        // Score
        Text(
            text = "${scoreEntry.score}",
            fontWeight = if (index <= 3) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}