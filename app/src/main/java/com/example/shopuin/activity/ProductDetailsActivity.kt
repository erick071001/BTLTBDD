package com.example.shopuin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.ActivityProductDetailsBinding
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Products
import com.example.shopuin.utils.Constants

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityProductDetailsBinding
    private var mProductId: String = ""
    private lateinit var mProductDetails: Products
    private var mProductOwnerId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!

        }
        if (FirestoreClass().getCurrentUserId() == mProductOwnerId
        ) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        getProductDetails()
        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productExistInCart() {
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE

    }


    fun productDetailsSuccess(product: Products) {
        mProductDetails = product
        //hideProgressDialog()
        Glide.with(this)
            .load(product.image) // URI of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_placeholder) // Default placeholder if the image fails to load
            .into( binding.ivProductDetailImage)
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "₦${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
        binding.tvProductDetailsShippingCharge.text = "₦${product.shipping_charge}"

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )

            )
            binding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lb_out_of_stock)

        } else {
            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemInCart(this, mProductId)
            }
        }


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProductDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            "", "", mProductDetails.shipping_charge
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE


    }


    override fun onClick(v: View?) {
        v.let {
            when (it!!.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
//                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }
}