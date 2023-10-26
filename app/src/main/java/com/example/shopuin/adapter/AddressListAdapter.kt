package com.example.shopuin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopuin.activities.AddEditAddressActivity
import com.example.shopuin.activities.CheckoutActivity
import com.example.shopuin.databinding.ListItemAddressBinding
import com.example.shopuin.models.Address

class AddressListAdapter(
    private val context: Context,
    private var addressList: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ListItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var address: Address

        fun bind(address: Address) {
            this.address = address
            binding.tvAddressFullName.text = address.name
            binding.tvAddressType.text = address.type
            binding.tvAddressDetails.text =
                "${address.address}, ${address.zipCode}"
            binding.tvAddressMobileNumber.text =
                address.mobileNumber

            if (selectAddress) {
                itemView.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra("extra_selected_address", address)
                    context.startActivity(intent)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ListItemAddressBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)

    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra("AddressDetails", addressList[position])
        activity.startActivityForResult(intent, 121)
        notifyItemChanged(position)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    override fun getItemCount() = addressList.size
}