package com.example.shopuin.controler

import com.example.shopuin.view.activities.BaseActivity
import com.example.shopuin.view.activities.CheckoutActivity
import com.example.shopuin.view.activities.ProductDetailActivity
import com.example.shopuin.controler.firebase.FirestoreClass
import com.example.shopuin.view.fragment.HomeFragment
import com.example.shopuin.view.fragment.MyCartFragment
import com.example.shopuin.models.Product

class ProductControler {

    fun getProductDetails(activity: ProductDetailActivity, productId: String){
        FirestoreClass().getProductDetails(this,activity, productId)
    }
    fun productDetailsSuccess(activity: ProductDetailActivity, product: Product){
        activity.productDetailsSuccess(product)
    }
    fun getHomeItemsList(homeFragment: HomeFragment) {
        FirestoreClass().getHomeItemsList(this,homeFragment)
    }

    fun successHomeItemsList(homeFragment: HomeFragment, homeItemList: ArrayList<Product>){
        homeFragment.successHomeItemsList(homeItemList)
    }

    fun failureHomeItemsList(homeFragment: HomeFragment){
        homeFragment.hideProgressDialog()
    }

    fun getAllProductsList(myCartFragment: MyCartFragment){
        FirestoreClass().getAllProductsList(this,myCartFragment)
    }

    fun getAllProductsList(activity: CheckoutActivity){
        FirestoreClass().getAllProductsList(this,activity)
    }
    fun successProductsListsFromFireStore(activity: CheckoutActivity, productList: ArrayList<Product>){
        activity.successProductsListsFromFireStore(productList)
    }
    fun successProductsListFromFireStore(myCartFragment: MyCartFragment, productList: ArrayList<Product>){
        myCartFragment.successProductsListFromFireStore(productList)
    }

    fun hideProgressDialog(activity : BaseActivity){
        activity.hideProgressDialog()
    }
}