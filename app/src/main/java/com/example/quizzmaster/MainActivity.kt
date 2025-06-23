package com.example.quizzmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.quizzmaster.view.MainActivity as ViewMainActivity

/**
 * This MainActivity is not used in the application.
 * The actual MainActivity is in the view package.
 * 
 * This class is kept for reference only and redirects to the correct MainActivity.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to the actual MainActivity in the view package
        val intent = Intent(this, ViewMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
