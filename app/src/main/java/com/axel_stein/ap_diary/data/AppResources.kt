package com.axel_stein.ap_diary.data

import android.content.Context
import com.axel_stein.ap_diary.R
import java.io.File

class AppResources(private val context: Context) {
    val months: Array<String> = context.resources.getStringArray(R.array.months)

    fun appDir(): File = context.filesDir
}