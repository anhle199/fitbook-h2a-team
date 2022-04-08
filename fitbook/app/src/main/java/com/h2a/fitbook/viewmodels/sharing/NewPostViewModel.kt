package com.h2a.fitbook.viewmodels.sharing

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import coil.load
import com.h2a.fitbook.R

class NewPostViewModel: ViewModel() {
    var _imgLink = "https://www.eatthis.com/wp-content/uploads/sites/4/2020/10/fast-food.jpg?quality=82&strip=1"

    fun loadImage(activity: AppCompatActivity) {
        if (_imgLink != "") {
            val imgView = activity.findViewById<ImageView>(R.id.share_new_post_img_post_image)
            imgView.load(_imgLink)
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}