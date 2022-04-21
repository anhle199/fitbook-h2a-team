package com.h2a.fitbook.views.activities.addfood

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.healthmanagement.AddFoodListAdapter
import com.h2a.fitbook.databinding.ActivityAddFoodBinding
import com.h2a.fitbook.models.Food
import com.h2a.fitbook.viewmodels.healthmanagement.AddFoodViewModel
import java.lang.NumberFormatException

class AddFoodActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityAddFoodBinding
    private lateinit var adapter: AddFoodListAdapter
    private lateinit var addFoodViewModel: AddFoodViewModel
    private lateinit var targetDate: String
    private var toast: Toast? = null
    private val onItemClick = { item: Food ->
        showDialog(item)
    }

    private fun showDialog(item: Food) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_input_food_amount, null)
        dialogView.findViewById<TextView>(R.id.dialog_input_amount_tv_food_name).text = item._name
        val context = this
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Nhập lượng thực phẩm")
            .setView(dialogView)
            .setPositiveButton("Xác nhận", null)
            .setNegativeButton("Hủy") { _,_ ->
                Toast.makeText(applicationContext, "Đã hủy", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val amountInput = dialogView.findViewById<TextInputEditText>(R.id.dialog_input_amount_et)
            try {
                val amount = amountInput!!.text.toString().toInt()
                addFoodViewModel.saveFood(context, targetDate, item, amount, toastCallback)
                dialogView.findViewById<TextInputLayout>(R.id.dialog_input_amount_til).error = null
                dialog.dismiss()
            } catch (e: NumberFormatException) {
                dialogView.findViewById<TextInputLayout>(R.id.dialog_input_amount_til).error = "Vui lòng nhập số lượng!"
            }
        }
    }

    private val notifyInsert = { position: Int ->
        if (position == -1)
            adapter.notifyChangeAll()
        else
            adapter.notifyChangeAt(position)
    }

    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityAddFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        targetDate = intent.getStringExtra("Date").toString()

        val actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.add_food_activity_title)

        val rcvList = _binding.addFoodRcvFoodList

        addFoodViewModel = ViewModelProvider(this)[AddFoodViewModel::class.java]

        adapter = AddFoodListAdapter(addFoodViewModel.getFoodList(), onItemClick)
        addFoodViewModel.loadFoodList(notifyInsert, toastCallback)

        rcvList.adapter = adapter

        rcvList.layoutManager = GridLayoutManager(this, 2)

        val search = _binding.addFoodEtSearch

        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(search.text)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        _binding.addFoodBtnOther.setOnClickListener {
            val intent = Intent(this, ManualAddFoodActivity::class.java)
            intent.putExtra("Date", targetDate)

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
