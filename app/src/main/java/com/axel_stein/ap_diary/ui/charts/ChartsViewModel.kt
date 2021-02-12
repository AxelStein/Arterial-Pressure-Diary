package com.axel_stein.ap_diary.ui.charts

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.ap_diary.data.AppSettings
import com.axel_stein.ap_diary.data.room.dao.ApLogDao
import com.axel_stein.ap_diary.data.room.dao.PulseLogDao
import com.axel_stein.ap_diary.data.room.repository.LogRepository
import com.axel_stein.ap_diary.ui.App
import com.axel_stein.ap_diary.ui.charts.helpers.ChartData
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class ChartsViewModel : ViewModel() {
    private lateinit var pulseDao: PulseLogDao
    private lateinit var apDao: ApLogDao
    private lateinit var settings: AppSettings
    private lateinit var logRepository: LogRepository
    private val disposables = CompositeDisposable()

    private var chartPeriod = -1
    private var chartType = -1

    private val showError = MutableLiveData(false)
    val showErrorLiveData: LiveData<Boolean> = showError

    private val chart = MutableLiveData<ChartData?>()
    val chartLiveData: LiveData<ChartData?> = chart

    init {
        App.appComponent.inject(this)
        logRepository.observeUpdates()
            .subscribe {
                forceUpdate()
            }.also {
                disposables.add(it)
            }
    }

    @Inject
    fun setPulseDao(dao: PulseLogDao) {
        pulseDao = dao
    }

    @Inject
    fun setApDao(dao: ApLogDao) {
        apDao = dao
    }

    @Inject
    fun setSettings(settings: AppSettings) {
        this.settings = settings
    }

    @Inject
    fun setLogRepository(repository: LogRepository) {
        this.logRepository = repository
    }

    private fun forceUpdate() {
        setChartPeriod(chartPeriod)
    }

    fun setChartPeriod(period: Int) {
        chartPeriod = period
        setChartType(chartType)
    }

    fun setChartType(type: Int) {
        chartType = type
        when (type) {
            0 -> loadApChartData(chartPeriod)
            1 -> loadPulseChartData(chartPeriod)
        }
    }

    @SuppressLint("CheckResult")
    private fun loadPulseChartData(period: Int) {
        Single.fromCallable {
            when (period) {
                0 -> pulseDao.getByLastMonth()
                1 -> pulseDao.getByLastThreeMonths()
                else -> pulseDao.getByThisYear()
            }
        }.subscribeOn(io())
            .subscribe({
                val data = ChartData()
                data.setPulseLogs(it)
                if (data.isEmpty()) chart.postValue(null)
                else chart.postValue(data)
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun loadApChartData(period: Int) {
        Single.fromCallable {
            when (period) {
                0 -> apDao.getByLastMonth()
                1 -> apDao.getByLastThreeMonths()
                else -> apDao.getByThisYear()
            }
        }.subscribeOn(io())
            .subscribe({
                val data = ChartData()
                data.setApLogs(it)
                if (data.isEmpty()) chart.postValue(null)
                else chart.postValue(data)
            }, {
                it.printStackTrace()
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}