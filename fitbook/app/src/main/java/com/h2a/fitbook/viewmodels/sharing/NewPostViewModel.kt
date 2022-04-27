package com.h2a.fitbook.viewmodels.sharing

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.h2a.fitbook.R
import com.h2a.fitbook.models.NewPostModel
import java.text.SimpleDateFormat
import java.util.*

class NewPostViewModel: ViewModel() {
    private var _imgUri: Uri? = null
    private var auth = FirebaseAuth.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var newPost: NewPostModel
    private lateinit var callbackUI: (String) -> Unit

    private val saveToDB = { url: String ->
        if (url == "") {
            db.collection("posts")
                .add(newPost.toHashMap())
                .addOnSuccessListener {
                    callbackUI("OK")
                }
                .addOnFailureListener {
                    callbackUI("Có lỗi xảy ra trong quá trình đăng bài viết!")
                    Log.i("Debug", it.toString())
                }
        } else {
            newPost.setImgLink(url)
            db.collection("posts")
                .add(newPost.toHashMap())
                .addOnSuccessListener {
                    callbackUI("OK")
                }
                .addOnFailureListener {
                    callbackUI("Có lỗi xảy ra trong quá trình đăng bài viết!")
                    Log.i("Debug", it.toString())
                }
        }
    }

    fun initImg(img: Uri) {
        _imgUri = img
    }

    fun getImgUri() = _imgUri

    @SuppressLint("SimpleDateFormat")
    private fun uploadImg(context: Context, callBack: ((String) -> Any), toast: ((String) -> Unit)) {
        if (_imgUri == null) {
            callBack("")
            return
        } else {
            val formatter = SimpleDateFormat(context.getString(R.string.date_time_format_upload_image_name))
            storage.getReference("/post-images/${formatter.format(Date())}")
                .putFile(_imgUri!!)
                .addOnSuccessListener {
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        callBack(url.toString())
                    }
                }
                .addOnFailureListener {
                    toast("Upload thất bại!")
                    Log.i("Debug", "Error while uploading image $it")
                }
        }
    }

    fun submit(context: Context, postContent: String, callBack: (String) -> Unit, toast: (String) -> Unit) {
        newPost = NewPostModel(auth.uid!!, postContent, Date())
        callbackUI = callBack
        toast("Đang đăng bài viết ...")
        uploadImg(context, saveToDB, toast)
    }

}