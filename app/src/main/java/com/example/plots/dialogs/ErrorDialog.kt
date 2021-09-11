package com.example.plots.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog

class ErrorDialog(private val activity: Activity, private val resource: Int) {
    private lateinit var dialog: AlertDialog

    @SuppressLint("InflateParams")
    fun start() {
        val builder = AlertDialog.Builder(activity)
        builder.setView(activity.layoutInflater.inflate(resource, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog.show()
    }
}