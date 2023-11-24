package com.example.shopuin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopuin.activities.OrderDetailsActivity
import com.example.shopuin.databinding.ItemOderBinding
import com.example.shopuin.fragment.OrdersFragment
import com.example.shopuin.models.Order


class OrdersListAdapter(
    private val context: Context,
    private val ordersList: ArrayList<Order>,
    private val fragment: OrdersFragment

) :
    RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding:ItemOderBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var orders: Order
        fun bind(orders: Order) {
            this.orders = orders
            Glide.with(context)
                .load(orders.image)
                .centerCrop()
                .into(binding.ivItemImage)

            binding.tvItemName.text = orders.title
            binding.tvItemPrice.text = "₦${orders.total_amount}"
            binding.ibDeleteProduct.visibility = View.VISIBLE

            binding.ibDeleteProduct.setOnClickListener {
                fragment.deleteAllOrders(orders.id)
            }


            itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra("extra_my_order_details", orders)
                context.startActivity(intent)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOderBinding.inflate(LayoutInflater.from(context),
        parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ordersList[position])
    }

    override fun getItemCount() = ordersList.size
}