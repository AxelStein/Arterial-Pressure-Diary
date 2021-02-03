package com.axel_stein.ap_diary.ui.home.log_items

import android.content.Context
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.ui.utils.formatTime

class ApLogItem(private val log: ApLog): LogItem {
    private var title = ""
    private var time = ""
    private var suffix = ""
    private var comment = ""

    override fun id() = log.id

    override fun error() = false

    override fun format(context: Context) {
        title = "${log.systolic} / ${log.diastolic}"
        suffix = context.getString(R.string.suffix_ap)
        comment = log.comment ?: ""
        time = formatTime(context, log.dateTime)
    }

    override fun title() = title

    override fun suffix() = suffix

    override fun comment() = comment

    override fun time() = time

    override fun dateTime() = log.dateTime
}