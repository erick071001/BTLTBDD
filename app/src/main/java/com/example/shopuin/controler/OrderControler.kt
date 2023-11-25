package com.example.shopuin.controler

import android.widget.Toast
import com.example.shopuin.activities.BaseActivity
import com.example.shopuin.activities.CheckoutActivity
import com.example.shopuin.firebase.FirestoreClass
import com.example.shopuin.fragment.OrdersFragment
import com.example.shopuin.models.Order

class OrderControler {

    fun placeOrder(activity: CheckoutActivity, order: Order){
        FirestoreClass().placeOrder(this,activity, order)
    }
    fun orderPlacedSuccess(activity:CheckoutActivity){
        activity.orderPlacedSuccess()
    }
    fun hideProgressDialog(activity:BaseActivity){
        activity.hideProgressDialog()
    }
    fun getMyOrdersList(ordersFragment: OrdersFragment){
        FirestoreClass().getMyOrdersList(this,ordersFragment)
    }
    fun onOrdersReceived(ordersFragment: OrdersFragment,ordersList: ArrayList<Order>){
        ordersFragment.populateOrdersListInUI(ordersList)
    }
    fun deleteOrder(ordersFragment: OrdersFragment,id : String) {
       FirestoreClass().deleteOrders(this,ordersFragment,id)
    }

    fun onDeleteDone(ordersFragment: OrdersFragment){
        ordersFragment.successDeleteAllOrders()
    }
}