package com.h2a.fitbook.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivitySignUpBinding
import com.h2a.fitbook.utils.UtilFunctions
import java.text.SimpleDateFormat

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            // Show action bar
            it.show()
            // Set title for action bar
            it.setTitle(R.string.sign_up_action_bar_title)
            // Display back button
            it.setDisplayHomeAsUpEnabled(true)
        }

        setupGenderAdapter()

        // Make underline for login text
        UtilFunctions.makeUnderlineForTextView(
            binding.signUpTvLogin,
            resources.getString(R.string.sign_up_login_text)
        )

        // Navigate to login activity
        binding.signUpTvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Navigate to home activity if login successfully
        binding.signUpBtnSignUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Show date picker
        binding.signUpTilBirthday.setEndIconOnClickListener {
            setupAndShowDatePicker()
        }
    }

    private fun setupGenderAdapter() {
        // Set adapter for Gender AutoCompleteTextView
        val options = resources.getStringArray(R.array.genders)
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        ).also {
            binding.signUpActvGender.setAdapter(it)
        }

//        val defaultOption = if (options.isNotEmpty())
//            options[0]
//        else
//            resources.getString(R.string.gender_male)

        // Set default option for this
//        binding.signUpActvGender.setText(defaultOption, false)
    }

    private fun setupAndShowDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.sign_up_text_field_birthday_placeholder))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat(resources.getString(R.string.date_format))
            binding.signUpEtBirthday.setText(dateFormatter.format(it))
        }

        datePicker.show(supportFragmentManager, "birthdaySelection")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  // Back button action
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
