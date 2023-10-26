package com.example.shopuin.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopuin.R
import com.example.shopuin.adapter.HomeListAdapter
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.FragmentHomeBinding
import com.example.shopuin.models.Products


class HomeFragment :  BaseFragment() {

    private lateinit var binding: FragmentHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        getDashboardItemList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    fun successDashboardItemsList(dashboardItemList: ArrayList<Products>) {
        hideProgressDialog()
        binding.let {
            if (dashboardItemList.size > 0) {
                val layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                binding.rvHomeItems.layoutManager = layoutManager

                binding.rvHomeItems.setHasFixedSize(true)
                val allProductsAdapter = HomeListAdapter(requireActivity(),this, dashboardItemList)
                binding.rvHomeItems.adapter = allProductsAdapter

            } else {
                binding.rvHomeItems.visibility = View.GONE
            }
        }

    }

    private fun getDashboardItemList() {
        showProgressDialog("Loading")
        FirestoreClass().getHomeItemsList(this@HomeFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun showDetail(intent: Intent) {
        startActivityForResult(intent,1)
    }
}