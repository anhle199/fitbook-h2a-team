package com.h2a.fitbook.views.activities.addfood

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityManualAddFoodBinding
import com.h2a.fitbook.models.Food
import com.h2a.fitbook.viewmodels.healthmanagement.AddFoodViewModel
import java.lang.NumberFormatException

class ManualAddFoodActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityManualAddFoodBinding
    private var toast: Toast? = null
    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityManualAddFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.manual_add_food_activity_title)

        val targetDate = intent.getStringExtra("Date").toString()

        _binding.manualAddFoodBtnSave.setOnClickListener {
            val amountInput = _binding.manualAddFoodEtFoodAmount
            val nameInput = _binding.manualAddFoodEtFoodName
            val caloriesInput = _binding.manualAddFoodEtFoodCalories
            val addFoodViewModel = ViewModelProvider(this)[AddFoodViewModel::class.java]

            val name = nameInput.text.toString().trim()
            if (name == "") {
                _binding.manualAddFoodTilFoodName.error = "Vui lòng không để trống tên!"
                return@setOnClickListener
            } else {
                _binding.manualAddFoodTilFoodName.error = null
            }
            val calories: Float
            try {
                calories = caloriesInput.text.toString().toFloat()
                _binding.manualAddFoodTilFoodCalories.error = null
            } catch (e: NumberFormatException) {
                _binding.manualAddFoodTilFoodCalories.error = "Vui lòng kiểm tra lại số calo!"
                return@setOnClickListener
            }
            val amount: Int
            try {
                amount = amountInput.text.toString().toInt()
                _binding.manualAddFoodTilFoodAmount.error = null
            } catch (e: NumberFormatException) {
                _binding.manualAddFoodTilFoodAmount.error = "Vui lòng kiểm tra lại lượng thực phẩm!"
                return@setOnClickListener
            }

            val item = Food(name, calories / amount, "", "gram")
            addFoodViewModel.saveFood(this, targetDate, item, amount, toastCallback)
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
