package com.example.quizzmaster.service

import com.example.quizzmaster.model.QuizzResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface définissant les différents endpoints de l'API.
 */
interface QuizzService {
    /**
     * Fetch les questions de l'API.
     * 
     * @param amount Le nombre de questions à récupérer
     * @param category l'ID de la catégorie des questions à récupérer (optionnel)
     * @param difficulty Le niveau de difficulté des questions à récupérer (optionnel: "easy", "medium", "hard")
     * @param type Le type de question (optionnel: "multiple", "boolean")
     * @return Un objet QuizzResponse contenant les questions
     */
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null
    ): QuizzResponse

    companion object {
        // IDs de catégories de l'API (liste non exhaustive)
        const val CATEGORY_GENERAL_KNOWLEDGE = 9
        const val CATEGORY_BOOKS = 10
        const val CATEGORY_FILM = 11
        const val CATEGORY_MUSIC = 12
        const val CATEGORY_TELEVISION = 14
        const val CATEGORY_VIDEO_GAMES = 15
        const val CATEGORY_SCIENCE_NATURE = 17
        const val CATEGORY_COMPUTERS = 18
        const val CATEGORY_MATHEMATICS = 19
        const val CATEGORY_SPORTS = 21
        const val CATEGORY_GEOGRAPHY = 22
        const val CATEGORY_HISTORY = 23
        const val CATEGORY_POLITICS = 24
        const val CATEGORY_ART = 25
        const val CATEGORY_CELEBRITIES = 26
        const val CATEGORY_ANIMALS = 27

        // Niveaux de difficulté
        const val DIFFICULTY_EASY = "easy"
        const val DIFFICULTY_MEDIUM = "medium"
        const val DIFFICULTY_HARD = "hard"

        // Types de question
        const val TYPE_MULTIPLE = "multiple"
        const val TYPE_BOOLEAN = "boolean"
    }
}
