package com.axel_stein.ap_diary.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.data.room.model.ApLog
import com.axel_stein.ap_diary.databinding.FragmentHomeBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import org.joda.time.DateTime

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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

        val adapter = HomeAdapter()
        binding.recyclerView.adapter = adapter

        val items = mutableListOf<LogItem>()
        for (i in 1..100) {
            items.add(ApLogItem(ApLog(110, 70, DateTime())))
        }
        adapter.submitList(items)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_ap -> {
                findNavController().navigate(R.id.action_add_log)
                true
            }
            R.id.menu_add_pulse -> {
                findNavController().navigate(R.id.action_add_log)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}