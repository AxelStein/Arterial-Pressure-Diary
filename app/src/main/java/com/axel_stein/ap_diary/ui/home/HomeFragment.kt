package com.axel_stein.ap_diary.ui.home

import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.data.room.model.PulseLog
import com.axel_stein.ap_diary.databinding.FragmentHomeBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.SwipeCallback
import com.axel_stein.ap_diary.ui.utils.TextHeaderDecor
import org.joda.time.DateTime
import kotlin.random.Random

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val headerDecor = TextHeaderDecor(R.layout.item_date).apply {
        setHeaders(SparseArray<String>().apply {
            set(0, "Wed 3 feb")
            set(2, "Mon 1 feb")
            set(5, "Fri 27 Jan")
            set(10, "Thu 15 Jan")
            set(15, "Sun 3 Jan")
        })
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(headerDecor)

        val adapter = HomeAdapter()
        binding.recyclerView.adapter = adapter

        SwipeCallback(requireContext()).apply {
            iconMargin = 16f
            swipeRightEnabled = true
            setSwipeRightColorRes(R.color.color_red)
            setSwipeRightIconRes(R.drawable.icon_delete)
            onSwipeRight = {}
        }.also {
            ItemTouchHelper(it).attachToRecyclerView(binding.recyclerView)
        }

        val items = mutableListOf<LogItem>()
        for (i in 1..10) {
            items.add(ApLogItem(ApLog(Random.nextInt(100) + 70, Random.nextInt(60) + 70, DateTime())).apply { format(requireContext()) })
        }
        for (i in 1..10) {
            items.add(PulseLogItem(PulseLog(Random.nextInt(100) + 40, DateTime())).apply { format(requireContext()) })
        }
        items.shuffle()
        adapter.submitList(items)
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