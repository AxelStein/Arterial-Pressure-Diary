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

    @Transaction
    open fun importBackup(backup: List<PulseLog>) {
        deleteAll()
        insert(backup)
    }
}