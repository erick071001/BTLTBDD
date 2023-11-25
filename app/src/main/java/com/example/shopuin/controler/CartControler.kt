package com.example.shopuin.controler

import com.example.shopuin.activities.BaseActivity
import com.example.shopuin.activities.CheckoutActivity
import com.example.shopuin.activities.ProductDetailsActivity
import com.example.shopuin.firebase.FirestoreClass
import com.example.shopuin.fragment.MyCartFragment
import com.example.shopuin.models.CartItem

class CartControler {

    fun checkIfItemInCart(activity: ProductDetailsActivity, productId: String){
        FirestoreClass().checkIfItemInCart(this,activity, productId)
    }
    fun productExistInCart(activity: ProductDetailsActivity){
        activity.productExistInCart()
    }
    fun getCartList(myCartFragment: MyCartFragment){
        FirestoreClass().getCartList(this,myCartFragment)
    }

    fun getCartList(activity: CheckoutActivity){
        FirestoreClass().getCartList(this,activity)
    }
    fun successCartItemsList(activity: CheckoutActivity,cartList: ArrayList<CartItem>){
        activity.successCartItemsList(cartList)
    }

    fun successCartItemsList(myCartFragment: MyCartFragment,cartList: ArrayList<CartItem>){
        myCartFragment.successCartItemsList(cartList)
    }

    fun failureCartItemsList(myCartFragment: MyCartFragment){
        myCartFragment.hideProgressDialog()
    }

    fun addCartItems(activity: ProductDetailsActivity, cartItem: CartItem){
        FirestoreClass().addCartItems(this,activity, cartItem)
    }

    fun addToCartSuccess(activity: ProductDetailsActivity){
        activity.addToCartSuccess()
    }
    fun removedItemFromCart(fragment: MyCartFragment, id: String){
        FirestoreClass().removedItemFromCart(this,fragment!!, id)
    }

    fun itemRemovedSuccess(myCartFragment: MyCartFragment){
        myCartFragment.itemRemovedSuccess()
    }

    fun hideProgressDialog(fragment: MyCartFragment){
        fragment.hideProgressDialog()
    }
    fun hideProgressDialog(activity: BaseActivity){
        activity.hideProgressDialog()
    }

    fun updateMyCart(fragment: MyCartFragment, id: String, itemHashMap: HashMap<String, Any>){
        FirestoreClass().updateMyCart(this,fragment!!, id, itemHashMap)
    }

    fun itemUpdateSuccess(fragment: MyCartFragment){
        fragment.itemUpdateSuccess()
    }

    fun  updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>){
        FirestoreClass().updateAllDetails(this,activity, cartList)
    }
    fun allDetailsUpdatedSuccess(activity:CheckoutActivity){
        activity.allDetailsUpdatedSuccess()
    }
}