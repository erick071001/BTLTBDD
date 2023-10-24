package com.example.shopuin.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.shopuin.R
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.ActivityLoginBinding
import com.example.shopuin.models.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val i = Intent(this@LoginActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        binding.tvRegister.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        view.let {
            when (it?.id) {
                R.id.tv_forgot_password -> {
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                }
                R.id.btn_login -> {
                    logInRegisteredUser()

                }
                R.id.tv_register -> {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
                else -> {

                }
            }
        }

    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập Email",
                    errorMessage = true
                )
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập mật khẩu",
                    errorMessage = true
                )
                false
            }
            else -> {
                true
            }
        }


    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {
            showProgressDialog("Loading")
            val email: String = binding.etEmail.text.toString().trim() { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim() { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().getUser(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(
                            task.exception!!.message.toString(),
                            true
                        )
                    }
                }
        }

    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    }
}