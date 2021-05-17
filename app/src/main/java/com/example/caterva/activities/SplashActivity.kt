package com.example.caterva.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.caterva.R
import com.example.caterva.firebase.FireStoreClass
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.FileDescriptor.out
import java.lang.System.out

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        Handler().postDelayed({

            var currentUserId = FireStoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            }else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500)
    }
}