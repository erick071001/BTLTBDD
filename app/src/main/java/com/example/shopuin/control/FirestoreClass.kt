package com.example.shopuin.control

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.shopuin.activity.*
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Products
import com.example.shopuin.models.User
import com.example.shopuin.fragment.HomeFragment
import com.example.shopuin.fragment.MyCartFragment
import com.example.shopuin.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUser(fragment: MyCartFragment) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                fragment.returnUser(user)
            }
            .addOnFailureListener { e ->

            }
    }

    fun getUser(activity: Activity) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                val sharedPreferences = activity.getSharedPreferences(
                    "MyPalPrefs",
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    "logged_in_username",
                    "${user.name}"
                )
                editor.apply()
                when (activity) {
                    is LoginActivity -> activity.userLoggedInSuccess(user)
                    is SettingsActivity -> activity.userDetailsSuccess(user)
                }

            }
            .addOnFailureListener { e ->

            }
    }

    fun getHomeItemsList(fragment: HomeFragment) {
        mFirestore.collection("products")
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
        mFirestore.collection("users")
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
            }
    }

    fun getCartList(fragment: MyCartFragment) {
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id", getCurrentUserId())
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
        mFirestore.collection("products")
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
        mFirestore.collection("products")
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
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id", getCurrentUserId())
            .whereEqualTo("product_id", productId)
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
        mFirestore.collection("cart_items")
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun removedItemFromCart(fragment: MyCartFragment, mCartListItems: ArrayList<CartItem>) {
        for (item in mCartListItems) {
            mFirestore.collection("cart_items")
                .document(item.id)
                .delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                }
        }

    }

    fun removedItemFromCart(fragment: MyCartFragment, id: String) {
        mFirestore.collection("cart_items")
            .document(id)
            .delete()
            .addOnSuccessListener {
                fragment.itemRemovedSuccess()

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun updateMyCart(fragment: MyCartFragment, id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection("cart_items")
            .document(id)
            .update(itemHashMap)
            .addOnSuccessListener {
                fragment.itemUpdateSuccess()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun uploadImageToCloudStorage(
        activity: UserProfileActivity,
        mSelectedImageFileUri: Uri,
        imageType: String
    ) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "$imageType${System.currentTimeMillis()}.${
                activity.getFileExtension(
                    activity, mSelectedImageFileUri
                )
            }"
        )
        mSelectedImageFileUri.let {
            sRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        activity.imageUploadSuccess(uri.toString())
                    }
            }
        }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()

            }
    }

    fun updateUserProfileData(activity: UserProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.userProfileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

            }

    }


    fun createOrder(order: Order) {
        mFirestore.collection("order")
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }


}