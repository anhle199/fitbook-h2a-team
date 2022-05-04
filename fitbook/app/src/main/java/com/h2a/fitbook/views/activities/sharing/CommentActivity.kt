package com.h2a.fitbook.views.activities.sharing

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.sharing.CommentListAdapter
import com.h2a.fitbook.databinding.ActivityCommentBinding
import com.h2a.fitbook.viewmodels.sharing.CommentViewModel
import okhttp3.internal.notifyAll

class CommentActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCommentBinding
    private lateinit var adapter: CommentListAdapter
    private var toast: Toast? = null
    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private val notifyInsert = { position: Int ->
        if (position == -1) {
            adapter.notifyAll()
        } else {
            adapter.notifyAt(position)
        }
        adapter.sortDate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityCommentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.comment_activity_title)

        val postId = intent.getStringExtra("PostId")

        val commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentViewModel.loadComment(postId, notifyInsert, toastCallback)

        val rcvList = _binding.commentRcvCommentList

        adapter = CommentListAdapter(commentViewModel.commentList)

        rcvList.adapter = adapter

        rcvList.layoutManager = LinearLayoutManager(this)

        _binding.commentTilCommentInput.setEndIconOnClickListener {
            val commentContent =  _binding.commentEtCommentInput.text!!.toString().trim()

            if (commentContent != "") {
                // check empty content
                commentViewModel.postComment(postId, _binding.commentEtCommentInput.text!!.toString(), notifyInsert, toastCallback)
                _binding.commentEtCommentInput.setText("")
            }
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