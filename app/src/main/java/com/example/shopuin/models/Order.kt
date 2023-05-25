package com.example.shopuin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val user_name: String = "",
    val address: String = "",
    val mobile: Long = 0,
    val email: String = "",
    var cart_order: String = "",
) : Parcelable