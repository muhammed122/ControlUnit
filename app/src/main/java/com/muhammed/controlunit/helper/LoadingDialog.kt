package com.muhammed.controlunit.helper

import android.app.Activity
import android.app.AlertDialog
import com.muhammed.controlunit.R

object LoadingDialog {


    private var alertDialog: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null


    fun showDialog(activity: Activity) {
        alertDialog = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        alertDialog?.setView(inflater.inflate(R.layout.dialog_layout, null, false))
        alertDialog?.setCancelable(false)

        dialog = alertDialog?.create()

        dialog?.show()
    }

    fun hideAlertDialog() {
        dialog?.dismiss()

    }


}