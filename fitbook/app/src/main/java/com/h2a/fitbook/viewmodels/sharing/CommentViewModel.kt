package com.h2a.fitbook.viewmodels.sharing

import androidx.lifecycle.ViewModel
import com.h2a.fitbook.models.CommentModel

class CommentViewModel : ViewModel() {
    private var commentList: MutableList<CommentModel> = arrayListOf()

    fun loadComment(postId: String?) {
        for (i in 0..10) {
            commentList.add(CommentModel(
                i.toString(),
                "",
                "Test Author",
                "06/04/2022 - 10:00 AM",
                "Xin chào mọi người!"))
        }
    }

    fun getCommentList() = commentList
}