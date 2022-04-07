package com.h2a.fitbook.views.activities.sharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModelProvider
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityShareNewPostBinding
import com.h2a.fitbook.viewmodels.sharing.NewPostViewModel

class ShareNewPostActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityShareNewPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityShareNewPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.share_new_post_activity_title)

        val shareNewPostViewModel = ViewModelProvider(this)[NewPostViewModel::class.java]

        _binding.shareNewPostFabChooseImage.setOnClickListener {
            shareNewPostViewModel.loadImage(this)
        }

        _binding.shareNewPostFabSubmit.setOnClickListener {
            finish()
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