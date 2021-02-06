package com.axel_stein.ap_diary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentHomeBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.SwipeCallback
import com.axel_stein.ap_diary.ui.utils.TextHeaderDecor
import com.axel_stein.ap_diary.ui.utils.setItemSelectedListener
import com.axel_stein.ap_diary.ui.utils.setVisible
import com.google.android.material.color.MaterialColors
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        // binding.toolbar.inflateMenu(R.menu.fragment_home)
        binding.toolbar.setOnMenuItemClickListener { item ->
            setupAxisTransition()
            when (item.itemId) {
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

        SwipeCallback(requireContext()).apply {
            iconMargin = 16f
            swipeRightEnabled = true
            setSwipeRightColor(MaterialColors.getColor(requireContext(), R.attr.colorRemove, 0))
            setSwipeRightIconRes(R.drawable.icon_delete)
            onSwipeRight = {}
        }.also {
            ItemTouchHelper(it).attachToRecyclerView(binding.recyclerView)
        }
        return binding.root
    }

    private fun onLogItemClick(item: LogItem, itemView: View) {
        /*exitTransition = MaterialElevationScale(false).apply {
            duration = 2000
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 2000
        }*/
        exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.transform_duration).toLong()
        }
        reenterTransition = Hold().apply {
            duration = resources.getInteger(R.integer.transform_duration).toLong()
        }
        ViewCompat.setTransitionName(itemView, "shared_element_container")
        val action = when (item) {
            is ApLogItem -> R.id.action_edit_ap
            is PulseLogItem -> R.id.action_edit_pulse
            else -> 0
        }
        findNavController().navigate(action, Bundle().apply {
            putLong("id", item.id())
        }, null, FragmentNavigatorExtras(itemView to "shared_element_container"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.yearMonthListLiveData.observe(viewLifecycleOwner, {
            binding.spinnerDate.setVisible(!it.isNullOrEmpty())
            binding.noData.setVisible(it.isNullOrEmpty())
            binding.spinnerDate.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, it).apply {
                setDropDownViewResource(R.layout.item_popup)
            }
            binding.spinnerDate.setSelection(viewModel.selectedYearMonth)
        })
        viewModel.itemsLiveData.observe(viewLifecycleOwner, {
            homeAdapter.submitList(it.list)
            headerDecor.setHeaders(it.headers)
            binding.noData.setVisible(it.list.isEmpty())
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAxisTransition() {
        /*exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
        reenterTransition = Hold().apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }*/
        exitTransition = MaterialSharedAxis(Z, true).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
        reenterTransition = MaterialSharedAxis(Z, false).apply {
            duration = resources.getInteger(R.integer.axis_duration).toLong()
        }
    }
}