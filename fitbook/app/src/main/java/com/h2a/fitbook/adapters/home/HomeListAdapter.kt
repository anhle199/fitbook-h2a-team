package com.h2a.fitbook.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.ExerciseModel

class HomeListAdapter(private val exerciseList: List<ExerciseModel>) :
    RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    var onItemClick: ((ExerciseModel) -> Unit)? = null
    var onDetailButtonClick: ((ExerciseModel) -> Unit)? = null

    inner class ViewHolder(exerciseItemView: View) : RecyclerView.ViewHolder(exerciseItemView) {
        val imgThumbnail: ImageView =
            exerciseItemView.findViewById(R.id.list_exercise_item_img_thumbnail)
        val title: TextView = exerciseItemView.findViewById(R.id.list_exercise_item_tv_title)
        val detail: TextView = exerciseItemView.findViewById(R.id.list_exercise_item_tv_detail)

        init {
            exerciseItemView.setOnClickListener {
                onItemClick?.invoke(exerciseList[adapterPosition])
            }

            val viewDetailBtn: Button = exerciseItemView.findViewById(R.id.list_exercise_item_btn_view_detail)
            viewDetailBtn.setOnClickListener {
                onDetailButtonClick?.invoke(exerciseList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val inflater = LayoutInflater.from(ctx)
        val listItemView = inflater.inflate(R.layout.list_exercise_item, parent, false)
        return ViewHolder(listItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { // Get model
        val exercise = exerciseList[position]

        holder.imgThumbnail.load(exercise.image) {
            crossfade(true)
            placeholder(R.drawable.bg_placeholder)
        }
        holder.title.text = exercise.title

        val detailString = "${exercise.duration} Phút | ${exercise.calories} Calo"
        holder.detail.text = detailString
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }
}