package com.h2a.fitbook.adapters.schedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h2a.fitbook.R
import com.h2a.fitbook.models.ScheduleModel
import com.h2a.fitbook.utils.CommonFunctions
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleListAdapter(private val scheduleList: ArrayList<ScheduleModel>) :
    RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {

    var onItemClick: ((ScheduleModel) -> Unit)? = null

    inner class ViewHolder(scheduleItemView: View) : RecyclerView.ViewHolder(scheduleItemView) {
        val linearBackground =
            scheduleItemView.findViewById<LinearLayout>(R.id.list_schedule_item_ln_bg)!!
        val detailTv = scheduleItemView.findViewById<TextView>(R.id.list_schedule_item_tv_detail)!!
        val dateTv = scheduleItemView.findViewById<TextView>(R.id.list_schedule_item_tv_date)!!
        val dateLabelTv =
            scheduleItemView.findViewById<TextView>(R.id.list_schedule_item_tv_date_label)!!
        val dateLabelDetailTv =
            scheduleItemView.findViewById<TextView>(R.id.list_schedule_item_tv_date_label_detail)!!

        init {
            scheduleItemView.setOnClickListener {
                onItemClick?.invoke(scheduleList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val inflater = LayoutInflater.from(ctx)
        val listItemView = inflater.inflate(R.layout.list_schedule_item, parent, false)
        return ViewHolder(listItemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheduleItem = scheduleList[position]
        holder.detailTv.text =
            "${scheduleItem.totalExercises} Bài | ${scheduleItem.totalMinutes} Phút"
        holder.dateTv.text = scheduleItem.date
        holder.dateLabelTv.setText(CommonFunctions.mapDateToShortDateText(position + 2))
        holder.dateLabelDetailTv.setText(CommonFunctions.mapDateToFullDateText(position + 2))

        val today = LocalDate.now()
        val curDayOfWeek = today.dayOfWeek
        val scheduleDate =
            LocalDate.parse(scheduleItem.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val scheduleDayOfWeek = scheduleDate.dayOfWeek

        if (scheduleDayOfWeek == DayOfWeek.SUNDAY) {
            holder.linearBackground.setBackgroundResource(R.drawable.bg_schedule_item_sunday)
        }

        if (curDayOfWeek == scheduleDayOfWeek) {
            holder.dateTv.setText(R.string.schedule_list_item_tv_date)
            holder.dateTv.setTextColor(Color.parseColor("#ffffff"))
            holder.detailTv.setTextColor(Color.parseColor("#ffffff"))
            holder.dateLabelDetailTv.setTextColor(Color.parseColor("#ffffff"))
            holder.dateLabelTv.setTextColor(Color.parseColor("#ffffff"))
            holder.linearBackground.setBackgroundResource(R.drawable.bg_schedule_item_normal_active)

            if (scheduleDayOfWeek == DayOfWeek.SUNDAY) {
                holder.linearBackground.setBackgroundResource(R.drawable.bg_schedule_item_sunday_active)
            }
        }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }
}