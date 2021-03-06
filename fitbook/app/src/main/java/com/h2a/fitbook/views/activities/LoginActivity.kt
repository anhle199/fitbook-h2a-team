package com.h2a.fitbook.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityLoginBinding
import com.h2a.fitbook.utils.UtilFunctions
import com.h2a.fitbook.viewmodels.LoginViewModel

enum class AuthTextInputLayoutState {
    VALID, INVALID, MISSING_VALUE
}

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    private val signInWithGoogleActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        Log.i("SignInWithGoogle", result.toString())
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // Start loading animation
            setLoadingState(true)

            val intent = result.data!!
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.i("SignInWithGoogle", "signInWithGoogleActivityResult - ${account.id}")

                viewModel.signInWithGoogle(this, account.idToken!!) { success ->
                    if (success) {
                        // Navigate to Main Feature Activity (home)
                        val homeIntent = Intent(this, MainFeatureActivity::class.java)

                        // End loading animation
                        setLoadingState(false)

                        startActivity(homeIntent)
                    } else {
                        // End loading animation
                        setLoadingState(false)
                        showShortToast(R.string.login_failed_to_login_message)
                    }
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.i("SignInWithGoogle", "Google sign in failed", e)
            }

            // End loading animation
            setLoadingState(false)
        } else {
            showShortToast(R.string.login_failed_to_login_message)
        }
    }

    override fun onStart() {
        super.onStart()

        binding.loginEtUsername.setText("")
        binding.loginEtPassword.setText("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize view model
        viewModel = LoginViewModel()

        supportActionBar?.let {
            // Show action bar
            it.show()
            // Set title for action bar
            it.setTitle(R.string.login_action_bar_title)
            // Display back button
            it.setDisplayHomeAsUpEnabled(true)
        }

        // Make underline for forgot password text
        UtilFunctions.makeUnderlineForTextView(
            binding.loginTvForgotPassword,
            resources.getString(R.string.login_forgot_password_text)
        )

        // Make underline for sign up text
        UtilFunctions.makeUnderlineForTextView(
            binding.loginTvSignUp,
            resources.getString(R.string.login_sign_up_text)
        )

        addOnTextChangedForAuthFields()
        addActionForButtons()
    }

    private fun setLoadingState(state: Boolean) {
        binding.loginClLoadingBackground.isVisible = state
    }

    private fun addActionForButtons() {
        // Navigate to sign up activity
        binding.loginTvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Add action for Login Button
        binding.loginBtnLogin.setOnClickListener {
            // Start loading animation
            setLoadingState(true)

            viewModel.username = binding.loginEtUsername.text.toString()
            viewModel.password = binding.loginEtPassword.text.toString()

            if (viewModel.validateAllAuthFields(binding, this::updateAuthFieldByState)) {
                viewModel.signInWithUsernameAndPassword(this) { success ->
                    // End loading animation
                    setLoadingState(false)

                    if (success) {
                        // Navigate to Main Feature Activity (home)
                        val intent = Intent(this, MainFeatureActivity::class.java)
                        startActivity(intent)
                    } else {
                        showShortToast(R.string.login_username_or_password_field_are_incorrect)
                    }
                }
            } else {
                // End loading animation
                setLoadingState(false)
            }
        }

        // Add action for Sign In With Google Button
        binding.loginLlLoginWithGoogle.setOnClickListener {
            val signInIntent = viewModel.googleSignInClient.signInIntent
            signInWithGoogleActivityResult.launch(signInIntent)
        }
    }

    private fun addOnTextChangedForAuthFields() {
        binding.loginEtUsername.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.loginTilUsername,
                null,
                AuthTextInputLayoutState.VALID
            )
        }

        binding.loginEtPassword.doOnTextChanged { _, _, _, _ ->
            updateAuthFieldByState(
                binding.loginTilPassword,
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
