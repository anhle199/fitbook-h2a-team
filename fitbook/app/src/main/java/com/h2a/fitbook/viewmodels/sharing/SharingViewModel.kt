package com.h2a.fitbook.viewmodels.sharing

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.OverviewPostModel

class SharingViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    val posts: MutableList<OverviewPostModel> = arrayListOf()

    fun loadPostList(notifyAdapter: ((Int) -> Unit), toast: ((String) -> Unit)) {
        posts.clear()
        notifyAdapter(posts.size - 1)
        db.collection("posts")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    db.collection("users")
                        .document(document.data["authorId"].toString())
                        .get()
                        .addOnSuccessListener { doc ->
                            db.collection("posts")
                            posts.add(OverviewPostModel(
                                document.id,
                                document.data["image"].toString(),
                                doc.data?.get("fullName").toString(),
                                (document.data["postedAt"] as Timestamp).toDate(),
                                document.data["content"].toString(),
                                (document.data["likes"] as List<*>).size,
                                document.data["commentCount"] as Long
                            ))
                            notifyAdapter(posts.size - 1)
                        }
                        .addOnFailureListener { exception ->
                            toast("Có lỗi xảy ra trong quá trình tải bài đăng!")
                            Log.i("Debug", "Error while loading user $exception")
                        }
                }
            }
            .addOnFailureListener {
                Log.i("Debug", it.toString())
            }
    }

    fun changeSort(type: Int, notifyAdapter: ((Int) -> Unit)) {
        when (type) {
            0 -> posts.sortByDescending { it._postAt }
            1 -> posts.sortByDescending { it._likeCount }
            else -> posts.sortByDescending { it._cmtCount }
        }
        notifyAdapter(-1)
    }
}
