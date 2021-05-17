package com.example.caterva.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.caterva.R
import com.example.caterva.firebase.FireStoreClass
import com.example.caterva.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

@Suppress("DEPRECATION")
class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
                this,
                "Успешная регистрация",
                Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it<=' '}
        val email: String = et_email.text.toString().trim { it<=' '}
        val password: String = et_password.text.toString().trim { it<=' '}

        if(validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user = User(firebaseUser.uid, name, registeredEmail)

                            FireStoreClass().registerUser(this, user)
                        } else {
                            Toast.makeText(
                                    this,
                                    task.exception!!.message,
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
        }
    }

    private fun validateForm(name: String,
                             email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Пожалуйста введите имя")
                false
            }

            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Пожалуйста введите email")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Пожалуйста введите пароль")
                false
            }else -> {
                true
            }
        }
    }
}