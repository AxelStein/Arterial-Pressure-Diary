package com.axel_stein.ap_diary.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentHomeBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.SwipeCallback
import com.axel_stein.ap_diary.ui.utils.TextHeaderDecor
import com.axel_stein.ap_diary.ui.utils.setVisible
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val adapter = HomeAdapter()
    private val headerDecor = TextHeaderDecor(R.layout.item_date)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(headerDecor)
        binding.recyclerView.adapter = adapter
        adapter.onItemClick = {
            val action = when (it) {
                is ApLogItem -> R.id.action_edit_ap
                is PulseLogItem -> R.id.action_edit_pulse
                else -> 0
            }
            findNavController().navigate(action, Bundle().apply {
                putLong("id", it.id())
            })
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.items.observe(viewLifecycleOwner, {
            adapter.submitList(it.list)
            headerDecor.setHeaders(it.headers)
            binding.noData.setVisible(it.list.isEmpty())
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_ap -> {
                findNavController().navigate(R.id.action_add_ap)
                true
            }
            R.id.menu_add_pulse -> {
                findNavController().navigate(R.id.action_add_pulse)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}