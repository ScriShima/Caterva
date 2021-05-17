package com.example.caterva.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.caterva.R
import com.example.caterva.firebase.FireStoreClass
import com.example.caterva.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

@Suppress("DEPRECATION")
class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this,
                MainActivity::class.java))
        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser() {
        val email: String = et_email_signIn.text.toString().trim{it <=' '}
        val password: String = et_password_signIn.text.toString().trim{it <=' '}

        if(validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            FireStoreClass().loadUserData(this@SignInActivity)
                        }else {
                            Toast.makeText(
                                    baseContext,
                                    "Ошибка авторизации",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

}