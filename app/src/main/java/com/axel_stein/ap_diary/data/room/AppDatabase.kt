package com.axel_stein.ap_diary.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.axel_stein.ap_diary.data.room.dao.ApLogDao
import com.axel_stein.ap_diary.data.room.dao.LogDao
import com.axel_stein.ap_diary.data.room.dao.PulseLogDao
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog

@Database(
    entities = [
        ApLog::class,
        PulseLog::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apLogDao(): ApLogDao
    abstract fun pulseLogDao(): PulseLogDao
    abstract fun logDao(): LogDao
}