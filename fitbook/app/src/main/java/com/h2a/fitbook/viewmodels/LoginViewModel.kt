package com.h2a.fitbook.viewmodels

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityLoginBinding
import com.h2a.fitbook.models.UserModel
import com.h2a.fitbook.utils.*
import com.h2a.fitbook.views.activities.AuthTextInputLayoutState

class LoginViewModel: ViewModel() {
    var username: String = ""
    var password: String = ""

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val googleSignInClient = AuthenticationManager.instance.googleSignInClient

    fun validateAllAuthFields(
        binding: ActivityLoginBinding,
        onUpdateField: (TextInputLayout, Int?, AuthTextInputLayoutState) -> Unit
    ): Boolean {
        // Username
        val usernameFieldState: AuthTextInputLayoutState
        val usernameHelperTextResId: Int?

        if (username.isEmpty()) {
            usernameFieldState = AuthTextInputLayoutState.MISSING_VALUE
            usernameHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validateUsername(username)) {
            usernameFieldState = AuthTextInputLayoutState.INVALID
            usernameHelperTextResId = R.string.auth_username_field_is_invalid
        } else {
            usernameFieldState = AuthTextInputLayoutState.VALID
            usernameHelperTextResId = null
        }

        // Password
        val passwordFieldState: AuthTextInputLayoutState
        val passwordHelperTextResId: Int?

        if (password.isEmpty()) {
            passwordFieldState = AuthTextInputLayoutState.MISSING_VALUE
            passwordHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validatePassword(password)) {
            passwordFieldState = AuthTextInputLayoutState.INVALID
            passwordHelperTextResId = R.string.auth_password_field_is_invalid
        } else {
            passwordFieldState = AuthTextInputLayoutState.VALID
            passwordHelperTextResId = null
        }

        onUpdateField(binding.loginTilUsername, usernameHelperTextResId, usernameFieldState)
        onUpdateField(binding.loginTilPassword, passwordHelperTextResId, passwordFieldState)

        return usernameFieldState == AuthTextInputLayoutState.VALID && passwordFieldState == AuthTextInputLayoutState.VALID
    }

    // Both username and password are valid.
    // `completion` parameter is a callback function to perform an action after login process is completed.
    fun signInWithUsernameAndPassword(context: Context, completion: (Boolean) -> Unit) {
        val email = username + Constants.SUFFIX_EMAIL
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    AuthenticationManager.instance.signInMethod = SignInMethod.EMAIL_PASSWORD
                    AuthenticationManager.instance.isSignedIn = true
                    Log.i("SignInWithEmailAndPassword", "signInWithUsernameAndPassword:success - ${auth.currentUser?.uid}")

                    // Save sign in method to share preferences
                    saveSignInMethodToSharedPreferences(context, SignInMethod.EMAIL_PASSWORD)
                } else {
                    Log.i("SignInWithEmailAndPassword", "signInWithUsernameAndPassword:failed")
                }

                completion(it.isSuccessful)
            }
    }

    fun signInWithGoogle(context: Context, idToken: String, completion: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    AuthenticationManager.instance.signInMethod = SignInMethod.GOOGLE
                    AuthenticationManager.instance.isSignedIn = true
                    Log.i("SignInWithGoogle", "signInWithGoogle:success - ${auth.currentUser?.uid}")

                    // Save sign in method to share preferences
                    saveSignInMethodToSharedPreferences(context, SignInMethod.GOOGLE)

                    // Save user info to firebase if needed
                    if (it.result.additionalUserInfo?.isNewUser == true && auth.currentUser != null) {
                        saveUserInfoSignInWithGoogle(auth.currentUser!!)
                    }
                } else {
                    Log.i("SignInWithGoogle", "signInWithGoogle:failed")
                }

                completion(it.isSuccessful)
            }
    }

    // Save sign in method to share preferences
    private fun saveSignInMethodToSharedPreferences(context: Context, signInMethod: SignInMethod) {
        val sharedPreferences = context.getSharedPreferences(
            Constants.SIGN_IN_INFO_SHARE_PREFERENCES_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SIGN_IN_METHOD_KEY, signInMethod.name)
        editor.apply()
    }

    private fun saveUserInfoSignInWithGoogle(firebaseUser: FirebaseUser) {
        val photoUrlString = if (firebaseUser.photoUrl != null) {
            firebaseUser.photoUrl.toString()
        } else { "" }

        val user = UserModel(
            firebaseUser.uid,
            Constants.SIGN_IN_WITH_GOOGLE_PROVIDER_ID,
            firebaseUser.displayName ?: "",
            Timestamp.now(),
            "male",
            photoUrlString,
        )

        firestore.collection("users")
            .document(user.userId)
            .set(user)
            .addOnCompleteListener { savedUserInfoTask ->
                if (savedUserInfoTask.isSuccessful) {
                    Log.i("SignInWithGoogle", "saveUserInfoSignInWithGoogle::success")
                } else {
                    Log.i("SignInWithGoogle", "saveUserInfoSignInWithGoogle::failed")
                }
            }
    }

    fun getBoxStrokeColorOfAuthFieldByState(
        @NonNull context: Context,
        state: AuthTextInputLayoutState
    ): Int {
        return when (state) {
            AuthTextInputLayoutState.VALID -> {
                ContextCompat.getColor(context, R.color.text_input_layout_stroke)
            }
            AuthTextInputLayoutState.INVALID, AuthTextInputLayoutState.MISSING_VALUE -> {
                ContextCompat.getColor(context, R.color.text_input_layout_stroke_error)
            }
        }
    }

    fun getHintTextColorOfAuthFieldByState(
        @NonNull context: Context,
        state: AuthTextInputLayoutState
    ): ColorStateList {
        return when (state) {
            AuthTextInputLayoutState.VALID -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            }
            AuthTextInputLayoutState.INVALID, AuthTextInputLayoutState.MISSING_VALUE -> {
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.text_input_layout_stroke_error)
                )
            }
        }
    }

}
