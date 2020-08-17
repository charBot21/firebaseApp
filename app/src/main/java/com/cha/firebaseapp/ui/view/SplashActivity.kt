package com.cha.firebaseapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.firebaseapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val intentSplash = Intent(this, LoginActivity::class.java)
        startActivity(intentSplash)
        finish()
    }
}