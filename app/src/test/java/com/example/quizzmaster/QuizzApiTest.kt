package com.example.quizzmaster

import org.junit.Test
import org.junit.Assert.*

/**
 * Simple test to verify the API configuration.
 */
class QuizzApiTest {

    @Test
    fun testApiConfiguration() {
        // Verify the API base URL
        val expectedBaseUrl = "https://opentdb.com/"
        val actualBaseUrl = com.example.quizzmaster.data.RetrofitInstance.javaClass.getDeclaredField("BASE_URL").apply {
            isAccessible = true
        }.get(null) as String

        assertEquals("API base URL should be correct", expectedBaseUrl, actualBaseUrl)

        println("[DEBUG_LOG] API base URL is correctly configured as: $actualBaseUrl")
        println("[DEBUG_LOG] The app is correctly configured to use the opentdb.com API")
    }
}
