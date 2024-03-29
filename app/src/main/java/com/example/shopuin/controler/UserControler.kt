package com.example.shopuin.controler

import android.net.Uri
import com.example.shopuin.view.activities.AddEditAddressActivity
import com.example.shopuin.view.activities.AddressListActivity
import com.example.shopuin.view.activities.BaseActivity
import com.example.shopuin.view.activities.RegisterActivity
import com.example.shopuin.view.activities.SettingsActivity
import com.example.shopuin.view.activities.UserProfileActivity
import com.example.shopuin.controler.firebase.FirestoreClass
import com.example.shopuin.view.fragment.MyCartFragment
import com.example.shopuin.models.Address
import com.example.shopuin.models.User

class UserControler {
    fun registerUser(activity: RegisterActivity, user: User){
        FirestoreClass().registerUser(this,activity, user)
    }
    fun userRegistrationSuccess(activity : RegisterActivity){
        activity.userRegistrationSuccess()
    }
    fun getCurrentUserId(): String{
        return FirestoreClass().getCurrentUserId()
    }

    fun getUser(myCartFragment: MyCartFragment){
        FirestoreClass().getUser(this,myCartFragment)
    }

    fun getUser(activity: BaseActivity){
        FirestoreClass().getUser(this,activity)
    }
    fun userDetailsSuccess(activity: SettingsActivity, user:User){
        activity.userDetailsSuccess(user)
    }
    fun returnUser(myCartFragment: MyCartFragment, user: User) {
        myCartFragment.returnUser(user)
    }

    fun updateUserProfileData(activity: UserProfileActivity, userHashMap: HashMap<String, Any>){
        FirestoreClass().updateUserProfileData(this,activity, userHashMap = userHashMap)
    }

    fun userProfileUpdateSuccess(activity: UserProfileActivity){
        activity.userProfileUpdateSuccess()
    }

    fun uploadImageToCloudStorage(
        activity: UserProfileActivity,
        mSelectedImageFileUri: Uri,
        imageType: String
    ){
        FirestoreClass().uploadImageToCloudStorage(
            this,activity,
            mSelectedImageFileUri!!, imageType
        )
    }

    fun getFileExtension(activity: UserProfileActivity, uri: Uri?): String?{
        return activity.getFileExtension(activity,uri)
    }

    fun imageUploadSuccess(activity: UserProfileActivity, imageURL: String){
        activity.imageUploadSuccess(imageURL)
    }

    fun getAddressesList(activity: AddressListActivity){
        FirestoreClass().getAddressesList(this,activity)
    }

    fun  successAddressListFromFirestore(activity: AddressListActivity, addressList: ArrayList<Address>){
        activity.successAddressListFromFirestore(addressList)
    }

    fun  addAddress(fullName : String,phoneNumber: String,address: String,city : String, additionalNote : String
                    ,addressType : String,otherDetails : String, activity: AddEditAddressActivity){
        val addressModel = Address(
            UserControler().getCurrentUserId(),
            fullName,
            phoneNumber,
            address,
            city,
            additionalNote,
            addressType,
            otherDetails,
            System.currentTimeMillis().toString()
        )
        FirestoreClass().addAddress(this,activity, addressModel)
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String){
        FirestoreClass().updateAddress(
            this,activity,
            addressInfo, addressId
        )
    }

    fun addUpdateAddressSuccess(activity: AddEditAddressActivity){
        activity.addUpdateAddressSuccess()
    }
    fun deleteAddress(activity: AddressListActivity, addressId: String){
        FirestoreClass().deleteAddress(
            this,activity,
            addressId
        )
    }
    fun deleteAddressSuccess(activity: AddressListActivity){
        activity.deleteAddressSuccess()
    }
    fun hideProgressDialog(activity: BaseActivity){
        activity.hideProgressDialog()
    }
}