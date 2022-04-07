package com.h2a.fitbook.views.activities.sharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityPostDetailBinding
import com.h2a.fitbook.viewmodels.sharing.PostDetailViewModel

class PostDetailActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityPostDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.post_detail_activity_title)

        val postId = intent.getStringExtra("PostId")

        val postDetailViewModel = ViewModelProvider(this)[PostDetailViewModel::class.java]
        postDetailViewModel.getDetail(postId)
        _binding.postDetailTvAuthorName.text = postDetailViewModel._authorName
        _binding.postDetailTvDate.text = getString(R.string.post_detail_date_placeholder, postDetailViewModel._postDate, postDetailViewModel._postTime)
        _binding.postDetailTvLike.text = getString(R.string.post_list_like_text_placeholder, postDetailViewModel._likeCount.toString())
        _binding.postDetailTvComment.text = getString(R.string.post_list_comment_text_placeholder, postDetailViewModel._cmtCount.toString())
        _binding.postDetailImgPostImage.load(postDetailViewModel._imgLink)
        _binding.postDetailTvPostContent.text = getString(R.string.about_description_text)

        _binding.postDetailFabComment.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("PostId", postId)

            startActivity(intent)
        }
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