package com.example.quizzmaster.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton d'instance Retrofit pour l'API Trivia Quizz.
 */
object RetrofitInstance {
    // URL API Trivia
    private const val BASE_URL = "https://opentdb.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}