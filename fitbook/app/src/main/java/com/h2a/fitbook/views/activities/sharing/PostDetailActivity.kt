package com.h2a.fitbook.views.activities.sharing

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityPostDetailBinding
import com.h2a.fitbook.models.PostDetailModel
import com.h2a.fitbook.viewmodels.sharing.PostDetailViewModel
import java.text.SimpleDateFormat

class PostDetailActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityPostDetailBinding
    private var postId: String = ""
    private var likeState = false
    private var toast: Toast? = null
    private lateinit var postDetailViewModel: PostDetailViewModel
    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
    private val updateCallback = { data: PostDetailModel ->
        _binding.postDetailTvAuthorName.text = data._authorName
        _binding.postDetailTvPostContent.text = data._content
        if (data._imgLink != "") {
            _binding.postDetailImgPostImage.load(data._imgLink)
        } else _binding.postDetailImgPostImage.setImageDrawable(this.getDrawable(R.drawable.bg_placeholder))
        _binding.postDetailTvDate.text = getString(R.string.post_detail_date_placeholder, SimpleDateFormat(this.getString(R.string.date_format)).format(data._postDate), SimpleDateFormat(this.getString(R.string.time_format)).format(data._postDate))
        _binding.postDetailTvLike.text = getString(R.string.post_list_like_text_placeholder, data._likeCount.toString())
        _binding.postDetailTvComment.text = getString(R.string.post_list_comment_text_placeholder, data._cmtCount.toString())
        if (data._liked) {
            _binding.postDetailFabLike.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fab_like_color)
            likeState = true
        }
        _binding.postDetailLayoutLoading.visibility = View.GONE
        _binding.postDetailPbLoading.visibility = View.GONE
    }
    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityPostDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.post_detail_activity_title)

        postId = intent.getStringExtra("PostId").toString()

        postDetailViewModel = ViewModelProvider(this)[PostDetailViewModel::class.java]

        _binding.postDetailFabComment.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("PostId", postId)

            startActivity(intent)
        }

        _binding.postDetailFabLike.setOnClickListener {
            _binding.postDetailFabLike.backgroundTintList = if (likeState) ContextCompat.getColorStateList(this, R.color.primary) else ContextCompat.getColorStateList(this, R.color.fab_like_color)

            likeState = !likeState
            postDetailViewModel.postLike(postId, likeState, updateCallback, toastCallback)
        }
    }

    override fun onStart() {
        super.onStart()
        postDetailViewModel.getDetail(postId, updateCallback, toastCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
