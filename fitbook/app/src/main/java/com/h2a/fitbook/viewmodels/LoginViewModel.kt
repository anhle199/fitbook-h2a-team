package com.h2a.fitbook.viewmodels

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityLoginBinding
import com.h2a.fitbook.models.UserModel
import com.h2a.fitbook.utils.AuthenticationManager
import com.h2a.fitbook.utils.Constants
import com.h2a.fitbook.utils.SignInMethod
import com.h2a.fitbook.utils.ValidationHandler
import com.h2a.fitbook.views.activities.AuthTextInputLayoutState

class LoginViewModel: ViewModel() {
    var username: String = ""
    var password: String = ""

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var _googleSignInClient: GoogleSignInClient
    val googleSignInClient get() = _googleSignInClient

    fun configureSignInWithGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.firebase_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        _googleSignInClient = GoogleSignIn.getClient(context, gso)
        AuthenticationManager.googleSignInClient = _googleSignInClient
    }

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
    fun signInWithUsernameAndPassword(completion: (Boolean) -> Unit) {
        val email = username + Constants.SUFFIX_EMAIL
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    AuthenticationManager.signInMethod = SignInMethod.EMAIL_PASSWORD
                    AuthenticationManager.isSignedIn = true
                    Log.i("SignInWithEmailAndPassword", "signInWithUsernameAndPassword:success - ${auth.currentUser?.uid}")
                } else {
                    Log.i("SignInWithEmailAndPassword", "signInWithUsernameAndPassword:failed")
                }

                completion(it.isSuccessful)
            }
    }

    fun signInWithGoogle(idToken: String, completion: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    AuthenticationManager.signInMethod = SignInMethod.GOOGLE
                    AuthenticationManager.isSignedIn = true
                    Log.i("SignInWithGoogle", "signInWithGoogle:success - ${auth.currentUser?.uid}")

                    if (it.result.additionalUserInfo?.isNewUser == true && auth.currentUser != null) {
                        saveUserInfoSignInWithGoogle(it.result.additionalUserInfo!!, auth.currentUser!!)
                    }
                } else {
                    Log.i("SignInWithGoogle", "signInWithGoogle:failed")
                }

                completion(it.isSuccessful)
            }
    }

    private fun saveUserInfoSignInWithGoogle(
        additionalUserInfo: AdditionalUserInfo,
        firebaseUser: FirebaseUser
    ) {
        val photoUrlString = if (firebaseUser.photoUrl != null) {
            firebaseUser.photoUrl.toString()
        } else { "" }

        val user = UserModel(
            firebaseUser.uid,
            additionalUserInfo.providerId!!,
            firebaseUser.displayName ?: "",
            null,
            null,
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
