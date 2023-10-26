package com.example.shopuin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.activities.ProductDetailsActivity
import com.example.shopuin.databinding.ItemHomeBinding
import com.example.shopuin.fragment.HomeFragment
import com.example.shopuin.models.Products


class HomeListAdapter(
    private val context: Context,
    private val frm: HomeFragment,
    private var allProducts: ArrayList<Products>
) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var products: Products

        fun bind(products: Products) {
            this.products = products

            Glide.with(context)
                .load(products.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into( binding.ivDashboardItemImage)

            binding.tvDashboardItemTitle.text = products.title
            binding.tvDashboardItemPrice.text =
                "${products.price}$"
            binding.tvDashboardItemDescription.text =
                products.description

            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra("extra_product_id", products.product_id)
                frm.showDetail(intent)
            }

            animateView(itemView)

        }

        private fun animateView(viewToAnimate: View) {
            if (viewToAnimate.animation == null) {
                val animation = AnimationUtils.loadAnimation(
                    viewToAnimate.context, R.anim.scale_xy
                )
                viewToAnimate.animation = animation
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allProducts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = allProducts.size


}



