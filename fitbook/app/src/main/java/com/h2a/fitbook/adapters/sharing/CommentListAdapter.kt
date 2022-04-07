package com.h2a.fitbook.adapters.sharing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h2a.fitbook.R
import com.h2a.fitbook.models.CommentModel

class CommentListAdapter(private val comments: List<CommentModel>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val commentAuthorName: TextView = listItemView.findViewById(R.id.comment_list_item_tv_author_name)
        val commentContent: TextView = listItemView.findViewById(R.id.comment_list_item_tv_comment_content)
        val commentDate: TextView = listItemView.findViewById(R.id.comment_list_item_tv_comment_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_comment_list_item, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentAuthorName.text = comments[position]._authorName
        holder.commentContent.text = comments[position]._cmtContent
        holder.commentDate.text = comments[position]._cmtDate
    }

    override fun getItemCount() = comments.size
}