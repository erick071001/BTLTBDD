package com.example.shopuin.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.shopuin.R
import com.example.shopuin.controler.UserControler
import com.example.shopuin.databinding.ActivityAddEditAddressBinding
import com.example.shopuin.models.Address


class AddEditAddressActivity : BaseActivity() {

    lateinit var binding: ActivityAddEditAddressBinding
    private var mAddressDetails: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_add_edit_address)

        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        if (intent.hasExtra("AddressDetails")) {
            mAddressDetails = intent.getParcelableExtra("AddressDetails")
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                binding.tvTitle.text = "Cập nhật địa chỉ"
                binding.btnSubmitAddress.text = "Cập nhật"
                binding.etFullName.setText(mAddressDetails?.name)
                binding.etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                binding.etAddress.setText(mAddressDetails?.address)
                binding.etZipCode.setText(mAddressDetails?.city)
                binding.etAdditionalNote.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    "Home" -> {
                        binding.rbHome.isChecked = true
                    }

                    "Office" -> {
                        binding.rbOffice.isChecked = true
                    }

                    else -> {
                        binding.rbOther.isChecked = true

                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }

            }
        }


        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetails.visibility = View.GONE
            }

        }

        binding.btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
               "Cập nhật địa chỉ thành công"
            } else {
                "Thêm địa chỉ thành công"
            }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)

        finish()
    }

    private fun saveAddressToFirestore() {
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }

        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }

        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }

        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }

        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' ' }


        if (validateData()) {
            // show the progress dialog
            showProgressDialog("Loading")

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    "Home"
                }
                binding.rbOffice.isChecked -> {
                    "Office"
                }
                else -> {
                    "Other"
                }
            }

            val addressModel = Address(
                UserControler().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails,
                System.currentTimeMillis().toString()
            )

            // only update the address in the Firestore when it's not empty
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
               UserControler().updateAddress(
                    this,
                    addressModel, mAddressDetails!!.id
                )
            } else {
                UserControler().addAddress(this@AddEditAddressActivity, addressModel)

            }


        }


    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(

                binding.etFullName.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập họ và tên",
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(
                binding.etPhoneNumber.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập số điện thoại",
                    errorMessage = true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập địa chỉ",
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập thành phố",
                    errorMessage = true
                )
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    "Nhập nơi khác",
                    errorMessage = true
                )
                false
            }
            else -> {
                true
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddEditAddressActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }

    }
}