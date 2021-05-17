package com.example.caterva.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.caterva.R
import com.example.caterva.firebase.FireStoreClass
import com.example.caterva.models.User
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FireStoreClass().loadUserData(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.phoneNumber != 0L) {
            et_mobile.setText(user.phoneNumber.toString())
        }
    }
}