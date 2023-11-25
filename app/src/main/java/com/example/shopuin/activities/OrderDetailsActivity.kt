package com.example.shopuin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopuin.R
import com.example.shopuin.adapter.CartListAdapter
import com.example.shopuin.databinding.ActivityOrderDetailsBinding
import com.example.shopuin.models.Order

import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        var myOrderDetails: Order = Order()

        if (intent.hasExtra("extra_my_order_details")) {
            myOrderDetails = intent.getParcelableExtra<Order>("extra_my_order_details")!!
        }


        setupUI(myOrderDetails)

    }


    private fun setupUI(orderDetails: Order) {
        binding.tvOrderDetailsId.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        binding.tvOrderDetailsDate.text = orderDateTime
        binding.tvOrderDetailsStatus.text = if (orderDetails.order_status == "No") "Đang xử lý" else "Đã xác nhận"


        binding.rvMyOrderItemsList.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
            setHasFixedSize(true)

            //reuse the cartAdapter
            adapter = CartListAdapter(
                this@OrderDetailsActivity,
                null,
                orderDetails.items, false
            )

        }
        binding.tvMyOrderDetailsAddressType.text = orderDetails.address.type
        binding.tvMyOrderDetailsFullName.text = orderDetails.address.name
        binding.tvMyOrderDetailsAddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.city}"

        binding.tvMyOrderDetailsAdditionalNote.text = orderDetails.address
            .additionalNote


        if (orderDetails.address.otherDetails.isNotEmpty()) {
            binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails

        } else {
            binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE

        }

        binding.tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber
        binding.tvOrderDetailsSubTotal.text = "${orderDetails.sub_total_amount}đ"
        binding.tvOrderDetailsShippingCharge.text = "${orderDetails.shipping_charge}đ"
        binding.tvOrderDetailsTotalAmount.text = "${orderDetails.total_amount}đ"


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }
}