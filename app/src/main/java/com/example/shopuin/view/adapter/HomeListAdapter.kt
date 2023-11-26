package com.example.shopuin.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopuin.R
import com.example.shopuin.view.activities.ProductDetailActivity
import com.example.shopuin.databinding.ItemHomeBinding
import com.example.shopuin.view.fragment.HomeFragment
import com.example.shopuin.models.Product


class HomeListAdapter(
    private val context: Context,
    private val frm: HomeFragment,
    private var allProducts: ArrayList<Product>
) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var product: Product

        fun bind(product: Product) {
            this.product = product

            Glide.with(context)
                .load(product.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(binding.ivDashboardItemImage)

            binding.tvDashboardItemTitle.text = product.title
            binding.tvDashboardItemPrice.text =
                "${product.price}đ"
            binding.tvDashboardItemDescription.text =
                product.description

            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("extra_product_id", product.product_id)
                intent.putExtra("extra_product_owner_id",product.user_id)
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



