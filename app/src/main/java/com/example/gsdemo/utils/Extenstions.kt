package com.example.gsdemo.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

fun Context.snackbar(view: View, message: CharSequence) {
    val snackbar: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    val snackbarView: View = snackbar.view
    val textView = snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    textView.maxLines = 3
    snackbar.show()
}


