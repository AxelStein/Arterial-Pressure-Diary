package com.axel_stein.ap_diary.ui.utils

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}