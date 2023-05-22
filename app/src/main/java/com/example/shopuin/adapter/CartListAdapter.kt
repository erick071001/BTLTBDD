package com.example.shopuin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.activity.HomeActivity
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.ListItemCartBinding
import com.example.shopuin.fragment.MyCartFragment
import com.example.shopuin.models.CartItem


class CartListAdapter(
    private val context: Context,
    private val fragment : MyCartFragment,
    private var cartListItems: ArrayList<CartItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ListItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartListItems[position])

    }

    override fun getItemCount(): Int {

        return cartListItems.size
    }

    inner class ViewHolder(val binding: ListItemCartBinding) : View.OnClickListener,

        RecyclerView.ViewHolder(binding.root) {
        private lateinit var cartItem: CartItem

        fun bind(cartItem: CartItem) {
            this.cartItem = cartItem

            Glide.with(context)
                .load(cartItem.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into( binding.ivCartItemImage)

            binding.tvCartItemTitle.text = cartItem.title
            binding.tvCartItemPrice.text = "${cartItem.price}$"
            binding.tvCartQuantity.text = cartItem.cart_quantity


            binding.ibDeleteCartItem.setOnClickListener(this)
            binding.ibAddCartItem.setOnClickListener(this)
            binding.ibRemoveCartItem.setOnClickListener(this)

            if (cartItem.cart_quantity == "0") {
                binding.ibRemoveCartItem.visibility =
                    View.GONE
                binding.ibAddCartItem.visibility =
                    View.GONE


                if (updateCartItems) {
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE
                } else {
                    binding.ibDeleteCartItem
                        .visibility = View.GONE
                }
                binding.tvCartQuantity.text = "Hết hàng"
                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSnackBarError
                    )
                )

            } else {
                if (updateCartItems) {
                    binding.ibRemoveCartItem.visibility =
                        View.VISIBLE
                    binding.ibAddCartItem.visibility =
                        View.VISIBLE
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE

                } else {
                    binding.ibRemoveCartItem.visibility =
                        View.GONE
                    binding.ibAddCartItem.visibility =
                        View.GONE
                    binding.ibDeleteCartItem
                        .visibility = View.GONE
                }

                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSecondaryText
                    )
                )

            }

        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    R.id.ib_delete_cart_item -> {
                        FirestoreClass().removedItemFromCart(fragment, cartItem.id)
                    }
                    R.id.ib_remove_cart_item -> {
                        if (cartItem.cart_quantity == "1") {
                            FirestoreClass().removedItemFromCart(fragment, cartItem.id)
                        } else {
                            val cartQuantity: Int = cartItem.cart_quantity.toInt()
                            val itemHashMap = HashMap<String, Any>()
                            itemHashMap["cart_quantity"] = (cartQuantity - 1).toString()
                            FirestoreClass()
                                .updateMyCart(fragment, cartItem.id, itemHashMap)
                        }
                    }
                    R.id.ib_add_cart_item -> {
                        val cartQuantity: Int = cartItem.cart_quantity.toInt()

                        if (cartQuantity < cartItem.stock_quantity.toInt()) {
                            val itemHashMap = HashMap<String, Any>()
                            itemHashMap["cart_quantity"] = (cartQuantity + 1).toString()
                            FirestoreClass().updateMyCart(fragment, cartItem.id, itemHashMap)
                        }
                    }
                }
            }
        }
    }
}