package com.axel_stein.ap_diary.ui.home.log_items

import android.content.Context
import com.axel_stein.ap_diary.data.room.model.PulseLog
import com.axel_stein.ap_diary.ui.utils.formatTime

class PulseLogItem(private val log: PulseLog) : LogItem {
    private var time = ""

    override fun id() = log.id

    override fun type() = 0

    override fun highValue() = false

    override fun format(context: Context) {
        time = formatTime(context, log.dateTime)
    }

    override fun time() = time

    override fun dateTime() = log.dateTime
}