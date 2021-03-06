package com.h2a.fitbook.adapters.sharing

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.models.OverviewPostModel
import java.text.SimpleDateFormat

class SharingPostListAdapter(private val posts: List<OverviewPostModel>) : RecyclerView.Adapter<SharingPostListAdapter.ViewHolder>() {
    var onItemClick: ((OverviewPostModel) -> Unit)? = null

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val postImgView: ImageView = listItemView.findViewById(R.id.post_list_item_img_post_image)
        val postAuthorName: TextView = listItemView.findViewById(R.id.post_list_item_tv_author_name)
        val postDate: TextView = listItemView.findViewById(R.id.post_list_item_tv_post_date)
        val postContent: TextView = listItemView.findViewById(R.id.post_list_item_tv_post_content)
        val postLike: TextView = listItemView.findViewById(R.id.post_list_item_tv_post_like_count)
        val postComment: TextView = listItemView.findViewById(R.id.post_list_item_tv_post_comment_count)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(posts[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.rcv_post_list_item, parent, false)
        return ViewHolder(rowView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (posts[position]._imgLink.isEmpty()) {
            holder.postImgView.setImageDrawable(holder.postImgView.context.getDrawable(R.drawable.bg_placeholder))
        } else {
            holder.postImgView.load(posts[position]._imgLink) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
        }
        holder.postAuthorName.text = posts[position]._authorName
        holder.postDate.text = holder.postDate.context.getString(R.string.post_list_date_placeholder, SimpleDateFormat(holder.postDate.context.getString(R.string.date_format)).format(posts[position]._postAt))
        holder.postContent.text = posts[position]._content
        holder.postContent.text = (holder.postContent.text as String).replace("\\n", "\n")
        holder.postLike.text = holder.postLike.context.getString(R.string.post_list_like_text_placeholder, posts[position]._likeCount.toString())
        holder.postComment.text = holder.postComment.context.getString(R.string.post_list_comment_text_placeholder, posts[position]._cmtCount.toString())
    }

    override fun getItemCount() = posts.size
}
