package com.axel_stein.ap_diary.data.room.repository

import android.content.Context
import android.util.SparseArray
import androidx.core.util.set
import androidx.room.InvalidationTracker
import com.axel_stein.ap_diary.data.room.AppDatabase
import com.axel_stein.ap_diary.data.room.dao.LogDao
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.formatDate
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDate

class LogRepository(private val context: Context, private val db: AppDatabase, private val dao: LogDao) {
    private val tables = arrayOf(
        "ap_log",
        "pulse_log"
    )

    data class LogListResult(
        val list: List<LogItem>,
        val headers: SparseArray<String>
    )

    fun getLogsByYearMonth(yearMonth: String) = createFlowable { dao.getByYearMonth(yearMonth) }

    fun getYearMonthList() = dao.getYearMonthList()

    fun observeUpdates(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            val observer = object : InvalidationTracker.Observer(tables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    emitter.onNext(true)
                }
            }
            db.invalidationTracker.addObserver(observer)
            emitter.setCancellable {
                db.invalidationTracker.removeObserver(observer)
            }
        }, BackpressureStrategy.LATEST)
    }

    private fun createFlowable(getData: () -> List<Any>): Flowable<LogListResult> {
        return Flowable.create({ emitter ->
            val worker = Schedulers.io().createWorker()
            val emitData = {
                worker.schedule {
                    try {
                        val objects = getData().map {
                            when (it) {
                                is ApLog -> ApLogItem(it)
                                is PulseLog -> PulseLogItem(it)
                                else -> {
                                    emitter.tryOnError(Throwable("Error creating flowable"))
                                    return@schedule
                                }
                            }
                        }
                        val list = sort(format(objects).toMutableList())
                        emitter.onNext(LogListResult(list, createHeaders(list)))
                    } catch (e: Exception) {
                        emitter.tryOnError(e)
                    }
                }
            }
            val observer = object : InvalidationTracker.Observer(tables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    emitData()
                }
            }
            db.invalidationTracker.addObserver(observer)
            emitter.setCancellable {
                db.invalidationTracker.removeObserver(observer)
            }
            emitData()
        }, BackpressureStrategy.LATEST)
    }

    private fun format(items: List<LogItem>) = items.onEach { it.format(context) }

    private fun sort(items: MutableList<LogItem>): List<LogItem> {
        items.sortByDescending { it.dateTime() }
        items.sortWith { a, b ->
            val d1 = a?.dateTime()?.toLocalDate()
            val d2 = b?.dateTime()?.toLocalDate()

            val compareDates = d1?.compareTo(d2)
            if (compareDates == 0) {
                val t1 = a.dateTime().toLocalTime()
                val t2 = b?.dateTime()?.toLocalTime()
                t1.compareTo(t2)
            } else {
                0
            }
        }
        return items
    }

    private fun createHeaders(items: List<LogItem>): SparseArray<String> {
        val headers = SparseArray<String>()
        var date: LocalDate? = null
        items.forEachIndexed { index, item ->
            val itemDate = item.dateTime().toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(context, item.dateTime())
                date = itemDate
            }
        }
        return headers
    }
}