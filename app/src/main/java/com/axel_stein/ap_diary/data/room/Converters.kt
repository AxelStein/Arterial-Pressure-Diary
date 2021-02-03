package com.axel_stein.ap_diary.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun dateToStr(date: DateTime): String = date.toString()

    @TypeConverter
    fun strToDate(s: String): DateTime = DateTime(s)
}