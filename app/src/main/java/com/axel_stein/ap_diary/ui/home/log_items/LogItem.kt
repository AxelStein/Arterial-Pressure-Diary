package com.axel_stein.ap_diary.ui.home.log_items

import android.content.Context
import org.joda.time.DateTime

interface LogItem {
    fun id(): Long
    fun type(): Int
    fun highValue(): Boolean
    fun format(context: Context)
    fun time(): String
    fun dateTime(): DateTime
}