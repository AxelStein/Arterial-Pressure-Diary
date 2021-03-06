package com.axel_stein.ap_diary.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.ap_diary.data.room.model.PulseLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class PulseLogDao : BaseDao<PulseLog>() {
    @Query("DELETE FROM pulse_log")
    abstract fun deleteAll()

    @Query("DELETE FROM pulse_log WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("SELECT * FROM pulse_log WHERE id = :id")
    abstract fun getById(id: Long): Single<PulseLog>

    @Query("SELECT * FROM pulse_log")
    abstract fun getAll(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-1 month')")
    abstract fun getByLastMonth(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-3 month')")
    abstract fun getByLastThreeMonths(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-1 year')")
    abstract fun getByThisYear(): List<PulseLog>

    @Transaction
    open fun importBackup(backup: List<PulseLog>) {
        deleteAll()
        insert(backup)
    }
}