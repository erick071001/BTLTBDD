package com.example.shopuin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopuin.R
import com.example.shopuin.adapter.CartListAdapter
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.ActivityCheckoutBinding
import com.example.shopuin.models.Address
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Order
import com.example.shopuin.models.Products

class CheckoutActivity : BaseActivity() {

    lateinit var binding: ActivityCheckoutBinding

    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Products>
    private lateinit var mCartItemList: ArrayList<CartItem>

    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_checkout)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra("extra_selected_address")) {
            mAddressDetails = intent.getParcelableExtra("extra_selected_address")

        }
        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type

            binding.tvCheckoutFullName.text = mAddressDetails?.name

            binding.tvCheckoutAddress.text =
                "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"

            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote


            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvMobileNumber.text = mAddressDetails?.mobileNumber


        }

        getProductList()

        binding.btnPlaceOrder.setOnClickListener{
            placeAnOrder()
        }

    }

    fun allDetailsUpdatedSuccess(){
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity, "Your order has been placed successfully",
            Toast.LENGTH_LONG).show()

        val intent = Intent(this@CheckoutActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess(){
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemList, mOrderDetails)
    }

    private fun placeAnOrder() {
        showProgressDialog("Loading")
        if (mAddressDetails != null){
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserId(),
                mCartItemList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                mSubTotal.toString(),
                mCartItemList[0].product_shipping_charge,
                mTotalAmount.toString(),
                System.currentTimeMillis(),
            )

            // create an orders collection from FireStore
            FirestoreClass().placeOrder(this, order = mOrderDetails)
        }



    }


    fun successProductsListsFromFireStore(productsList: ArrayList<Products>) {
        mProductsList = productsList
        getCartItemsList()
    }


    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }


    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemList = cartList
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartListItems.setHasFixedSize(true)

        binding.rvCartListItems.adapter = CartListAdapter(this, null,mCartItemList, false)


        var shippingCharge = 0

        for (item in mCartItemList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                shippingCharge = item.product_shipping_charge.toInt()

                mSubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = "₦$mSubTotal"
        binding.tvCheckoutShippingCharge.text = "₦$shippingCharge"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + shippingCharge
            binding.tvCheckoutTotalAmount.text = mTotalAmount.toString()
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE

        }

    }


    private fun getProductList() {
        showProgressDialog("Loading")
       FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }

    }
}

