package com.axel_stein.ap_diary.ui.edit_ap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.FragmentEditApBinding
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog
import com.axel_stein.ap_diary.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.ap_diary.ui.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z

class EditApFragment : Fragment(), OnConfirmListener {
    private var id = 0L
    private val viewModel: EditApViewModel by viewModels { EditApFactory(requireContext(), this, id) }
    private lateinit var binding: FragmentEditApBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.transform_duration).toLong()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditApBinding.inflate(inflater)
        binding.container.transitionName = "shared_element_ap_$id"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
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
                else -> false
            }
        }
        binding.toolbar.menu.findItem(R.id.menu_delete)?.isVisible = id != 0L

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
        binding.systolic.setupEditor {
            viewModel.setSystolic(it)
        }
        binding.diastolic.setupEditor {
            viewModel.setDiastolic(it)
        }
        binding.comment.setupEditor {
            viewModel.setComment(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.apLiveData.observe(viewLifecycleOwner, {
            binding.date.text = formatDate(requireContext(), it.dateTime)
            binding.time.text = formatTime(requireContext(), it.dateTime)
            with(it.systolic) {
                binding.systolic.setEditorText(if (this == 0) "" else toString())
            }
            with(it.diastolic) {
                binding.diastolic.setEditorText(if (this == 0) "" else toString(), false)
            }
            binding.comment.setEditorText(it.comment ?: "", false)
        })
        viewModel.errorSystolicEmptyLiveData.observe(viewLifecycleOwner, {
            binding.inputLayoutSystolic.showError(it, R.string.error_field_empty)
        })
        viewModel.errorDiastolicEmptyLiveData.observe(viewLifecycleOwner, {
            binding.inputLayoutDiastolic.showError(it, R.string.error_field_empty)
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                activity?.findViewById<View>(android.R.id.content)?.let { content ->
                    Snackbar.make(content, msg, LENGTH_SHORT).show()
                }
            }
        })
        viewModel.actionFinishLiveData.observe(viewLifecycleOwner, {
            findNavController().navigateUp()
        })
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}