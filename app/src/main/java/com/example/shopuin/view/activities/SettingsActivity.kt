package com.example.shopuin.view.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.controler.UserControler
import com.example.shopuin.databinding.ActivitySettingsBinding
import com.example.shopuin.models.User
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.tvEdit.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.callHotline.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        getUserDetails()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSettingsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog("Loading")
        UserControler().getUser(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra("extra_user_details", mUser)
                    startActivity(intent)

                }

                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.call_hotline -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val phoneNumber = "0839111372"
                        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                        startActivity(callIntent)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.CALL_PHONE),
                            2
                        )
                    }
                }
            }
        }
    }

    fun userDetailsSuccess(user: User) {
        mUser = user
        hideProgressDialog()
        Glide.with(this)
            .load(mUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(binding.ivUserPhoto)
        binding.tvName.text = "${user.name}"
        binding.tvAddress.text = "${user.address}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "0${user.mobile}"
    }
}