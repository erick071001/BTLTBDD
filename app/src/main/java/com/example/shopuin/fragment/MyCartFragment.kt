package com.example.shopuin.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopuin.R
import com.example.shopuin.activities.AddressListActivity
import com.example.shopuin.activities.MyToast
import com.example.shopuin.adapter.CartListAdapter
import com.example.shopuin.controler.CartControler
import com.example.shopuin.controler.ProductControler
import com.example.shopuin.controler.UserControler
import com.example.shopuin.databinding.FragmentMycartBinding
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Product
import com.example.shopuin.models.User


class MyCartFragment : BaseFragment() {


    private lateinit var binding: FragmentMycartBinding
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>
    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMycartBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onStart() {
        super.onStart()
        UserControler().getUser(this)
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(context, AddressListActivity::class.java)
            intent.putExtra("extra_select_address", true)
            startActivity(intent)
        }
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        MyToast.show(context,"Xoá sản phẩm thành công",false)
        getCartItemsList()

    }

    private fun getCartItemsList() {
        showProgressDialog("Loading")
        CartControler().getCartList(this)

    }


    private fun getProductList() {
        ProductControler().getAllProductsList(this)
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()

    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartListItems = cartList
        val fragment = this
        if (mCartListItems.size > 0) {
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE
            binding.llCheckout.visibility = View.VISIBLE
            with(this.binding.rvCartItemsList) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                val cartListAdapter = CartListAdapter(
                    context,fragment,
                    cartListItems = mCartListItems, true
                )
                adapter = cartListAdapter
            }

            var subTotal: Double = 0.0
            var shippingCharge = 30000
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }

            }
            binding.tvSubTotal.text = "${subTotal}đ"
            binding.tvShippingCharge.text = "${shippingCharge}đ"
        } else {
            binding.rvCartItemsList.visibility = View.INVISIBLE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.INVISIBLE
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun returnUser(user: User) {
        mUser = user
    }
}