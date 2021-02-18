package com.axel_stein.ap_diary.ui.charts.helpers

import com.axel_stein.ap_diary.data.AppResources
import com.axel_stein.ap_diary.data.AppSettings
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog
import com.axel_stein.ap_diary.ui.App
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import javax.inject.Inject

class ChartData {
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources
    private lateinit var lineData: LineData
    private lateinit var labels: Array<String>
    private lateinit var limits: ArrayList<Float>
    private var maxValue = 0f

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setSettings(s: AppSettings) {
        settings = s
    }

    @Inject
    fun setResources(r: AppResources) {
        resources = r
    }

    fun setPulseLogs(list: List<PulseLog>) {
        val inflater = LineDataInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as PulseLog
            log.value.toFloat() to log.dateTime
        }

        val lineColor = resources.pulseLineColor()
        createChartData(inflater, lineColor, -1, arrayListOf(100f))
    }

    private fun createChartData(inflater: LineDataInflater, lineColor: Int, fillColor: Int, limits: ArrayList<Float>) {
        this.limits = limits
        labels = inflater.getLabels()
        maxValue = inflater.getMaxValue()
        lineData = LineDataCreator()
            .from(inflater.getEntries(), lineColor, fillColor)
            .create()
    }

    fun setApLogs(list: List<ApLog>) {
        val sortedList = list.sortedBy { it.dateTime }

        val inflater = LineDataInflater()
        inflater.inflate(sortedList) {
            val log = it as ApLog
            log.systolic.toFloat() to log.dateTime
        }

        val systolicEntries = inflater.getEntries()
        labels = inflater.getLabels()
        limits = arrayListOf(85f, 90f, 100f, 130f, 140f, 160f)

        val diastolicEntries = sortedList.mapIndexed { index, log ->
            Entry(index.toFloat(), log.diastolic.toFloat())
        }

        lineData = LineDataCreator()
            .from(systolicEntries, resources.systolicLineColor(), -1)
            .from(diastolicEntries, resources.diastolicLineColor(), -1)
            .create()
    }

    fun isEmpty() = labels.isNullOrEmpty()

    fun getLabels() = labels

    fun getLimits() = limits

    fun getMaxValue() = maxValue

    fun getLineData() = lineData
}