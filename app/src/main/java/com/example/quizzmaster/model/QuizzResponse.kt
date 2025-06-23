package com.example.quizzmaster.model

import com.google.gson.annotations.SerializedName

/**
 * Data class représentant une réponse de l'API.
 * 
 * @property responseCode Le code de réponse de l'API 0 = succès
 * @property results La liste des questions du quizz retournées par l'API
 */
data class QuizzResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results: List<Question>
)

/**
 * Data class représentant une question du quizz.
 * 
 * @property category La catégorie de la question
 * @property type Le type de question
 * @property difficulty Le niveau de difficulté de la question
 * @property question Le texte de la question
 * @property correctAnswer La réponse de la question
 * @property incorrectAnswers La liste des mauvaises réponses de la question
 */
data class Question(
    @SerializedName("category") val category: String,
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("question") val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)