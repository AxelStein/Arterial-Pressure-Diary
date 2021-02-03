package com.axel_stein.ap_diary.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog
import io.reactivex.Flowable

@Dao
interface LogDao {
    @Query("SELECT * FROM ap_log WHERE substr(date_time, 1, 7) = :yearMonth")
    fun getApLogsByYearMonth(yearMonth: String): List<ApLog>

    @Query("SELECT * FROM pulse_log WHERE substr(date_time, 1, 7) = :yearMonth")
    fun getPulseLogsByYearMonth(yearMonth: String): List<PulseLog>

    @Transaction
    fun getByYearMonth(yearMonth: String): List<Any> {
        val list = ArrayList<Any>()
        list.add(getApLogsByYearMonth(yearMonth))
        list.add(getPulseLogsByYearMonth(yearMonth))
        return list
    }

    // 2021-02-01T08:00:00.000Z
    @Query("""
        SELECT SUBSTR(1, 7) FROM ap_log UNION 
        SELECT SUBSTR(1, 7) FROM pulse_log
    """)
    fun getYearMonthList(): Flowable<List<String>>
}