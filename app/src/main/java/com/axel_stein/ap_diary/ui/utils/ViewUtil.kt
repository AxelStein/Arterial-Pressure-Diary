package com.axel_stein.ap_diary.ui.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}