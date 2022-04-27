package com.h2a.fitbook.adapters.sharing

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h2a.fitbook.R
import com.h2a.fitbook.models.CommentModel
import java.text.SimpleDateFormat

class CommentListAdapter(private val comments: List<CommentModel>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val commentAuthorName: TextView = listItemView.findViewById(R.id.comment_list_item_tv_author_name)
        val commentContent: TextView = listItemView.findViewById(R.id.comment_list_item_tv_comment_content)
        val commentDate: TextView = listItemView.findViewById(R.id.comment_list_item_tv_comment_date)
    }

    private var displayComments = comments

    @SuppressLint("NotifyDataSetChanged")
    fun sortDate() {
        displayComments = comments.sortedBy { it._cmtDate }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyFull() {
        displayComments = comments
        notifyDataSetChanged()
    }

    fun notifyAt(position: Int) {
        displayComments = comments
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_comment_list_item, parent, false)
        return ViewHolder(rowView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentAuthorName.text = displayComments[position]._authorName
        holder.commentContent.text = displayComments[position]._cmtContent
        holder.commentDate.text = SimpleDateFormat(holder.commentDate.context.getString(R.string.date_time_format)).format(displayComments[position]._cmtDate)
    }

    override fun getItemCount() = displayComments.size
}