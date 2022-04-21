package com.h2a.fitbook.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityMainBinding
import com.h2a.fitbook.utils.UtilFunctions


// Extension of Activity
fun Activity.showShortToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setVisibleForAuthButtons(true)

        UtilFunctions.makeUnderlineForTextView(
            binding.mainTvContinueAsGuest,
            resources.getString(R.string.main_continue_as_guest_text)
        )

        // Navigate to login activity
        binding.mainBtnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Navigate to sign up activity
        binding.mainBtnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Navigate to home activity as guest
        binding.mainTvContinueAsGuest.setOnClickListener {
            val intent = Intent(this, MainFeatureActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setVisibleForAuthButtons(isVisible: Boolean) {
        if (isVisible) {
            binding.mainLlAppTitle.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0,
                1f
            )
        } else {
            binding.mainLlAppTitle.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        binding.mainLlAuthButtons.isVisible = isVisible
    }
}
