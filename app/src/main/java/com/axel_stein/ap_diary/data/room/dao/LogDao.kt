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
        list.addAll(getApLogsByYearMonth(yearMonth))
        list.addAll(getPulseLogsByYearMonth(yearMonth))
        return list
    }

    // 2021-02-01T08:00:00.000Z
    @Query("""
        SELECT SUBSTR(date_time, 1, 7) AS ym FROM ap_log UNION 
        SELECT SUBSTR(date_time, 1, 7) AS ym FROM pulse_log
        GROUP BY ym
        ORDER BY ym DESC
    """)
    fun getYearMonthList(): Flowable<List<String>>
}