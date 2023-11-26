package com.example.shopuin.controler

import com.example.shopuin.view.activities.BaseActivity
import com.example.shopuin.view.activities.CheckoutActivity
import com.example.shopuin.controler.firebase.FirestoreClass
import com.example.shopuin.models.Address
import com.example.shopuin.models.CartItem
import com.example.shopuin.view.fragment.OrdersFragment
import com.example.shopuin.models.Order

class OrderControler {

    fun placeOrder(mCartItemList: ArrayList<CartItem>, mAddressDetails: Address,mSubTotal :Int,mTotalAmount : Int, activity: CheckoutActivity){
        val order = Order(
            UserControler().getCurrentUserId(),
            mCartItemList,
            mAddressDetails!!,
            "Đơn hàng ${System.currentTimeMillis()}",
            mCartItemList[0].image,
            mSubTotal.toString(),
            "30000",
            mTotalAmount.toString(),
            System.currentTimeMillis(),
            System.currentTimeMillis().toString()
            ,"No"
        )
        FirestoreClass().placeOrder(this,activity, order)
    }
    fun orderPlacedSuccess(activity: CheckoutActivity){
        activity.orderPlacedSuccess()
    }
    fun hideProgressDialog(activity: BaseActivity){
        activity.hideProgressDialog()
    }
    fun getMyOrdersList(ordersFragment: OrdersFragment){
        FirestoreClass().getMyOrdersList(this,ordersFragment)
    }
    fun onOrdersReceived(ordersFragment: OrdersFragment, ordersList: ArrayList<Order>){
        ordersFragment.populateOrdersListInUI(ordersList)
    }
    fun deleteOrder(ordersFragment: OrdersFragment, id : String) {
       FirestoreClass().deleteOrders(this,ordersFragment,id)
    }

    fun onDeleteDone(ordersFragment: OrdersFragment){
        ordersFragment.successDeleteAllOrders()
    }
}