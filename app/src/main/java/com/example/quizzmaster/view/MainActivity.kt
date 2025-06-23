package com.example.quizzmaster.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quizzmaster.R
import com.example.quizzmaster.model.Question
import com.example.quizzmaster.navigation.QuizzNavigation
import com.example.quizzmaster.ui.theme.QuizzMasterTheme
import com.example.quizzmaster.util.TranslationUtil
import com.example.quizzmaster.viewmodel.QuizzViewModel
import kotlinx.coroutines.launch
import android.widget.Toast

/**
 * Main activity for the Quizz application.
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: QuizzViewModel
    private var translatorInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[QuizzViewModel::class.java]

        // Initialize ML Kit Translator
        initializeTranslator()

        enableEdgeToEdge()
        setContent {
            QuizzMasterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Use the new navigation system
                    Box(modifier = Modifier.padding(innerPadding)) {
                        QuizzNavigation(viewModel = viewModel)
                    }
                }
            }
        }
    }

    /**
     * Initialise le traducteur ML Kit en téléchargeant les modèles nécessaires.
     */
    private fun initializeTranslator() {
        lifecycleScope.launch {
            try {
                val result = TranslationUtil.prepareTranslator(applicationContext)
                translatorInitialized = result
                if (!result) {
                    // Notification à l'utilisateur si le téléchargement a échoué
                    Toast.makeText(
                        this@MainActivity,
                        "Mode traduction hors-ligne activé (pas de connexion Wi-Fi)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // En cas d'erreur, on continue avec le traducteur de secours
                Toast.makeText(
                    this@MainActivity,
                    "Mode traduction hors-ligne activé (erreur: ${e.message})",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libérer les ressources du traducteur lors de la fermeture
        TranslationUtil.closeTranslator()
    }
}

/**
 * Composable function for the main quiz screen.
 */
@Composable
fun QuizzScreen(viewModel: QuizzViewModel, modifier: Modifier = Modifier) {
    // Observe ViewModel states
    val question by viewModel.currentQuestion.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    LoadingView()
                }
                errorMessage != null -> {
                    ErrorView(
                        errorMessage = errorMessage ?: "",
                        onRetry = { viewModel.fetchQuestion() }
                    )
                }
                question != null -> {
                    QuestionView(
                        question = question ?: Question("", "", "", "", "", emptyList()),
                        onAnswerSelected = {
                            // Wait a moment to show the answer result, then fetch a new question
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                viewModel.fetchQuestion()
                            }, 1500) // 1.5 seconds delay
                        }
                    )
                }
                else -> {
                    WelcomeView(
                        onStart = { viewModel.fetchQuestion() }
                    )
                }
            }
        }
    }
}

/**
 * Composable function for the loading screen.
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
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
            // Logo
            Image(
                painter = painterResource(id = R.drawable.quizzlogonobg),
                contentDescription = "Quizz Master Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp)
            )

            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = Color.White,
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading text
            Text(
                text = "Loading your quiz...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

/**
 * Composable function for displaying a quiz question.
 * 
 * @param question The question to display
 * @param onAnswerSelected Callback for when an answer is selected
 * @param modifier Modifier for styling
 */
@Composable
fun QuestionView(
    question: Question, 
    onAnswerSelected: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
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
            Spacer(modifier = Modifier.height(32.dp))

            // Category
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
            val allAnswers = remember {
                val answers = question.incorrectAnswers.toMutableList()
                answers.add(question.correctAnswer)
                answers.shuffled()
            }

            // Display answer options
            allAnswers.forEach { answer ->
                AnswerOption(
                    text = answer,
                    isCorrect = answer == question.correctAnswer,
                    onClick = { 
                        // Call the onAnswerSelected callback
                        onAnswerSelected()
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Difficulty indicator
            Text(
                text = "Difficulty: ${question.difficulty.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Composable function for displaying an answer option.
 */
@Composable
fun AnswerOption(
    text: String,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = remember { mutableStateOf(false) }

    Button(
        onClick = {
            selected.value = true
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected.value) {
                if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
            } else {
                Color(0xFF6200EE)
            }
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

/**
 * Composable function for displaying error messages.
 */
@Composable
fun ErrorView(errorMessage: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.quizzlogonobg),
                contentDescription = "Quizz Master Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            // Error message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Oops! Something went wrong",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Retry button
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "TRY AGAIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Composable function for the welcome screen.
 */
@Composable
fun WelcomeView(onStart: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.quizzlogonobg),
                contentDescription = "Quizz Master Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 24.dp)
            )

            // Welcome text
            Text(
                text = "Welcome to Quizz Master!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Start button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "START QUIZ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
