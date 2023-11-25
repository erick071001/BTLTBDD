package com.example.shopuin.activities

import android.app.Activity
import android.content.Intent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopuin.R
import com.example.shopuin.adapter.AddressListAdapter
import com.example.shopuin.firebase.FirestoreClass
import com.example.shopuin.databinding.ActivityAddressListBinding
import com.example.shopuin.models.Address
import com.example.shopuin.utils.SwipeToDeleteCallback
import com.example.shopuin.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    lateinit var binding: ActivityAddressListBinding

    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_address_list)

        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        getAddressList()

        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, 121)

        }

        if (intent.hasExtra("extra_select_address")) {
            mSelectAddress = intent.getBooleanExtra("extra_select_address", false)
        }

        if (mSelectAddress) {
            binding.tvTitle.text = "Select Address"
            binding.tvAddAddress.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

    /* override fun onResume() {
         super.onResume()
         getAddressList()
     }*/

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "Address deleted successfully", Toast.LENGTH_SHORT
        ).show()
        getAddressList()

    }

    private fun getAddressList() {
        showProgressDialog("Loading")
        FirestoreClass().getAddressesList(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()
        if (addressList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE


            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)
            binding.rvAddressList.adapter =
                AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)


            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog("Loading")

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )

                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

            }


        } else {
            //hideProgressDialog()
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }

    }
}