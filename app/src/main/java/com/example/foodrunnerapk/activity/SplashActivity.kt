package com.example.foodrunnerapk.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.foodrunnerapk.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            val startAct= Intent(this@SplashActivity, LogInActivity::class.java)
            startActivity(startAct)
            finish()
        },1000)


    }
}