package com.h2a.fitbook.viewmodels.sharing

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.CommentModel
import com.h2a.fitbook.models.PostDetailModel
import java.util.*

class CommentViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    var commentList: MutableList<CommentModel> = arrayListOf()
    private var auth = FirebaseAuth.getInstance()

    fun loadComment(postId: String?, callback: ((Int) -> Unit), toast: ((String) -> Unit)) {
        commentList.clear()
        db.collection("posts")
            .document(postId!!)
            .collection("comments")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    db.collection("users")
                        .document(document.data["userId"].toString())
                        .get()
                        .addOnSuccessListener { userInfo ->
                            commentList.add(CommentModel(document.id,
                                userInfo.data?.get("profileImage").toString(),
                                userInfo.data?.get("fullName").toString(),
                                (document.data["commentAt"] as Timestamp).toDate(),
                                document.data["content"].toString()))
                            callback(commentList.size - 1)
                        }
                        .addOnFailureListener { exception ->
                            toast("Có lỗi xảy ra trong quá trình tải bình luận!")
                            Log.i("Debug", "Error while fetching comments $exception")
                        }
                }
            }
            .addOnFailureListener {
                Log.i("Debug", "Error while fetching comments $it")
            }
    }

    fun postComment(postId: String?, content: String, callback: (Int) -> Unit, toast: ((String) -> Unit)) {
        db.collection("posts")
            .document(postId!!)
            .collection("comments")
            .add(CommentModel("",
                "",
                "",
                Date(), content
            ).toHashMap(auth.uid!!)).addOnSuccessListener {
                db.collection("posts")
                    .document(postId)
                    .update("commentCount", commentList.size + 1)
                    .addOnFailureListener { exception ->
                        Log.i("Debug", "Update comment count failed $exception")
                    }
                it.get()
                    .addOnSuccessListener { comment ->
                    db.collection("users")
                        .document(comment.get("userId").toString())
                        .get().addOnSuccessListener { userInfo ->
                            commentList.add(CommentModel(it.id,
                                userInfo.data?.get("profileImage").toString(),
                                userInfo.data?.get("fullName").toString(),
                                (comment.data?.get("commentAt") as Timestamp).toDate(),
                                comment.data?.get("content").toString()))
                            callback(commentList.size - 1)
                        }
                        .addOnFailureListener { ex ->
                            toast("Có lỗi xảy ra trong quá trình tải bình luận!")
                            Log.i("Debug", ex.toString())
                        }
                    }
                    .addOnFailureListener { ex ->
                        toast("Có lỗi xảy ra trong quá trình tải bình luận!")
                        Log.i("Debug", ex.toString())
                    }
            }
            .addOnFailureListener {
                toast("Có lỗi xảy ra trong quá trình gửi bình luận!")
                Log.i("Debug", it.toString())
            }
    }
}
