package com.h2a.fitbook.views.activities.addfood

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityManualAddFoodBinding

class ManualAddFoodActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityManualAddFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityManualAddFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.manual_add_food_activity_title)

        _binding.manualAddFoodBtnSave.setOnClickListener {
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
