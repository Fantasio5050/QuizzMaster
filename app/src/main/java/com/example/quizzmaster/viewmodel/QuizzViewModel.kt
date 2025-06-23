package com.example.quizzmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzmaster.data.RetrofitInstance
import com.example.quizzmaster.model.Question
import com.example.quizzmaster.service.QuizzService
import com.example.quizzmaster.util.TranslationUtil
import kotlinx.coroutines.launch

/**
 * ViewModel for handling quiz-related business logic.
 */
class QuizzViewModel : ViewModel() {

    // Service for API calls
    private val quizzService = RetrofitInstance.retrofit.create(QuizzService::class.java)

    // LiveData for the current question
    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    // LiveData for all questions in the current quiz
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Current question index
    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    // Current score
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    // Selected category
    private val _selectedCategory = MutableLiveData<Int?>(null)
    val selectedCategory: LiveData<Int?> = _selectedCategory

    // Selected difficulty
    private val _selectedDifficulty = MutableLiveData<String?>(null)
    val selectedDifficulty: LiveData<String?> = _selectedDifficulty

    // Timer state
    private val _timeRemaining = MutableLiveData(15) // 15 seconds per question
    val timeRemaining: LiveData<Int> = _timeRemaining

    // Game state
    private val _gameState = MutableLiveData<GameState>(GameState.HOME)
    val gameState: LiveData<GameState> = _gameState

    /**
     * Sets the selected category.
     */
    fun setCategory(categoryId: Int?) {
        _selectedCategory.value = categoryId
    }

    /**
     * Sets the selected difficulty.
     */
    fun setDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }

    /**
     * Starts a new quiz with the selected category and difficulty.
     */
    fun startQuiz() {
        _gameState.value = GameState.PLAYING
        _currentQuestionIndex.value = 0
        _score.value = 0
        fetchQuestions()
    }

    /**
     * Fetches questions from the API based on selected category and difficulty.
     */
    private fun fetchQuestions() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = quizzService.getQuestions(
                    amount = 10,
                    category = _selectedCategory.value,
                    difficulty = _selectedDifficulty.value
                )

                if (response.responseCode == 0 && response.results.isNotEmpty()) {
                    val translatedQuestions = TranslationUtil.translateQuestions(response.results)
                    _questions.value = translatedQuestions
                    _currentQuestion.value = translatedQuestions[0]
                    _currentQuestionIndex.value = 0
                    _timeRemaining.value = 15 // Reset timer
                } else {
                    _errorMessage.value = "Erreur: Réponse invalide de l'API"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Moves to the next question.
     */
    fun nextQuestion() {
        val currentIndex = _currentQuestionIndex.value ?: 0
        val questions = _questions.value ?: return

        if (currentIndex < questions.size - 1) {
            _currentQuestionIndex.value = currentIndex + 1
            _currentQuestion.value = questions[currentIndex + 1]
            _timeRemaining.value = 15 // Reset timer
        } else {
            // End of quiz
            _gameState.value = GameState.FINISHED
        }
    }

    /**
     * Handles answer selection.
     * 
     * @param answer The selected answer
     * @return True if the answer is correct, false otherwise
     */
    fun selectAnswer(answer: String): Boolean {
        val currentQuestion = _currentQuestion.value ?: return false
        val isCorrect = answer == currentQuestion.correctAnswer

        if (isCorrect) {
            // Increment score
            _score.value = (_score.value ?: 0) + 1
        }

        return isCorrect
    }

    /**
     * Updates the timer.
     * 
     * @return True if time is up, false otherwise
     */
    fun updateTimer(): Boolean {
        val currentTime = _timeRemaining.value ?: 0
        if (currentTime > 0) {
            _timeRemaining.value = currentTime - 1
            return false
        }
        return true // Time's up
    }

    /**
     * Resets the game state to HOME.
     */
    fun resetToHome() {
        _gameState.value = GameState.HOME
    }

    /**
     * Sets the game state to CATEGORY_SELECTION.
     */
    fun goToCategorySelection() {
        _gameState.value = GameState.CATEGORY_SELECTION
    }

    /**
     * Sets the game state to DIFFICULTY_SELECTION.
     */
    fun goToDifficultySelection() {
        _gameState.value = GameState.DIFFICULTY_SELECTION
    }

    /**
     * Sets the game state to SCOREBOARD.
     */
    fun goToScoreboard() {
        _gameState.value = GameState.SCOREBOARD
    }

    /**
     * Fetches a single question from the API (legacy method).
     */
    fun fetchQuestion() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = quizzService.getQuestions(amount = 1)

                if (response.responseCode == 0 && response.results.isNotEmpty()) {
                    // Translate question to French
                    _currentQuestion.value = TranslationUtil.translateQuestion(response.results[0])
                } else {
                    _errorMessage.value = "Erreur: Réponse invalide de l'API"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Enum representing the different states of the game.
 */
enum class GameState {
    HOME,
    CATEGORY_SELECTION,
    DIFFICULTY_SELECTION,
    PLAYING,
    FINISHED,
    SCOREBOARD
}
