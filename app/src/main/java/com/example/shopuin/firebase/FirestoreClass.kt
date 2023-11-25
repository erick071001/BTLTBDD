package com.example.shopuin.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.example.shopuin.activities.*
import com.example.shopuin.controler.OrderControler
import com.example.shopuin.models.CartItem
import com.example.shopuin.models.Product
import com.example.shopuin.models.User
import com.example.shopuin.fragment.HomeFragment
import com.example.shopuin.fragment.MyCartFragment
import com.example.shopuin.fragment.OrdersFragment
import com.example.shopuin.models.Address
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
                val productList: ArrayList<Product> = ArrayList()
                for (item in document.documents) {
                    val allProduct = item.toObject(Product::class.java)!!
                    allProduct.product_id = item.id
                    productList.add(allProduct)
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
    fun getCartList(activity: CheckoutActivity) {
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id", getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, it.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()
                for (items in it.documents) {
                    val cartItem = items.toObject(CartItem::class.java)!!
                    cartItem.id = items.id
                    cartList.add(cartItem)
                }
                activity.successCartItemsList(cartList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }
    fun getAllProductsList(fragment: MyCartFragment) {
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener {
                val allProductList = ArrayList<Product>()
                for (items in it.documents) {
                    val product = items.toObject(Product::class.java)!!
                    product.product_id = items.id
                    allProductList.add(product)
                }
                fragment.successProductsListFromFireStore(allProductList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun getAllProductsList(activity: CheckoutActivity) {
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener {
                val allProductList = ArrayList<Product>()
                for (items in it.documents) {
                    val product = items.toObject(Product::class.java)!!
                    product.product_id = items.id
                    allProductList.add(product)
                }
                activity.successProductsListsFromFireStore(productList = allProductList)

            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }


    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener {
                val product = it.toObject(Product::class.java)
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

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFirestore.collection("addresses")
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating item from the cart list",
                    it
                )

            }

    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFirestore.collection("addresses")
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFirestore.collection("addresses")
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }

    }

    fun getAddressesList(activity: AddressListActivity) {
        mFirestore.collection("addresses")
            .whereEqualTo("user_id", getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val addressList: ArrayList<Address> = ArrayList()

                for (address in it.documents) {
                    val addressItem = address.toObject(Address::class.java)!!
                    addressItem.id = address.id

                    addressList.add(addressItem)

                }
                activity.successAddressListFromFirestore(addressList = addressList)


            }
            .addOnFailureListener {
                activity.hideProgressDialog()
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

    fun getMyOrdersList(orderControler: OrderControler,ordersFragment: OrdersFragment) {
        val ordersList: ArrayList<Order> = ArrayList()
        mFirestore.collection("orders")
            .whereEqualTo("user_id", getCurrentUserId())
            .get()
            .addOnSuccessListener {
                for (items in it.documents) {
                    val orderItem = items.toObject(Order::class.java)!!
                    orderItem.id = items.id
                    ordersList.add(orderItem)
                }
                orderControler.onOrdersReceived(ordersFragment,ordersList)
            }
            .addOnFailureListener {

            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFirestore.collection("orders")
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }


    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFirestore.batch()
        for (cartItem in cartList) {
            val productHashMap = HashMap<String, Any>()
            productHashMap["stock_quantity"] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()
            val documentReference = mFirestore.collection("products")
                .document(cartItem.product_id)
            writeBatch.update(documentReference, productHashMap)
        }
        for (cartItem in cartList) {
            val documentReference = mFirestore.collection("cart_items")
                .document(cartItem.id)
            writeBatch.delete(documentReference)

        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }


    }

    fun deleteOrders(orderControler: OrderControler, ordersFragment: OrdersFragment,userId: String) {
        mFirestore.collection("orders")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                    orderControler.onDeleteDone(ordersFragment)
            }
            .addOnFailureListener {
            }
    }



}