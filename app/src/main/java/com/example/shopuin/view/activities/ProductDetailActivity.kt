package com.example.shopuin.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.controler.CartControler
import com.example.shopuin.controler.ProductControler
import com.example.shopuin.databinding.ActivityProductDetailsBinding
import com.example.shopuin.models.Product

class ProductDetailActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityProductDetailsBinding
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        if (intent.hasExtra("extra_product_id")) {
            mProductId = intent.getStringExtra("extra_product_id")!!
        }
        if (intent.hasExtra("extra_product_owner_id")) {
            mProductOwnerId = intent.getStringExtra("extra_product_owner_id")!!

        }

        binding.btnAddToCart.visibility = View.VISIBLE
        getProductDetails()
        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    private fun getProductDetails() {
        showProgressDialog("Loading")
        ProductControler().getProductDetails(this, mProductId)
    }

    fun productExistInCart() {
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE

    }


    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        //hideProgressDialog()
        Glide.with(this)
            .load(product.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into( binding.ivProductDetailImage)
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "${product.price}$"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity


        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailActivity,
                    R.color.colorSnackBarError
                )

            )
            binding.tvProductDetailsAvailableQuantity.text = "Không còn sản phẩm"

        } else {
                CartControler().checkIfItemInCart(this, mProductId)
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
        showProgressDialog("Loading")
        CartControler().addCartItem(mProductOwnerId,mProductId,mProductDetails,this)
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        MyToast.show(this, "Thêm thành công vào giỏ hàng", false)
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
                    val resultIntent = Intent()
                    if (  binding.btnGoToCart.visibility == View.VISIBLE) {
                        Log.e("onActivityResult: ", "selectedFragment.toString() ")
                        resultIntent.putExtra("selected_fragment", 2)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                  onBackPressed()
                }
            }
        }
    }

}