package com.example.caterva.firebase

import android.app.Activity
import android.util.Log
import com.example.caterva.activities.MainActivity
import com.example.caterva.activities.MyProfileActivity
import com.example.caterva.activities.SignInActivity
import com.example.caterva.activities.SignUpActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.caterva.models.User
import com.example.caterva.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class  FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }.addOnFailureListener {
                    e -> Log.e(activity.javaClass.simpleName, "Ошибка записи", e)
            }
    }

    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener {
                    e ->
                    when (activity) {
                        is SignInActivity -> {
                            activity.hideProgressDialog()
                        }
                        is MainActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e("SignInUser", "Ошибка записи", e)
            }
    }

    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}