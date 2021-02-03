package com.axel_stein.ap_diary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.ap_diary.R
import com.axel_stein.ap_diary.ui.home.log_items.LogItem
import kotlin.random.Random

class HomeAdapter : ListAdapter<LogItem, HomeAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<LogItem>() {
        override fun areItemsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id() && a.type() == a.type()
        }

        override fun areContentsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id() && a.type() == a.type() // fixme
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val l = listOf(R.layout.item_log, R.layout.item_date)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val res = Random.nextInt(2).run {
            l[this]
        }
        return ViewHolder(parent.inflate(res))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}

private fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}
