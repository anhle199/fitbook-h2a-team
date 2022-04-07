package com.h2a.fitbook.views.activities.sharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.sharing.CommentListAdapter
import com.h2a.fitbook.databinding.ActivityCommentBinding
import com.h2a.fitbook.viewmodels.sharing.CommentViewModel

class CommentActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityCommentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.comment_activity_title)

        val commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentViewModel.loadComment("postId")
        val commentList = commentViewModel.getCommentList()

        val rcvList = _binding.commentRcvCommentList

        val adapter = CommentListAdapter(commentList)

        rcvList.adapter = adapter

        rcvList.layoutManager = LinearLayoutManager(this)
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