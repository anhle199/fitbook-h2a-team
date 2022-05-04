package com.h2a.fitbook.models

import java.util.*
import kotlin.collections.HashMap

class NewPostModel(
    var _authorId: String,
    var _content: String,
    var _postedAt: Date
) {
    private var _image = ""
    private val _commentCount = 0
    private val _likes = arrayListOf<String>()

    fun setImgLink(link: String) {
        _image = link
    }

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "authorId" to _authorId,
            "commentCount" to _commentCount,
            "content" to _content,
            "likes" to _likes,
            "image" to _image,
            "postedAt" to _postedAt
        )
    }
}