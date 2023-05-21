package com.example.shopuin.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import com.example.shopuin.R
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.ActivityRegisterBinding


import com.example.shopuin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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

        setupActionBar()
        binding.tvLogin.setOnClickListener {
            /* startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
             finish()*/
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.etFirstName.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_first_name),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(
                binding.etLastName.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_last_name),
                    errorMessage = true
                )
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_email),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_password),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    errorMessage = true
                )
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    errorMessage = true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    errorMessage = true
                )
                false
            }
            else -> {
                /*showErrorSnackBar(
                    resources.getString(R.string.registration_successful),
                    errorMessage = false
                )*/
                true
            }
        }


    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = binding.etEmail.text.toString().trim() { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim() { it <= ' ' }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            binding.etFirstName.text.toString().trim() { it <= ' ' },
                            binding.etLastName.text.toString().trim() { it <= ' ' },
                            binding.etEmail.text.toString().trim() { it <= ' ' }

                        )
                        FirestoreClass().registerUser(this@RegisterActivity, user)

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        hideProgressDialog()


                    } else {
                        hideProgressDialog()
                        // If the registration is not successful then show error message
                        showErrorSnackBar(
                            task.exception!!.message.toString(),
                            true
                        )
                        Log.e("errror", email )
                        Log.e("errror", password )
                        Log.e("errror", task.exception!!.message.toString(), )
                    }
                }
        }
    }

    fun userRegistrationSuccess(){

        //Hide the progress dialog
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success), Toast.LENGTH_SHORT
        ).show()

    }



}