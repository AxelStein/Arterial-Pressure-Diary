package com.axel_stein.ap_diary.ui.charts

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentChartsBinding
import com.axel_stein.ap_diary.ui.charts.helpers.ChartData
import com.axel_stein.ap_diary.ui.charts.helpers.LabelValueFormatter
import com.axel_stein.ap_diary.ui.utils.setItemSelectedListener
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialSharedAxis

class ChartsFragment: Fragment() {
    private val viewModel: ChartsViewModel by viewModels()
    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(context)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupChart()

        /*viewModel.showErrorLiveData.observe(viewLifecycleOwner, { todo
            if (it) {
                binding.content.hide()
                binding.textError.show()
            } else {
                binding.content.show()
                binding.textError.hide()
            }
        })*/
    }

    private fun setupChart() {
        setupChartView(binding.chart)
        binding.chartTypeSpinner.onItemSelectedListener = setItemSelectedListener {
            viewModel.setChartType(it)
        }
        binding.chartPeriodSpinner.onItemSelectedListener = setItemSelectedListener {
            viewModel.setChartPeriod(it)
        }

        viewModel.chartLiveData.observe(viewLifecycleOwner, {
            setChartData(binding.chart, it)
        })
    }

    private fun setupChartView(chart: LineChart) {
        chart.xAxis.textColor = MaterialColors.getColor(
            requireActivity(),
            R.attr.chartTextColor,
            Color.BLACK
        )
        chart.xAxis.position = XAxis.XAxisPosition.TOP
        chart.xAxis.setAvoidFirstLastClipping(true)

        chart.axisLeft.textColor = MaterialColors.getColor(
            requireActivity(),
            R.attr.chartTextColor,
            Color.BLACK
        )
        chart.axisLeft.setDrawAxisLine(true)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawTopYLabelEntry(false)
        chart.axisLeft.setCenterAxisLabels(true)

        chart.axisRight.setDrawLabels(false)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isDoubleTapToZoomEnabled = false
        chart.setDrawGridBackground(false)
        chart.setExtraOffsets(8f, 0f, 8f, 4f)
        chart.setNoDataText(getString(R.string.no_data))
        chart.setPinchZoom(false)
    }

    private fun setChartData(chart: LineChart, data: ChartData?) {
        chart.clear()
        if (data != null) {
            val lineData = data.getLineData()
            chart.xAxis.granularity = 1f
            chart.xAxis.labelCount = lineData.entryCount
            chart.data = lineData
            chart.xAxis.valueFormatter = LabelValueFormatter(data.getLabels())
            setChartLimitLines(binding.chart, data.getLimits())
            chart.setVisibleXRange(0f, 10f)
        }
    }

    private fun setChartLimitLines(chart: LineChart, limits: ArrayList<Float>) {
        chart.axisLeft.removeAllLimitLines()
        for (limit in limits) {
            chart.axisLeft.addLimitLine(LimitLine(limit))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}