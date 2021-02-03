package com.axel_stein.ap_diary.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "pulse_log")
data class PulseLog(
    val value: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime,

    val comment: String? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}