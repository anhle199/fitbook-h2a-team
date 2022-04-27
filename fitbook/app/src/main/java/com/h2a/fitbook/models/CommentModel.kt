package com.h2a.fitbook.models

import java.util.*
import kotlin.collections.HashMap

class CommentModel(
    var _id: String,
    var _avatarImgLink: String,
    var _authorName: String,
    var _cmtDate: Date,
    var _cmtContent: String) {
    fun toHashMap(userId: String): HashMap<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "content" to _cmtContent,
            "commentAt" to _cmtDate
        )
    }
}
