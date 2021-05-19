package com.example.caterva.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.caterva.activities.*
import com.example.caterva.models.Board
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

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
                .document()
                .set(board, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,
                            "Board created successfully",
                    )

                    Toast.makeText(
                            activity,
                            "Доска успешно создана",
                            Toast.LENGTH_SHORT
                    ).show()

                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener {
                    exception ->
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName,
                    "Error while creating board", exception)

                    Toast.makeText(
                            activity,
                            "Ошибка при создании доски",
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                          userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,
                "Profile Data Updated")

                Toast.makeText(
                    activity,
                    "Профиль обновлен",
                    Toast.LENGTH_LONG
                ).show()

                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while creating a board", e)

                Toast.makeText(
                    activity,
                    "Ошибка при обновлении",
                    Toast.LENGTH_LONG
                ).show()
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