package com.example.videoplus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this, R.anim.zoom)
        val logo: ImageView = findViewById(R.id.logo_splashScreen)
        logo.animation = animation

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }, 1500)
    }
}