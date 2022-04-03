package com.h2a.fitbook.views.activities.addfood

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.healthmanagement.AddFoodListAdapter
import com.h2a.fitbook.databinding.ActivityAddFoodBinding
import com.h2a.fitbook.models.HealthManagementFood

class AddFoodActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityAddFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityAddFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "Thêm thực phẩm"

        val rcvList = _binding.addFoodRcvFoodList

        val testData: MutableList<HealthManagementFood> = arrayListOf()

        for (i in 0..10) {
            testData.add(HealthManagementFood("Thực phẩm", 100, "https://yt3.ggpht.com/ytc/AKedOLRnTRRXf6mT_gGQUTaCCZeLLc2FUB8lJOFq7CElow=s900-c-k-c0x00ffffff-no-rj"))
        }

        val adapter = AddFoodListAdapter(testData)

        rcvList.adapter = adapter

        rcvList.layoutManager = GridLayoutManager(this, 2)

        _binding.addFoodBtnOther.setOnClickListener {
            val intent = Intent(this, ManualAddFoodActivity::class.java)

            startActivity(intent)
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
