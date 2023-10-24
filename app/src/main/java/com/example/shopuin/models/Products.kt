package com.example.shopuin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    val title:String = "",
    val price:String = "",
    val description:String = "",
    val stock_quantity:String = "",
    val image:String = "",
    var product_id:String = "",
    val shipping_charge:String = ""
):Parcelable