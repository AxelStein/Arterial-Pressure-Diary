package com.axel_stein.ap_diary.data.backup

import com.axel_stein.ap_diary.data.AppResources
import com.axel_stein.ap_diary.data.room.dao.ApLogDao
import com.axel_stein.ap_diary.data.room.dao.PulseLogDao
import com.axel_stein.ap_diary.ui.App
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import java.io.File
import javax.inject.Inject

class BackupHelper {
    private lateinit var apDao: ApLogDao
    private lateinit var pulseDao: PulseLogDao
    private lateinit var gson: Gson
    private lateinit var resources: AppResources

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(a: ApLogDao, p: PulseLogDao, g: Gson, r: AppResources) {
        apDao = a
        pulseDao = p
        gson = g
        resources = r
    }

    fun createBackup(): Single<File> {
        return Single.fromCallable {
            createBackupImpl()
        }.subscribeOn(io())
    }

    fun createBackupImpl(): File {
        val backup = Backup(
            1,
            apDao.getAll(),
            pulseDao.getAll()
        )
        val data = gson.toJson(backup, Backup::class.java)
        val backupFile = File(resources.appDir(), BACKUP_FILE_NAME)
        backupFile.writeText(data)
        return backupFile
    }

    fun importBackup(data: String): Completable {
        return Completable.fromAction {
            val backup = gson.fromJson(data, Backup::class.java)
            // version 1
            apDao.importBackup(backup.ap_logs)
            pulseDao.importBackup(backup.pulse_logs)
        }.subscribeOn(io())
    }
}