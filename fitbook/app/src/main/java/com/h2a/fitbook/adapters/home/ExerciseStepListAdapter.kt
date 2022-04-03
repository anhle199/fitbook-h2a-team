package com.h2a.fitbook.adapters.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h2a.fitbook.R

class ExerciseStepListAdapter(private val stepList: ArrayList<String>) :
    RecyclerView.Adapter<ExerciseStepListAdapter.ViewHolder>() {
    inner class ViewHolder(stepItemView: View) : RecyclerView.ViewHolder(stepItemView) {
        val step: TextView = stepItemView.findViewById(R.id.list_exercise_step_item_tv_step)
        val description: TextView =
            stepItemView.findViewById(R.id.list_exercise_step_item_tv_step_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val inflater = LayoutInflater.from(ctx)
        val listViewItem = inflater.inflate(R.layout.list_exercise_step_item, parent, false)
        return ViewHolder(listViewItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = stepList[position]
        holder.step.text = "${position + 1}"
        holder.description.text = step
    }

    override fun getItemCount(): Int {
        return stepList.size
    }
}