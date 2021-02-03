package com.axel_stein.ap_diary.ui.home.log_items

import android.content.Context
import org.joda.time.DateTime

interface LogItem {
    fun id(): Long
    fun error(): Boolean
    fun format(context: Context)
    fun title(): String
    fun suffix(): String
    fun comment(): String
    fun time(): String
    fun dateTime(): DateTime
}