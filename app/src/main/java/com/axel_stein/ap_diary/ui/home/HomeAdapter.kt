package com.axel_stein.ap_diary.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.databinding.ItemLogBinding
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import com.axel_stein.ap_diary.ui.utils.inflate
import com.axel_stein.ap_diary.ui.utils.setVisible

class HomeAdapter : ListAdapter<LogItem, HomeAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<LogItem>() {
        override fun areItemsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id()
        }

        override fun areContentsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id() // fixme
        }
    }

    var onItemClick: ((item: LogItem) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemLogBinding.bind(view)

        fun setItem(item: LogItem) {
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
        return ViewHolder(parent.inflate(R.layout.item_log)).apply {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }
}
