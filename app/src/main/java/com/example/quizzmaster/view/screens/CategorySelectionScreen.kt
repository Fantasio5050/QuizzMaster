package com.example.quizzmaster.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
 * Data class representing a quiz category.
 */
data class CategoryItem(
    val id: Int,
    val name: String,
    val color: Color
)

/**
 * Screen for selecting a quiz category.
 * 
 * @param onCategorySelected Callback for when a category is selected
 * @param onBackClick Callback for when the back button is clicked
 */
@Composable
fun CategorySelectionScreen(
    onCategorySelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    // List of available categories
    val categories = listOf(
        CategoryItem(
            id = QuizzService.CATEGORY_GENERAL_KNOWLEDGE,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_GENERAL_KNOWLEDGE),
            color = Color(0xFF6200EE)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_SCIENCE_NATURE,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_SCIENCE_NATURE),
            color = Color(0xFF03DAC5)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_COMPUTERS,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_COMPUTERS),
            color = Color(0xFF4CAF50)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_HISTORY,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_HISTORY),
            color = Color(0xFFF44336)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_GEOGRAPHY,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_GEOGRAPHY),
            color = Color(0xFF2196F3)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_SPORTS,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_SPORTS),
            color = Color(0xFFFF9800)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_FILM,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_FILM),
            color = Color(0xFF9C27B0)
        ),
        CategoryItem(
            id = QuizzService.CATEGORY_MUSIC,
            name = TranslationUtil.getCategoryNameFromId(QuizzService.CATEGORY_MUSIC),
            color = Color(0xFFE91E63)
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
                    text = "Choisissez une catégorie",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

/**
 * Card representing a quiz category.
 */
@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.8f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}