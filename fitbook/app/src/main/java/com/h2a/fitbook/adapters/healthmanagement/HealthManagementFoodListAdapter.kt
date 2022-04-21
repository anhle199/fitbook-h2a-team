package com.h2a.fitbook.adapters.healthmanagement

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.HealthManagementFood

class HealthManagementFoodListAdapter(private val foods: List<HealthManagementFood>) : RecyclerView.Adapter<HealthManagementFoodListAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val foodImgView: ImageView = listItemView.findViewById(R.id.food_list_item_img_food_img)
        val foodNameTextView: TextView = listItemView.findViewById(R.id.food_list_item_tv_food_name)
        val foodCaloriesTextView: TextView = listItemView.findViewById(R.id.food_list_item_tv_food_calories)
    }

    private var displayList: List<HealthManagementFood> = foods.toList()
    private val itemTouchHelperCallBack = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            displayList = displayList.filterNot { it == displayList[position] }
//            for (item in displayList) {
//                Log.i("Debug", item._imgLink + " " + item._name)
//            }
            notifyDataSetChanged()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, 4 * dX / 5, dY, actionState, isCurrentlyActive)
        }
    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)

    fun setSwipeState(state: Boolean, rcv: RecyclerView) {
        if (state)
            itemTouchHelper.attachToRecyclerView(rcv)
        else
            itemTouchHelper.attachToRecyclerView(null)
    }

    fun getCurrentListChanges(): List<HealthManagementFood> = foods.filterNot { displayList.contains(it) }

    fun notifyChangeAt(index: Int) {
        displayList = foods.toList()
        notifyItemInserted(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChangeAll() {
        displayList = foods.toList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun cancelEdit() {
        displayList = foods.toList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_health_management_food_list_item, parent, false)
        return ViewHolder(rowView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (displayList[position]._imgLink.isEmpty()) {
            holder.foodImgView.setImageDrawable(holder.foodImgView.context.getDrawable(R.drawable.ic_default_food_image))
        } else {
            holder.foodImgView.load(displayList[position]._imgLink) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
        }
        holder.foodImgView.clipToOutline = true
        holder.foodNameTextView.text = displayList[position]._name
        holder.foodCaloriesTextView.text = holder.foodCaloriesTextView.context.getString(R.string.food_list_unit, displayList[position]._calories * displayList[position]._quantity, displayList[position]._quantity.toString(), displayList[position]._unit)
    }

    override fun getItemCount() = displayList.size
}
