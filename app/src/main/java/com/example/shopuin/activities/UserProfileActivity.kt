package com.example.shopuin.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.firebase.FirestoreClass
import com.example.shopuin.databinding.ActivityUserProfileBinding
import com.example.shopuin.models.User
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUser: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (intent.hasExtra("extra_user_details")){
            mUser = intent.getParcelableExtra("extra_user_details")!!
        }
        binding.etName.setText(mUser.name)
        binding.etAddress.setText(mUser.address)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUser.email)
        Glide.with(this)
            .load(mUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(binding.ivUserPhoto)
        if (mUser.mobile != 0L) {
            binding.etMobileNumber.setText(mUser.mobile.toString())

        }
        if (mUser.gender == "Nu") {
            binding.rbFemale.isChecked = true
        } else {
            binding.rbMale.isChecked = true
        }

        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        binding.btnUpdate.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_user_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser(this@UserProfileActivity)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                      2
                    )
                }
            }

            R.id.btn_update -> {
                showProgressDialog("Loading")
                if (mSelectedImageFileUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(
                        this,
                        mSelectedImageFileUri!!, "user_profile_image"
                    )

                } else {
                    updateUserProfileUserDetails()
                }

            }
        }
    }

    private fun updateUserProfileUserDetails() {
        val userHashMap = HashMap<String, Any>()
        val firstName = binding.etName.text.toString().trim { it <= ' ' }
        if (firstName != mUser.name) {
            userHashMap["name"] = firstName
        }
        val address = binding.etAddress.text.toString().trim { it <= ' ' }
        if (address != mUser.address) {
            userHashMap["address"] = address
        }
        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            "Nam"
        } else {
            "Nữ"
        }
        if (mUserProfileImageUrl.isNotEmpty()) {
            userHashMap["image"] = mUserProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUser.mobile.toString()) {
            userHashMap["mobile"] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUser.gender) {
            userHashMap["gender"] = gender
        }
        userHashMap["gender"] = gender
        userHashMap["profileCompleted"] = 1
        FirestoreClass().updateUserProfileData(this, userHashMap = userHashMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        Glide.with(this)
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(binding.ivUserPhoto)
                    } catch (e: IOException) {

                    }
                }
            }
        }

    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, 1)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        mUserProfileImageUrl = imageURL
        updateUserProfileUserDetails()
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        onBackPressed()
    }
}