package com.martin.morning.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.martin.morning.R
import com.martin.morning.data.AlarmEntity
import com.martin.morning.data.RepeatType

class AlarmAdapter(
    private val onToggle: (AlarmEntity, Boolean) -> Unit,
    private val onClick: (AlarmEntity) -> Unit
) : ListAdapter<AlarmEntity, AlarmAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val tvRepeat: TextView = itemView.findViewById(R.id.tv_repeat)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val switch: SwitchMaterial = itemView.findViewById(R.id.switch_enabled)

        fun bind(alarm: AlarmEntity) {
            tvTime.text = String.format("%02d:%02d", alarm.hour, alarm.minute)
            tvRepeat.text = formatRepeat(alarm)
            // TODO: 加载分类名和颜色
            tvCategory.text = "分类"

            switch.isChecked = alarm.isEnabled
            itemView.alpha = if (alarm.isEnabled) 1f else 0.5f

            switch.setOnCheckedChangeListener { _, checked ->
                onToggle(alarm, checked)
            }
            itemView.setOnClickListener { onClick(alarm) }
        }

        private fun formatRepeat(alarm: AlarmEntity): String {
            return when (alarm.repeatType) {
                RepeatType.DAILY -> "每天"
                RepeatType.WEEKDAYS -> "仅工作日"
                RepeatType.ONCE -> "仅此一次"
                RepeatType.CUSTOM -> {
                    val names = alarm.customDays.split(",").mapNotNull {
                        when (it.trim().toIntOrNull()) {
                            1 -> "一"
                            2 -> "二"
                            3 -> "三"
                            4 -> "四"
                            5 -> "五"
                            6 -> "六"
                            7 -> "日"
                            else -> null
                        }
                    }
                    if (names.isEmpty()) "未设置" else names.joinToString(" · ")
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AlarmEntity>() {
        override fun areItemsTheSame(a: AlarmEntity, b: AlarmEntity) = a.id == b.id
        override fun areContentsTheSame(a: AlarmEntity, b: AlarmEntity) = a == b
    }
}
