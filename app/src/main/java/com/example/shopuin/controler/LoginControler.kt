package com.example.shopuin.controler

import com.example.shopuin.view.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginControler {
    fun getCurrentUser() : FirebaseUser? {
        if (FirebaseAuth.getInstance().currentUser !=null) return FirebaseAuth.getInstance().currentUser
        else return null
    }
    fun signInWithEmailAndPassword(loginActivity: LoginActivity, email:String, password: String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginActivity.userLoggedInSuccess()
                } else {
                    loginActivity.hideProgressDialog()
                }
            }
    }
}