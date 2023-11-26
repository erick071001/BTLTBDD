package com.example.shopuin.view.activities

import android.os.Bundle
import com.example.shopuin.R
import com.example.shopuin.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarForgotPasswordActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }

        binding.btnSubmit.setOnClickListener {
            val email: String = binding.etEmail.text.toString().trim() { it <= ' ' }
            if (email.isEmpty()) {
                showErrorSnackBar("Vui lòng nhập email", true)
            } else {
                showProgressDialog("Loading")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            MyToast.show(
                                this,
                                "Vui lòng kiểm tra Email của bạn để khôi phục mật khẩu",
                                true
                            )
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }

        }
    }

}