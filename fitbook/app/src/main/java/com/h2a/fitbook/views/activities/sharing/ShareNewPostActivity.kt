package com.h2a.fitbook.views.activities.sharing

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityShareNewPostBinding
import com.h2a.fitbook.viewmodels.sharing.NewPostViewModel
import java.lang.NullPointerException

class ShareNewPostActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityShareNewPostBinding
    private lateinit var shareNewPostViewModel: NewPostViewModel
    private var toast: Toast? = null
    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    private val callbackSubmit = { msg: String ->
        if (msg == "OK") {
            toastCallback("Đăng bài viết thành công!")
            finish()
        } else {
            toastCallback(msg)
        }
    }

    private val getSecondActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    shareNewPostViewModel.initImg(result.data?.data!!)
                    _binding.shareNewPostImgPostImage.setImageURI(null)
                    _binding.shareNewPostImgPostImage.setImageURI(shareNewPostViewModel.getImgUri())
                } catch (e: NullPointerException) {
                    toastCallback("Có lỗi xảy ra!")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityShareNewPostBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.share_new_post_activity_title)

        shareNewPostViewModel = ViewModelProvider(this)[NewPostViewModel::class.java]

        _binding.shareNewPostFabChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            // launch intent to choose image
            getSecondActivityIntent.launch(intent)
        }

        _binding.shareNewPostFabSubmit.setOnClickListener {
            // get post content and check empty
            val postContent = _binding.shareNewPostEtPostContent.text.toString().trim()

            if (postContent == "") {
                _binding.shareNewPostTilPostContent.error = "Trường bỏ trống."
                toastCallback("Vui lòng không để trống nội dung.")
            } else {
                _binding.shareNewPostTilPostContent.error = null

                // call submit method of view model
                shareNewPostViewModel.submit(this, postContent, callbackSubmit, toastCallback)
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