package com.shree.pcpulse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    companion object {
        const val SPLASH_TIMEOUT: Long = 4000 // 4 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val logoImageView = findViewById<ImageView>(R.id.imgLogo)

//        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_fade_in)
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_scale_up)

        logoImageView.startAnimation(scaleAnimation)

        Handler().postDelayed({
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIMEOUT.toLong())

    }
}