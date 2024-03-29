package com.example.shopuin.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopuin.R
import com.example.shopuin.view.adapter.HomeListAdapter
import com.example.shopuin.controler.ProductControler
import com.example.shopuin.databinding.FragmentHomeBinding
import com.example.shopuin.models.Product


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

    fun successHomeItemsList(homeItemList: ArrayList<Product>) {
        hideProgressDialog()
        binding.let {
            if (homeItemList.size > 0) {
                val layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                binding.rvHomeItems.layoutManager = layoutManager
                binding.rvHomeItems.setHasFixedSize(true)
                val allProductsAdapter = HomeListAdapter(requireActivity(),this, homeItemList)
                binding.rvHomeItems.adapter = allProductsAdapter

            } else {
                binding.rvHomeItems.visibility = View.GONE
            }
        }

    }

    private fun getDashboardItemList() {
        showProgressDialog("Loading")
        ProductControler().getHomeItemsList(this@HomeFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun showDetail(intent: Intent) {
        startActivityForResult(intent,1)
    }
}