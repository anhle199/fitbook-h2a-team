package com.h2a.fitbook.views.fragments.sharing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.sharing.SharingPostListAdapter
import com.h2a.fitbook.databinding.FragmentSharingBinding
import com.h2a.fitbook.models.OverviewPostModel
import com.h2a.fitbook.views.activities.sharing.PostDetailActivity
import com.h2a.fitbook.views.activities.sharing.ShareNewPostActivity

class SharingFragment : Fragment() {
    lateinit var _binding: FragmentSharingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharingBinding.inflate(inflater, container, false)
        val root: View = _binding.root

        val rcvList = _binding.sharingRcvPostList

        val testData: MutableList<OverviewPostModel> = arrayListOf()

        for (i in 0..10) {
            testData.add(OverviewPostModel(i.toString(), "https://www.eatthis.com/wp-content/uploads/sites/4/2020/10/fast-food.jpg?quality=82&strip=1", "Test Author", "06/04/2022", getString(R.string.about_description_text), 10, 10))
        }

        val adapter = SharingPostListAdapter(testData)

        rcvList.adapter = adapter

        rcvList.layoutManager = LinearLayoutManager(activity)

        rcvList.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        adapter.onItemClick = {
            val intent = Intent(activity, PostDetailActivity::class.java)
            intent.putExtra("PostId", it._id)

            startActivity(intent)
        }

        _binding.sharingFabPost.setOnClickListener {
            val intent = Intent(activity, ShareNewPostActivity::class.java)

            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return root
    }
}