package com.example.shopuin.fragment

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopuin.R
import com.example.shopuin.adapter.HomeListAdapter
import com.example.shopuin.control.FirestoreClass
import com.example.shopuin.databinding.FragmentHomeBinding
import com.example.shopuin.models.Products


class HomeFragment :  BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    fun successDashboardItemsList(dashboardItemList: ArrayList<Products>) {
        hideProgressDialog()
         binding?.let {
            if (dashboardItemList.size > 0) {
                binding!!.rvDashboardItems.visibility = View.VISIBLE
                val layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                layoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return if((position + 1) % 5 == 0) 2 else 1
                    }
                }
                binding!!.rvDashboardItems.layoutManager = layoutManager

                binding!!.rvDashboardItems.setHasFixedSize(true)
                val allProductsAdapter = HomeListAdapter(requireActivity(), dashboardItemList)
                binding!!.rvDashboardItems.adapter = allProductsAdapter

            } else {
                binding!!.rvDashboardItems.visibility = View.GONE
            }
        }




    }

    private fun getDashboardItemList() {
        showProgressDialog("Loading")
        FirestoreClass().getHomeItemsList(this@HomeFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}