package com.axel_stein.ap_diary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.ap_diary.databinding.ItemLogBinding
import com.axel_stein.ap_diary.ui.home.log_items.ApLogItem
import com.axel_stein.ap_diary.ui.home.log_items.ItemType
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.home.log_items.PulseLogItem
import com.axel_stein.ap_diary.ui.utils.setVisible

class HomeAdapter : ListAdapter<LogItem, HomeAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<LogItem>() {
        override fun areItemsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id() && a.type() == b.type()
        }

        override fun areContentsTheSame(a: LogItem, b: LogItem): Boolean {
            return if (a is ApLogItem && b is ApLogItem) {
                a.id() == b.id() && a.log == b.log
            } else if (a is PulseLogItem && b is PulseLogItem) {
                a.id() == b.id() && a.log == b.log
            } else {
                true
            }
        }
    }

    var onItemClick: ((item: LogItem, itemView: View) -> Unit)? = null

    class ViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: LogItem) {
            when (item.type()) {
                ItemType.AP -> {
                    itemView.transitionName = "shared_element_ap_${item.id()}"
                }

                ItemType.PULSE -> {
                    itemView.transitionName = "shared_element_pulse_${item.id()}"
                }
            }
            binding.title.text = item.title()
            // binding.title.setTextColor(if (item.error()) 0 else 1)
            binding.suffix.text = item.suffix()
            with(item.comment()) {
                binding.comment.text = this
                binding.comment.setVisible(isNotEmpty())
            }
            binding.time.text = item.time()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding).apply {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition), itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }
}
