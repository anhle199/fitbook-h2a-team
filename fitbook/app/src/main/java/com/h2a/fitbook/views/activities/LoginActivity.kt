package com.h2a.fitbook.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityLoginBinding
import com.h2a.fitbook.utils.UtilFunctions

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Navigate to sign up activity
        binding.loginTvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Navigate to home activity if login successfully
        binding.loginBtnLogin.setOnClickListener {
            val intent = Intent(this, MainFeatureActivity::class.java)
            startActivity(intent)
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
