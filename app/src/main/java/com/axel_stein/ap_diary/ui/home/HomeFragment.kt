package com.axel_stein.ap_diary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentHomeBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.TextHeaderDecor
import com.axel_stein.ap_diary.ui.utils.setItemSelectedListener
import com.axel_stein.ap_diary.ui.utils.setVisible
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val homeAdapter = HomeAdapter()
    private val headerDecor = TextHeaderDecor(R.layout.item_date)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.toolbar.setOnMenuItemClickListener { item ->
            setupAxisTransition()
            when (item.itemId) {
                R.id.action_charts -> {
                    findNavController().navigate(R.id.charts_fragment)
                    true
                }

                R.id.action_settings -> {
                    findNavController().navigate(R.id.main_preferences_fragment)
                    true
                }

                R.id.menu_add_ap -> {
                    findNavController().navigate(R.id.action_add_ap)
                    true
                }

                R.id.menu_add_pulse -> {
                    findNavController().navigate(R.id.action_add_pulse)
                    true
                }

                else -> false
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            setHasFixedSize(true)
            addItemDecoration(headerDecor)
            adapter = homeAdapter
        }
        homeAdapter.onItemClick = { item, itemView ->
            onLogItemClick(item, itemView)
        }
        binding.spinnerDate.onItemSelectedListener = setItemSelectedListener {
            viewModel.selectYearMonth(it)
        }
        return binding.root
    }

    private fun onLogItemClick(item: LogItem, itemView: View) {
        exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.transform_duration).toLong()
        }
        reenterTransition = Hold().apply {
            duration = resources.getInteger(R.integer.transform_duration).toLong()
        }

        val action = when (item) {
            is ApLogItem -> {
                R.id.action_edit_ap
            }
            is PulseLogItem -> {
                R.id.action_edit_pulse
            }
            else -> 0
        }
        findNavController().navigate(action, Bundle().apply {
            putLong("id", item.id())
        }, null, FragmentNavigatorExtras(itemView to itemView.transitionName))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.yearMonthListLiveData.observe(viewLifecycleOwner) {
            binding.spinnerDate.setVisible(!it.isNullOrEmpty())
            binding.noData.setVisible(it.isNullOrEmpty())
            binding.spinnerDate.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, it).apply {
                setDropDownViewResource(R.layout.item_popup)
            }
            binding.spinnerDate.setSelection(viewModel.selectedYearMonth)
        }
        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            homeAdapter.submitList(it.list)
            headerDecor.setHeaders(it.headers)
            binding.noData.setVisible(it.list.isEmpty())
        }
        viewModel.showMessageLiveData.observe(viewLifecycleOwner) {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAxisTransition() {
        exitTransition = MaterialSharedAxis(Z, true).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
        reenterTransition = MaterialSharedAxis(Z, false).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
    }
}