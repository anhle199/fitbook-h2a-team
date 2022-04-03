package com.h2a.fitbook.adapters.healthmanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.HealthManagementFood

class AddFoodListAdapter(private val foodList: List<HealthManagementFood>) : RecyclerView.Adapter<AddFoodListAdapter.ViewHolder>()  {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val foodImgView: ImageView = listItemView.findViewById(R.id.add_food_list_item_img_food_img)
        val foodNameTextView: TextView = listItemView.findViewById(R.id.add_food_list_item_tv_food_name)
        val foodCaloriesTextView: TextView = listItemView.findViewById(R.id.add_food_list_item_tv_food_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_add_food_list_item, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foodImgView.load(foodList[position]._imgLink)
        holder.foodImgView.clipToOutline = true
        holder.foodNameTextView.text = foodList[position]._name
        holder.foodCaloriesTextView.text = holder.foodCaloriesTextView.context.getString(R.string.food_list_unit, foodList[position]._calories.toString())
    }

    override fun getItemCount() = foodList.size
}
