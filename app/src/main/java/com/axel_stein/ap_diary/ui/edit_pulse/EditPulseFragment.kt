package com.axel_stein.ap_diary.ui.edit_pulse

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentEditPulseBinding
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.ap_diary.ui.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z

class EditPulseFragment : Fragment(), OnConfirmListener {
    private var id = 0L
    private val viewModel: EditPulseViewModel by viewModels { EditPulseFactory(requireContext(), this, id) }
    private lateinit var binding: FragmentEditPulseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        id = arguments?.getLong("id") ?: 0L

        if (id == 0L) {
            enterTransition = MaterialSharedAxis(Z, true).apply {
                duration = resources.getInteger(R.integer.axis_duration).toLong()
            }
            returnTransition = MaterialSharedAxis(Z, false).apply {
                duration = resources.getInteger(R.integer.axis_duration).toLong()
            }
        } else {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = resources.getInteger(R.integer.transform_duration).toLong()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPulseBinding.inflate(inflater)
        ViewCompat.setTransitionName(binding.container, "shared_element_container")

        binding.date.setOnClickListener {
            showDatePicker(requireContext(), viewModel.getCurrentDateTime()) { year, month, dayOfMonth ->
                viewModel.setDate(year, month, dayOfMonth)
                binding.date.text = formatDate(requireContext(), true, year, month, dayOfMonth)
            }
        }
        binding.time.setOnClickListener {
            showTimePicker(requireContext(), viewModel.getCurrentDateTime()) { hourOfDay, minuteOfHour ->
                viewModel.setTime(hourOfDay, minuteOfHour)
                binding.time.text = formatTime(requireContext(), hourOfDay, minuteOfHour)
            }
        }
        binding.pulse.setupEditor {
            viewModel.setPulse(it)
        }
        binding.comment.setupEditor {
            viewModel.setComment(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pulseLiveData.observe(viewLifecycleOwner, {
            binding.date.text = formatDate(requireContext(), it.dateTime)
            binding.time.text = formatTime(requireContext(), it.dateTime)
            with(it.value) {
                binding.pulse.setEditorText(if (this == 0) "" else toString())
            }
            binding.comment.setEditorText(it.comment ?: "", false)
        })
        viewModel.errorValueEmptyLiveData.observe(viewLifecycleOwner, {
            binding.inputLayoutPulse.showError(it, R.string.error_field_empty)
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, LENGTH_SHORT).show()
            }
        })
        viewModel.actionFinishLiveData.observe(viewLifecycleOwner, {
            findNavController().navigateUp()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.fragment_edit_log, menu)
        menu.findItem(R.id.menu_delete)?.isVisible = id != 0L
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                viewModel.save()
                true
            }
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_log)
                    .positiveBtnText(R.string.action_delete)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}