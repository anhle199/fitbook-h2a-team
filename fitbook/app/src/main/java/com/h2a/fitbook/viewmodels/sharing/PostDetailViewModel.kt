package com.h2a.fitbook.viewmodels.sharing

import androidx.lifecycle.ViewModel

class PostDetailViewModel : ViewModel() {
    var _authorName = ""
    var _likeCount = 0
    var _cmtCount = 0
    var _postDate = ""
    var _postTime = ""
    var _imgLink = ""
    var _content = ""

    fun getDetail(id: String?) {
        _authorName = "Test Author"
        _likeCount = 10
        _cmtCount = 10
        _postDate = "06/04/2022"
        _postTime = "10:00 AM"
        _imgLink = "https://www.eatthis.com/wp-content/uploads/sites/4/2020/10/fast-food.jpg?quality=82&strip=1"
        _content = ""
    }
}