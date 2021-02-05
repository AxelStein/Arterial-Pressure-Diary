package com.axel_stein.ap_diary.data.backup

import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog

data class Backup(
    val version: Int,
    val ap_logs: List<ApLog>,
    val pulse_logs: List<PulseLog>
)

const val BACKUP_FILE_NAME = "backup.json"