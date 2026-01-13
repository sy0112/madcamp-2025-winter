package com.example.androidlab

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = Firebase.auth.currentUser

            val intent = if (currentUser != null) {
                Intent(this, MainActivity::class.java)
            } else { Intent(this, LoginActivity::class.java) }

            startActivity(intent)
            finish()
        }, 500)
    }
}
