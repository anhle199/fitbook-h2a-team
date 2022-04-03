package com.h2a.fitbook.views.fragments.healthmanagement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.homemanagement.HealthManagementFoodListAdapter
import com.h2a.fitbook.databinding.FragmentHealthManagementBinding
import com.h2a.fitbook.models.HealthManagementFood
import com.h2a.fitbook.views.activities.addfood.AddFoodActivity

class HealthManagementFragment : Fragment() {
    private var _binding: FragmentHealthManagementBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthManagementBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        val rcvList = _binding!!.healthManagementRcvFoodList

        val testData: MutableList<HealthManagementFood> = arrayListOf()

        for (i in 0..10) {
            testData.add(HealthManagementFood("Thực phẩm", 100, "https://yt3.ggpht.com/ytc/AKedOLRnTRRXf6mT_gGQUTaCCZeLLc2FUB8lJOFq7CElow=s900-c-k-c0x00ffffff-no-rj"))
        }

        val adapter = HealthManagementFoodListAdapter(testData)

        rcvList.adapter = adapter

        rcvList.layoutManager = LinearLayoutManager(activity)

        val menuBtn = _binding!!.healthManagementFabMenu

        menuBtn.setOnClickListener {
            val curState = _binding!!.healthManagementFabAdd.visibility == View.INVISIBLE
            _binding!!.healthManagementFabAdd.visibility = if (curState) View.VISIBLE else View.INVISIBLE
            _binding!!.healthManagementFabEdit.visibility = if (curState) View.VISIBLE else View.INVISIBLE
            if (curState) {
                menuBtn.backgroundTintList = ContextCompat.getColorStateList(this.requireContext(), R.color.fab_close_color)
                menuBtn.setImageResource(R.drawable.ic_round_close_24)
            } else {
                menuBtn.backgroundTintList = ContextCompat.getColorStateList(this.requireContext(), R.color.fab_main_color)
                menuBtn.setImageResource(R.drawable.ic_round_menu_24)
            }
        }

        _binding!!.healthManagementFabAdd.setOnClickListener {
            val intent = Intent(this.requireContext(), AddFoodActivity::class.java)

            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}