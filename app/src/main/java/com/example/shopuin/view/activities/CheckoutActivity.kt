package com.example.shopuin.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopuin.R
import com.example.shopuin.view.adapter.CartListAdapter
import com.example.shopuin.controler.CartControler
import com.example.shopuin.controler.OrderControler
import com.example.shopuin.controler.ProductControler
import com.example.shopuin.controler.UserControler
import com.example.shopuin.databinding.ActivityCheckoutBinding
import com.example.shopuin.models.Address
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Order
import com.example.shopuin.models.Product

class CheckoutActivity : BaseActivity() {

    lateinit var binding: ActivityCheckoutBinding

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemList: ArrayList<CartItem>

    private var mSubTotal: Int = 0
    private var mTotalAmount: Int = 0

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
                "${mAddressDetails!!.address}, ${mAddressDetails!!.city}"

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
        val intent = Intent(this@CheckoutActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess(){
        MyToast.show(this, "Đặt hàng thành công", false)
        CartControler().updateAllDetails(this@CheckoutActivity, mCartItemList)
    }

    private fun placeAnOrder() {
        showProgressDialog("Loading")
        if (mAddressDetails != null){
           OrderControler().placeOrder(mCartItemList, mAddressDetails!!,mSubTotal,mTotalAmount,this)
        }



    }


    fun successProductsListsFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()
    }


    private fun getCartItemsList() {
        CartControler().getCartList(this@CheckoutActivity)
    }


    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        mCartItemList = CartControler().setStockQuatity(cartList,mProductList)
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartListItems.setHasFixedSize(true)

        binding.rvCartListItems.adapter = CartListAdapter(this, null,mCartItemList, false)
        mSubTotal = CartControler().subTotal(mCartItemList)
        binding.tvCheckoutSubTotal.text = "${mSubTotal}đ"
        binding.tvCheckoutShippingCharge.text = "30000đ"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 30000
            binding.tvCheckoutTotalAmount.text = mTotalAmount.toString() + "đ"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE

        }

    }


    private fun getProductList() {
        showProgressDialog("Loading")
       ProductControler().getAllProductsList(this@CheckoutActivity)
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

