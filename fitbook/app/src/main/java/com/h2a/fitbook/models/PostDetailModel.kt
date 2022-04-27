package com.h2a.fitbook.models

import java.util.*

class PostDetailModel(
    var _id: String,
    var _authorName: String,
    var _liked: Boolean,
    var _likeCount: Int,
    var _cmtCount: Long,
    var _postDate: Date,
    var _imgLink: String,
    var _content: String
) {}