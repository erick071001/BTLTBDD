package com.example.shopuin.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.example.shopuin.activities.*
import com.example.shopuin.controler.CartControler
import com.example.shopuin.controler.OrderControler
import com.example.shopuin.controler.ProductControler
import com.example.shopuin.controler.UserControler
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
    fun getUser(userControler: UserControler,fragment: MyCartFragment) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                userControler.returnUser(fragment,user)
            }
            .addOnFailureListener { e ->

            }
    }
    fun getUser(userControler: UserControler,activity: Activity) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                when (activity) {
                    is SettingsActivity -> userControler.userDetailsSuccess(activity,user)
                }

            }
            .addOnFailureListener { e ->

            }
    }
    fun getHomeItemsList(productControler: ProductControler,fragment: HomeFragment) {
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener { document ->
                val productList: ArrayList<Product> = ArrayList()
                for (item in document.documents) {
                    val allProduct = item.toObject(Product::class.java)!!
                    allProduct.product_id = item.id
                    productList.add(allProduct)
                }
                productControler.successHomeItemsList(fragment,productList)
            }
            .addOnFailureListener {
                productControler.failureHomeItemsList(fragment)
            }
    }
    fun registerUser(userControler: UserControler,activity: RegisterActivity, user: User) {
        mFirestore.collection("users")
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                userControler.userRegistrationSuccess(activity)
            }
            .addOnFailureListener { e ->
            }
    }
    fun getCartList(cartControler: CartControler,fragment: MyCartFragment) {
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
                cartControler.successCartItemsList(fragment,cartList)
            }
            .addOnFailureListener {
                cartControler.failureCartItemsList(fragment)
            }
    }
    fun getCartList(cartControler: CartControler,activity: CheckoutActivity) {
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
                cartControler.successCartItemsList(activity,cartList)
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(activity)
            }
    }
    fun getAllProductsList(productControler: ProductControler,fragment: MyCartFragment) {
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener {
                val allProductList = ArrayList<Product>()
                for (items in it.documents) {
                    val product = items.toObject(Product::class.java)!!
                    product.product_id = items.id
                    allProductList.add(product)
                }
                productControler.successProductsListFromFireStore(fragment,allProductList)
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }
    fun getAllProductsList(productControler: ProductControler,activity: CheckoutActivity) {
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener {
                val allProductList = ArrayList<Product>()
                for (items in it.documents) {
                    val product = items.toObject(Product::class.java)!!
                    product.product_id = items.id
                    allProductList.add(product)
                }
                productControler.successProductsListsFromFireStore(activity,productList = allProductList)

            }
            .addOnFailureListener {
                productControler.hideProgressDialog(activity)
            }
    }
    fun getProductDetails(productControler: ProductControler,activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener {
                val product = it.toObject(Product::class.java)
                if (product != null) {
                    productControler.productDetailsSuccess(activity,product)
                }
            }
            .addOnFailureListener {
                productControler.hideProgressDialog(activity)
            }
    }
    fun checkIfItemInCart(cartControler: CartControler,activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id", getCurrentUserId())
            .whereEqualTo("product_id", productId)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    cartControler.productExistInCart(activity)
                } else {
                    cartControler.hideProgressDialog(activity)
                }
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(activity)

            }
    }
    fun addCartItems(cartControler: CartControler,activity: ProductDetailsActivity, cartItem: CartItem) {
        mFirestore.collection("cart_items")
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                cartControler.addToCartSuccess(activity)
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(activity)
            }
    }
    fun addAddress(userControler: UserControler,activity: AddEditAddressActivity, addressInfo: Address) {
        mFirestore.collection("addresses")
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                userControler.addUpdateAddressSuccess(activity)
            }
            .addOnFailureListener {
                userControler.hideProgressDialog(activity)
            }

    }
    fun updateAddress(userControler: UserControler,activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFirestore.collection("addresses")
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                userControler.addUpdateAddressSuccess(activity)
            }
            .addOnFailureListener {
                userControler.hideProgressDialog(activity)
            }
    }
    fun deleteAddress(userControler: UserControler,activity: AddressListActivity, addressId: String) {
        mFirestore.collection("addresses")
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                userControler.deleteAddressSuccess(activity)
            }
            .addOnFailureListener {
                userControler.hideProgressDialog(activity)
            }

    }
    fun getAddressesList(userControler: UserControler,activity: AddressListActivity) {
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
                userControler.successAddressListFromFirestore(activity,addressList = addressList)

            }
            .addOnFailureListener {
                userControler.hideProgressDialog(activity)
            }
    }
    fun removedItemFromCart(cartControler: CartControler,fragment: MyCartFragment, id: String) {
        mFirestore.collection("cart_items")
            .document(id)
            .delete()
            .addOnSuccessListener {
                cartControler.itemRemovedSuccess(fragment)
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(fragment)
            }
    }
    fun updateMyCart(cartControler: CartControler,fragment: MyCartFragment, id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection("cart_items")
            .document(id)
            .update(itemHashMap)
            .addOnSuccessListener {
                cartControler.itemUpdateSuccess(fragment)
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(fragment)
            }
    }
    fun uploadImageToCloudStorage(userControler: UserControler,activity: UserProfileActivity,mSelectedImageFileUri: Uri,imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "$imageType${System.currentTimeMillis()}.${
                userControler.getFileExtension(
                    activity, mSelectedImageFileUri
                )
            }"
        )
        mSelectedImageFileUri.let {
            sRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        userControler.imageUploadSuccess(activity,uri.toString())
                    }
            }
        }
            .addOnFailureListener { exception ->
                userControler.hideProgressDialog(activity)
            }
    }
    fun updateUserProfileData(userControler: UserControler,activity: UserProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection("users")
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                userControler.userProfileUpdateSuccess(activity)
            }
            .addOnFailureListener { e ->
                userControler.hideProgressDialog(activity)

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
    fun placeOrder(orderControler: OrderControler,activity: CheckoutActivity, order: Order) {
        mFirestore.collection("orders")
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                orderControler.orderPlacedSuccess(activity)
            }
            .addOnFailureListener {
                orderControler.hideProgressDialog(activity)
            }


    }
    fun updateAllDetails(cartControler: CartControler,activity: CheckoutActivity, cartList: ArrayList<CartItem>) {
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
                cartControler.allDetailsUpdatedSuccess(activity)
            }
            .addOnFailureListener {
                cartControler.hideProgressDialog(activity)
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