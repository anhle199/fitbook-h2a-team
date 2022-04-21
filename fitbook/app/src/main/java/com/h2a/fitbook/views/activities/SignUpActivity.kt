package com.h2a.fitbook.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivitySignUpBinding
import com.h2a.fitbook.utils.UtilFunctions
import com.h2a.fitbook.viewmodels.SignUpViewModel
import java.text.SimpleDateFormat

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onStart() {
        super.onStart()

        binding.signUpEtFullName.setText("")
        binding.signUpEtBirthday.setText("")
        binding.signUpActvGender.setText(resources.getString(R.string.gender_male), false)
        binding.signUpEtUsername.setText("")
        binding.signUpEtPassword.setText("")
        binding.signUpEtConfirmPassword.setText("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize view model
        viewModel = SignUpViewModel()

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

        addOnTextChangedForAuthFields()
        addActionForButtons()
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

        // Set default option for this
        val defaultOption = if (options.isNotEmpty()) options[0] else resources.getString(R.string.gender_male)
        binding.signUpActvGender.setText(defaultOption, false)
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

    private fun addActionForButtons() {
        // Navigate to login activity
        binding.signUpTvLogin.setOnClickListener {
            finish()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Add action for Sign Up Button
        binding.signUpBtnSignUp.setOnClickListener {
            viewModel.fullName = binding.signUpEtFullName.text.toString()
            viewModel.dateOfBirth = binding.signUpEtBirthday.text.toString()
            viewModel.gender = binding.signUpActvGender.text.toString()
            viewModel.username = binding.signUpEtUsername.text.toString()
            viewModel.password = binding.signUpEtPassword.text.toString()
            viewModel.confirmPassword = binding.signUpEtConfirmPassword.text.toString()

            val datePattern = resources.getString(R.string.date_format)
            if (viewModel.validateAllAuthFields(datePattern, binding, this::updateAuthFieldByState)) {
                viewModel.signUp(this) { success ->
                    if (success) {
                        finish()

                        // Navigate to Login Activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        showShortToast(R.string.sign_up_failed_to_sign_up)
                    }
                }
            }
        }

        // Show date picker
        binding.signUpTilBirthday.setEndIconOnClickListener {
            setupAndShowDatePicker()
        }
    }

    private fun addOnTextChangedForAuthFields() {
        binding.signUpEtFullName.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.signUpTilFullName,
                null,
                AuthTextInputLayoutState.VALID
            )
        }

        binding.signUpEtBirthday.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.signUpTilBirthday,
                null,
                AuthTextInputLayoutState.VALID
            )
        }

        binding.signUpEtUsername.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.signUpTilUsername,
                null,
                AuthTextInputLayoutState.VALID
            )
        }

        binding.signUpEtPassword.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.signUpTilPassword,
                null,
                AuthTextInputLayoutState.VALID
            )
        }

        binding.signUpEtConfirmPassword.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.signUpTilConfirmPassword,
                null,
                AuthTextInputLayoutState.VALID
            )
        }
    }

    private fun updateAuthFieldByState(
        textInputLayout: TextInputLayout,
        helperTextResId: Int?, state: AuthTextInputLayoutState
    ) {
        textInputLayout.boxStrokeColor = viewModel.getBoxStrokeColorOfAuthFieldByState(this, state)
        textInputLayout.hintTextColor = viewModel.getHintTextColorOfAuthFieldByState(this, state)
        textInputLayout.helperText = if (helperTextResId != null) {
            resources.getString(helperTextResId)
        } else {
            null
        }
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
