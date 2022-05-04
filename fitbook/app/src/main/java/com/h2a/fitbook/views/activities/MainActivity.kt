package com.h2a.fitbook.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityMainBinding
import com.h2a.fitbook.utils.AuthenticationManager
import com.h2a.fitbook.utils.Constants
import com.h2a.fitbook.utils.UtilFunctions


// Extension of Activity
fun Activity.showShortToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()

        if (AuthenticationManager.instance.showAuthButtons) {
            setVisibleForAuthButtons(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Start loading animation
        setLoadingState(true)

        // Configure GoogleSignInClient and load sign in method
        AuthenticationManager.instance.configureSignInWithGoogle(this)
        AuthenticationManager.instance.loadSignInMethod(this)

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

        checkLastSignInAndAutoSignInIfNeeded()
        AuthenticationManager.instance.showAuthButtons = true
    }

    private fun setLoadingState(state: Boolean) {
        binding.mainPbLoading.isVisible = state
    }

    private fun setVisibleForAuthButtons(isVisible: Boolean) {
        // Ensure that loading state is always off
        setLoadingState(false)

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

    private fun checkLastSignInAndAutoSignInIfNeeded() {
        AuthenticationManager.instance.isSignedIn = FirebaseAuth.getInstance().currentUser != null

        // Stop loading animation
        setLoadingState(false)

        if (AuthenticationManager.instance.isSignedIn) {
            // Navigate to Main Feature Activity (home)
            val intent = Intent(this, MainFeatureActivity::class.java)
            startActivity(intent)
        } else {
            // Remove method saved in the shared preferences
            if (AuthenticationManager.instance.signInMethod != null) {
                val sharedPreferences = getSharedPreferences(
                    Constants.SIGN_IN_INFO_SHARE_PREFERENCES_NAME,
                    Activity.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.remove(Constants.SIGN_IN_METHOD_KEY)
                editor.apply()
            }

            AuthenticationManager.instance.showAuthButtons = true
            setVisibleForAuthButtons(true)
        }
    }

}
