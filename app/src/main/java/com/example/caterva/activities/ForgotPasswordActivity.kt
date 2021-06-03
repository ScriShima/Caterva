package com.example.caterva.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.caterva.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_sign_in.*

@Suppress("DEPRECATION")
class ForgotPasswordActivity : BaseActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_reset_password.setOnClickListener {
            val email: String = et_email_reset_password.text.toString().trim{it <=' '}
            if (email.isNotEmpty()) {
                resetYourPassword(email)
            } else {
                validateForm(email)
            }
        }

        setupActionBar()
    }

    private fun resetYourPassword(email: String) {
        mAuth!!.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Письмо с инструкцией отправлено на вашу почту",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(
                        this,
                        SignInActivity::class.java
                    ))
                    finish()
                } else {

                    Toast.makeText(
                        this,
                        "Ошибка в отправке письма: $email",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun validateForm(email: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Пожалуйста введите email.")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_reset_password)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_sign_reset_password.setNavigationOnClickListener { onBackPressed() }
    }
}