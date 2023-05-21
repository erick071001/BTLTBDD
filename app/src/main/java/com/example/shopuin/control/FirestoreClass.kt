package com.example.shopuin.control

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Products
import com.example.shopuin.models.User
import com.example.shopuin.activity.LoginActivity
import com.example.shopuin.activity.ProductDetailsActivity
import com.example.shopuin.activity.RegisterActivity
import com.example.shopuin.fragment.HomeFragment
import com.example.shopuin.fragment.MyCartFragment
import com.example.shopuin.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        /* var currentUserID = ""
         currentUserID.let {
             currentUserID = currentUser!!.uid
         }*/
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
//                    is SettingsActivity -> {
//                        activity.userDetailsSuccess(user)
//                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
//                    is SettingsActivity -> {
//                        activity.hideProgressDialog()
//                    }
                }
            }
    }

    fun getHomeItemsList(fragment: HomeFragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.toString())
                val productList: ArrayList<Products> = ArrayList()
                for (item in document.documents) {
                    val allProducts = item.toObject(Products::class.java)!!
                    allProducts.product_id = item.id
                    productList.add(allProducts)
                }
                fragment.successDashboardItemsList(productList)
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun registerUser(activity: RegisterActivity, user: User) {
          mFirestore.collection(Constants.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
            }
    }

    fun getCartList(fragment: MyCartFragment) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.e(fragment.javaClass.simpleName, it.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()
                for (items in it.documents) {
                    val cartItem = items.toObject(CartItem::class.java)!!
                    cartItem.id = items.id
                    cartList.add(cartItem)
                }
                fragment.successCartItemsList(cartList)
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun getAllProductsList(fragment: MyCartFragment) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
                val allProductsList = ArrayList<Products>()
                for (items in it.documents) {
                    val product = items.toObject(Products::class.java)!!
                    product.product_id = items.id

                    allProductsList.add(product)
                }
                fragment.successProductsListFromFireStore(allProductsList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener {
                val product = it.toObject(Products::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun checkIfItemInCart(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener {
                activity.hideProgressDialog()

            }
    }

    fun addCartItems(activity: ProductDetailsActivity, cartItem: CartItem) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }

            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }


}