package com.example.quizzmaster.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.quizzmaster.data.ScoreRepository
import com.example.quizzmaster.view.screens.CategorySelectionScreen
import com.example.quizzmaster.view.screens.DifficultySelectionScreen
import com.example.quizzmaster.view.screens.GameScreen
import com.example.quizzmaster.view.screens.HomeScreen
import com.example.quizzmaster.view.screens.ScoreScreen
import com.example.quizzmaster.view.screens.ScoreboardScreen
import com.example.quizzmaster.viewmodel.GameState
import com.example.quizzmaster.viewmodel.QuizzViewModel

/**
 * Composant de navigation principal de l'app.
 * Gère la navuigation entre les écrans basé sur le state du jeu.
 */
@Composable
fun QuizzNavigation(viewModel: QuizzViewModel) {
    val gameState by viewModel.gameState.observeAsState(GameState.HOME)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current
    
    // Ecran de chargement
    if (isLoading) {
        com.example.quizzmaster.view.LoadingView()
        return
    }
    
    // Ecran d'erreur
    if (errorMessage != null) {
        ErrorScreen(errorMessage = errorMessage!!, onRetry = { viewModel.resetToHome() })
        return
    }
    
    // Navigateion selon state
    when (gameState) {
        GameState.HOME -> {
            HomeScreen(
                onPlayClick = { viewModel.goToCategorySelection() },
                onScoreboardClick = { viewModel.goToScoreboard() }
            )
        }
        GameState.CATEGORY_SELECTION -> {
            CategorySelectionScreen(
                onCategorySelected = { categoryId ->
                    viewModel.setCategory(categoryId)
                    viewModel.goToDifficultySelection()
                },
                onBackClick = { viewModel.resetToHome() }
            )
        }
        GameState.DIFFICULTY_SELECTION -> {
            DifficultySelectionScreen(
                onDifficultySelected = { difficulty ->
                    viewModel.setDifficulty(difficulty)
                    viewModel.startQuiz()
                },
                onBackClick = { viewModel.goToCategorySelection() }
            )
        }
        GameState.PLAYING -> {
            GameScreen(viewModel = viewModel)
        }
        GameState.FINISHED -> {
            ScoreScreen(
                score = viewModel.score.observeAsState(0).value,
                totalQuestions = 10,
                onSaveScore = { username ->
                    // Save score and go to scoreboard
                    saveUserScore(context, viewModel, username)
                    viewModel.goToScoreboard()
                },
                onPlayAgain = { viewModel.resetToHome() }
            )
        }
        GameState.SCOREBOARD -> {
            ScoreboardScreen(
                onBackClick = { viewModel.resetToHome() }
            )
        }
    }
}

/**
 * Sauvegarde le score de l'user.
 */
private fun saveUserScore(context: Context, viewModel: QuizzViewModel, username: String) {
    val scoreRepository = ScoreRepository(context)
    val score = viewModel.score.value ?: 0
    val categoryId = viewModel.selectedCategory.value
    val difficulty = viewModel.selectedDifficulty.value
    
    val categoryName = if (categoryId != null) {
        com.example.quizzmaster.util.TranslationUtil.getCategoryNameFromId(categoryId)
    } else {
        ""
    }
    
    val difficultyName = if (difficulty != null) {
        com.example.quizzmaster.util.TranslationUtil.translateDifficulty(difficulty)
    } else {
        ""
    }
    
    scoreRepository.saveScore(
        username = username,
        score = score,
        category = categoryName,
        difficulty = difficultyName
    )
}

/**
 * Ecran d'erreur.
 */
@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    com.example.quizzmaster.view.ErrorView(
        errorMessage = errorMessage,
        onRetry = onRetry
    )
}