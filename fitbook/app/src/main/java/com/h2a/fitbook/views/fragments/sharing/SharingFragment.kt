package com.h2a.fitbook.views.fragments.sharing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.sharing.SharingPostListAdapter
import com.h2a.fitbook.databinding.FragmentSharingBinding
import com.h2a.fitbook.viewmodels.sharing.SharingViewModel
import com.h2a.fitbook.views.activities.sharing.PostDetailActivity
import com.h2a.fitbook.views.activities.sharing.ShareNewPostActivity

class SharingFragment : Fragment() {
    private lateinit var _binding: FragmentSharingBinding
    private lateinit var adapter: SharingPostListAdapter
    private lateinit var sharingVM: SharingViewModel
    private var selectedFilter = 0 // default filter
    private var toast: Toast? = null
    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private val notifyInsert = { position: Int ->
        if (position == -1) {
            adapter.notifyDataSetChanged()
        } else {
            adapter.notifyItemInserted(position)
        }
    }

    private fun changeFilter() {
        sharingVM.changeSort(selectedFilter, notifyInsert)
    }

    private fun showFilterDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chọn cách sắp xếp")
            .setItems(R.array.post_filter) { _, i ->
                selectedFilter = i
                changeFilter()
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharingBinding.inflate(inflater, container, false)
        val root: View = _binding.root

        val rcvList = _binding.sharingRcvPostList

        sharingVM = ViewModelProvider(this)[SharingViewModel::class.java]

        adapter = SharingPostListAdapter(sharingVM.posts)

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

        _binding.sharingFabFilter.setOnClickListener {
            showFilterDialog()
        }

        // Inflate the layout for this fragment
        return root
    }

    override fun onStart() {
        super.onStart()
        sharingVM.loadPostList(notifyInsert, toastCallback)
    }
}