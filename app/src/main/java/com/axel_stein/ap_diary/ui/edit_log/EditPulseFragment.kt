package com.axel_stein.ap_diary.ui.edit_log

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentEditPulseBinding

class EditPulseFragment : Fragment() {
    private lateinit var binding: FragmentEditPulseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPulseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.fragment_edit_log, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}