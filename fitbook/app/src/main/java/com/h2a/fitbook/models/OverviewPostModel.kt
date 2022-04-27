package com.h2a.fitbook.models

import java.util.*

class OverviewPostModel(
    var _id: String,
    var _imgLink: String,
    var _authorName: String,
    var _postAt: Date,
    var _content: String,
    var _likeCount: Int,
    var _cmtCount: Long) {}
