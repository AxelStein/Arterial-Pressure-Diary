package com.axel_stein.ap_diary.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.ap_diary.data.room.model.ApLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class ApLogDao : BaseDao<ApLog>() {
    @Query("DELETE FROM ap_log")
    abstract fun deleteAll()

    @Query("DELETE FROM ap_log WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("SELECT * FROM ap_log WHERE id = :id")
    abstract fun getById(id: Long): Single<ApLog>

    @Query("SELECT * FROM ap_log")
    abstract fun getAll(): List<ApLog>

    @Transaction
    open fun importBackup(backup: List<ApLog>) {
        deleteAll()
        insert(backup)
    }
}