package com.h2a.fitbook.adapters.healthmanagement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.Food

class AddFoodListAdapter(private val foodList: List<Food>, private val onItemClick: ((Food) -> Unit)? = null) : RecyclerView.Adapter<AddFoodListAdapter.ViewHolder>(), Filterable {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val foodImgView: ImageView = listItemView.findViewById(R.id.add_food_list_item_img_food_img)
        val foodNameTextView: TextView = listItemView.findViewById(R.id.add_food_list_item_tv_food_name)
        val foodCaloriesTextView: TextView = listItemView.findViewById(R.id.add_food_list_item_tv_food_calories)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(filteredList[adapterPosition])
            }
        }
    }

    private var filteredList: List<Food> = foodList.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_add_food_list_item, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foodImgView.load(filteredList[position]._imgLink) {
            crossfade(true)
            placeholder(R.drawable.bg_placeholder)
        }
        holder.foodImgView.clipToOutline = true
        holder.foodNameTextView.text = filteredList[position]._name
        holder.foodCaloriesTextView.text = holder.foodCaloriesTextView.context.getString(R.string.add_food_list_unit, filteredList[position]._calories, filteredList[position]._unit)
    }

    override fun getItemCount() = filteredList.size

    fun notifyChangeAt(index: Int) {
        filteredList = foodList.toList()
        notifyItemInserted(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChangeAll() {
        filteredList = foodList.toList()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val searchValue = p0.toString().trim() ?: ""
                filteredList = if (searchValue.isEmpty()) {
                    foodList
                } else {
                    foodList.filter {
                        it._name.contains(searchValue, true)
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}
