package com.axel_stein.ap_diary.data

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.ap_diary.R
import com.google.android.material.color.MaterialColors
import java.io.File

class AppResources(private val context: Context) {
    val months: Array<String> = context.resources.getStringArray(R.array.months)
    val monthsAbbrArray: Array<String> = context.resources.getStringArray(R.array.months_a)
    private var pulseLineColor = 0
    private var systolicLineColor = 0
    private var diastolicLineColor = 0

    fun initColorResources(context: AppCompatActivity) {
        pulseLineColor = MaterialColors.getColor(context, R.attr.pulseLineColor, Color.BLACK)
        systolicLineColor = MaterialColors.getColor(context, R.attr.systolicLineColor, Color.BLACK)
        diastolicLineColor = MaterialColors.getColor(context, R.attr.diastolicLineColor, Color.BLACK)
    }

    fun appDir(): File = context.filesDir
    fun pulseLineColor() = pulseLineColor
    fun systolicLineColor() = systolicLineColor
    fun diastolicLineColor() = diastolicLineColor
}