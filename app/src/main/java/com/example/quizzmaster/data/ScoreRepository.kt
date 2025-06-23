package com.example.quizzmaster.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

/**
 * Repository pour gérer les scores utilisateurs.
 * Utilise SharedPreferences pour persister le score.
 */
class ScoreRepository(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    /**
     * Enregistre le score pour un user donné.
     * 
     * @param username L'username du joueur
     * @param score Le score obtenu
     * @param category La catégorie du quizz choisi (optionnel)
     * @param difficulty Le niveau de difficulté choisi (optionnel)
     */
    fun saveScore(username: String, score: Int, category: String = "", difficulty: String = "") {
        val scoreEntry = ScoreEntry(
            username = username,
            score = score,
            category = category,
            difficulty = difficulty,
            timestamp = System.currentTimeMillis()
        )
        
        val scores = getAllScores().toMutableList()
        scores.add(scoreEntry)
        
        // Tri des scores par valeur DESC
        scores.sortByDescending { it.score }
        
        // Sauvegarde la liste
        val scoresJson = gson.toJson(scores)
        sharedPreferences.edit { putString(KEY_SCORES, scoresJson) }
    }
    
    /**
     * Retourne tous les scores.
     * 
     * @return La liste de tous les scores, triés par ordre décroissant
     */
    fun getAllScores(): List<ScoreEntry> {
        val scoresJson = sharedPreferences.getString(KEY_SCORES, null) ?: return emptyList()
        
        val type = object : TypeToken<List<ScoreEntry>>() {}.type
        return gson.fromJson(scoresJson, type) ?: emptyList()
    }
    
    /**
     * Retourne les meilleurs scores.
     * 
     * @param count Le nombre des meilleurs scores à récupérer
     * @return La liste des meilleurs scores
     */
    fun getTopScores(count: Int = 3): List<ScoreEntry> {
        return getAllScores().take(count)
    }
    
    /**
     * Supprime tous les scores.
     */
    fun clearScores() {
        sharedPreferences.edit().remove(KEY_SCORES).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "quizz_master_scores"
        private const val KEY_SCORES = "scores"
    }
}

/**
 * Data class d'un score.
 */
data class ScoreEntry(
    val username: String,
    val score: Int,
    val category: String = "",
    val difficulty: String = "",
    val timestamp: Long = 0
)