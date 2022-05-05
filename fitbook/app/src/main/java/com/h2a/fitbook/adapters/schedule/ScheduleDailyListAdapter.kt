package com.h2a.fitbook.adapters.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.ExerciseDailyListItemModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleDailyListAdapter(private val exerciseDailyList: ArrayList<ExerciseDailyListItemModel>) :
    RecyclerView.Adapter<ScheduleDailyListAdapter.ViewHolder>() {

    var onItemClick: ((ExerciseDailyListItemModel) -> Unit)? = null

    inner class ViewHolder(exerciseItemView: View) : RecyclerView.ViewHolder(exerciseItemView) {
        val imgThumbnail: ImageView =
            exerciseItemView.findViewById(R.id.list_exercise_daily_item_img_thumbnail)
        val title: TextView = exerciseItemView.findViewById(R.id.list_exercise_daily_item_tv_title)
        val detail: TextView =
            exerciseItemView.findViewById(R.id.list_exercise_daily_item_tv_detail)
        val date: TextView = exerciseItemView.findViewById(R.id.list_exercise_daily_item_tv_date)

        init {
            exerciseItemView.setOnClickListener {
                onItemClick?.invoke(exerciseDailyList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val inflater = LayoutInflater.from(ctx)
        val listItemView = inflater.inflate(R.layout.list_exercise_daily_item, parent, false)
        return ViewHolder(listItemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exerciseDailyList[position]

        holder.imgThumbnail.load(exercise.image) {
            crossfade(true)
            placeholder(R.drawable.bg_placeholder)
        }
        holder.title.text = exercise.name

        val detailString =
            "${exercise.duration / 60} Phút | ${exercise.measureCalories * exercise.duration} Calo"
        holder.detail.text = detailString
        holder.date.text =
            LocalDateTime.parse(exercise.scheduleDate).format(DateTimeFormatter.ofPattern("HH:mm"))
                .toString()
    }

    override fun getItemCount(): Int {
        return exerciseDailyList.size
    }
}