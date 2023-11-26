package com.example.shopuin.controler

import com.example.shopuin.view.activities.BaseActivity
import com.example.shopuin.view.activities.CheckoutActivity
import com.example.shopuin.view.activities.ProductDetailActivity
import com.example.shopuin.controler.firebase.FirestoreClass
import com.example.shopuin.view.fragment.MyCartFragment
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Product

class CartControler {

    fun checkIfItemInCart(activity: ProductDetailActivity, productId: String){
        FirestoreClass().checkIfItemInCart(this,activity, productId)
    }
    fun productExistInCart(activity: ProductDetailActivity){
        activity.productExistInCart()
    }
    fun getCartList(myCartFragment: MyCartFragment){
        FirestoreClass().getCartList(this,myCartFragment)
    }

    fun getCartList(activity: CheckoutActivity){
        FirestoreClass().getCartList(this,activity)
    }
    fun successCartItemsList(activity: CheckoutActivity, cartList: ArrayList<CartItem>){
        activity.successCartItemsList(cartList)
    }

    fun successCartItemsList(myCartFragment: MyCartFragment, cartList: ArrayList<CartItem>){
        myCartFragment.successCartItemsList(cartList)
    }

    fun failureCartItemsList(myCartFragment: MyCartFragment){
        myCartFragment.hideProgressDialog()
    }

    fun addCartItem(mProductOwnerId : String, mProductId : String, mProductDetails : Product, activity: ProductDetailActivity){
        val cartItem = CartItem(
            UserControler().getCurrentUserId(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            "1",
            mProductDetails.stock_quantity, ""
        )
        FirestoreClass().addCartItem(this,activity, cartItem)
    }

    fun addToCartSuccess(activity: ProductDetailActivity){
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
    fun allDetailsUpdatedSuccess(activity: CheckoutActivity){
        activity.allDetailsUpdatedSuccess()
    }
    fun setStockQuatity(cartList: ArrayList<CartItem>, mProductList: ArrayList<Product>) : ArrayList<CartItem>{
        var cartlist =cartList
        for (product in mProductList) {
            for (cartItem in cartlist) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        return cartlist
    }

    fun subTotal(mCartListItems: ArrayList<CartItem>) : Int {
        var subTotal = 0
        for (item in mCartListItems) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toInt()
                val quantity = item.cart_quantity.toInt()
                subTotal += (price * quantity)
            }
        }
        return  subTotal
    }
}