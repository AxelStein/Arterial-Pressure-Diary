package com.axel_stein.ap_diary.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "ap_log")
data class ApLog(
    val systolic: Int,
    val diastolic: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime,

    val comment: String? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}