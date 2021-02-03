package com.axel_stein.ap_diary.ui.home.log_items

import android.content.Context
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.ui.utils.formatTime

class ApLogItem(private val apLog: ApLog): LogItem {
    private var time = ""

    override fun id() = apLog.id

    override fun type() = 0

    override fun highValue() = false

    override fun format(context: Context) {
        time = formatTime(context, apLog.dateTime)
    }

    override fun time() = time

    override fun dateTime() = apLog.dateTime
}