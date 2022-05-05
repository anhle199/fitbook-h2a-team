package com.h2a.fitbook.viewmodels.sharing

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.PostDetailModel

class PostDetailViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var postDetail: PostDetailModel

    fun getDetail(id: String?, callback: ((PostDetailModel) -> Unit), toast: ((String) -> Unit)) {
        db.collection("posts")
            .document(id!!)
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    it.data?.get("authorId")?.let { authorId ->
                        db.collection("users")
                            .document(authorId.toString())
                            .get()
                            .addOnSuccessListener { doc ->
                                postDetail = PostDetailModel(
                                    id,
                                    doc.data?.get("fullName").toString(),
                                    (it.data?.get("likes") as List<*>).contains(auth.uid),
                                    (it.data?.get("likes") as List<*>).size,
                                    it.data?.get("commentCount") as Long,
                                    (it.data?.get("postedAt") as Timestamp).toDate(),
                                    it.data?.get("image").toString(),
                                    it.data?.get("content").toString().replace("\\n", "\n"),
                                    authorId.toString() == auth.uid
                                )
                                callback(postDetail)
                            }
                            .addOnFailureListener { exception ->
                                toast("Có lỗi xảy ra trong quá trình tải dữ liệu.")
                                Log.i("Debug", "Error while loading user $exception")
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.i("Debug", "Error while fetching posts $it")
            }
    }

    fun postLike(id: String?, like: Boolean, callback: ((PostDetailModel) -> Unit), toast: ((String) -> Unit)) {
        if (like) {
            db.collection("posts")
                .document(id!!)
                .update("likes", FieldValue.arrayUnion(auth.uid))
                .addOnSuccessListener {
                    postDetail._likeCount += 1
                    postDetail._liked = like
                    callback(postDetail)
                }
                .addOnFailureListener { exception ->
                    toast("Có lỗi xảy ra.")
                    Log.i("Debug", "Error while posting like state $exception")
                }
        } else {
            db.collection("posts")
                .document(id!!)
                .update("likes", FieldValue.arrayRemove(auth.uid))
                .addOnSuccessListener {
                    postDetail._likeCount -= 1
                    postDetail._liked = like
                    callback(postDetail)
                }
                .addOnFailureListener { exception ->
                    toast("Có lỗi xảy ra.")
                    Log.i("Debug", "Error while posting like state $exception")
                }
        }
    }

    fun deletePost(id: String?, callback: (() -> Unit), toast: (String) -> Unit) {
        db.collection("posts")
            .document(id!!)
            .delete()
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                toast("Có lỗi xảy ra.")
                Log.i("Debug", "Error while deleting post $it")
            }
    }
}
