package com.example.notesdailyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, go to HomePageActivity
            startActivity(Intent(this, HomePageActivity::class.java))
        } else {
            // No user is signed in, go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Finish SplashActivity
    }
}