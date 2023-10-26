package com.example.shopuin.activities



import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.shopuin.R
import com.google.android.material.snackbar.Snackbar


open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content), message,
            Snackbar.LENGTH_LONG
        )
        val snackBarView = snackBar.view
        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()

    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        MyToast.show(this,"Nhấn back để thoát",false)
        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


}

object MyToast {
    @SuppressLint("MissingInflatedId")
    fun show(context: Context?, text: String?, isLong: Boolean) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(com.example.shopuin.R.layout.dialog_toast, null)
        val textV = layout.findViewById(com.example.shopuin.R.id.tv_text) as TextView
        textV.text = text
        textV.gravity = Gravity.CENTER
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast.setView(layout)
        toast.show()
    }
}