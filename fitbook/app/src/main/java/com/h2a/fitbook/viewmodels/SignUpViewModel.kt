package com.h2a.fitbook.viewmodels

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivitySignUpBinding
import com.h2a.fitbook.models.UserModel
import com.h2a.fitbook.utils.*
import com.h2a.fitbook.views.activities.AuthTextInputLayoutState

class SignUpViewModel: ViewModel() {

    var fullName: String = ""
    var dateOfBirth: String = ""
    var gender: String = ""
    var username: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun validateAllAuthFields(
        dateFormatPattern: String,
        binding: ActivitySignUpBinding,
        onUpdateField: (TextInputLayout, Int?, AuthTextInputLayoutState) -> Unit
    ): Boolean {
        // Full name
        val fullNameFieldState: AuthTextInputLayoutState
        val fullNameHelperTextResId: Int?

        if (fullName.isEmpty()) {
            fullNameFieldState = AuthTextInputLayoutState.MISSING_VALUE
            fullNameHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validateFullName(fullName)) {
            fullNameFieldState = AuthTextInputLayoutState.INVALID
            fullNameHelperTextResId = R.string.sign_up_full_name_field_is_invalid
        } else {
            fullNameFieldState = AuthTextInputLayoutState.VALID
            fullNameHelperTextResId = null
        }

        // Birthday
        val birthdayFieldState: AuthTextInputLayoutState
        val birthdayHelperTextResId: Int?

        if (dateOfBirth.isEmpty()) {
            birthdayFieldState = AuthTextInputLayoutState.MISSING_VALUE
            birthdayHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validateDateOfBirth(dateOfBirth, dateFormatPattern)) {
            birthdayFieldState = AuthTextInputLayoutState.INVALID
            birthdayHelperTextResId = R.string.sign_up_birthday_field_is_invalid
        } else {
            birthdayFieldState = AuthTextInputLayoutState.VALID
            birthdayHelperTextResId = null
        }

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

        // Confirm Password
        val confirmPasswordFieldState: AuthTextInputLayoutState
        val confirmPasswordHelperTextResId: Int?

        if (confirmPassword.isEmpty()) {
            confirmPasswordFieldState = AuthTextInputLayoutState.MISSING_VALUE
            confirmPasswordHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validatePassword(confirmPassword)) {
            confirmPasswordFieldState = AuthTextInputLayoutState.INVALID
            confirmPasswordHelperTextResId = R.string.sign_up_confirm_password_dont_match_password
        } else {
            confirmPasswordFieldState = AuthTextInputLayoutState.VALID
            confirmPasswordHelperTextResId = null
        }

        onUpdateField(binding.signUpTilFullName, fullNameHelperTextResId, fullNameFieldState)
        onUpdateField(binding.signUpTilBirthday, birthdayHelperTextResId, birthdayFieldState)
        onUpdateField(binding.signUpTilUsername, usernameHelperTextResId, usernameFieldState)
        onUpdateField(binding.signUpTilPassword, passwordHelperTextResId, passwordFieldState)
        onUpdateField(binding.signUpTilConfirmPassword, confirmPasswordHelperTextResId, confirmPasswordFieldState)

        return fullNameFieldState == AuthTextInputLayoutState.VALID
                && birthdayFieldState == AuthTextInputLayoutState.VALID
                && usernameFieldState == AuthTextInputLayoutState.VALID
                && passwordFieldState == AuthTextInputLayoutState.VALID
                && confirmPasswordFieldState == AuthTextInputLayoutState.VALID
    }

    // `completion` parameter is a callback function to perform an action after login process is completed.
    fun signUp(context: Context, completion: (Boolean) -> Unit) {
        val email = username + Constants.SUFFIX_EMAIL
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.additionalUserInfo != null && it.result.user != null) {
                        saveUserInfo(context, it.result.user!!)
                    }

                    Log.i("SignUp", "signUp:success")
                } else {
                    Log.i("SignUp", "signUp:failed")
                }

                completion(it.isSuccessful)
            }
    }

    private fun saveUserInfo(context: Context, firebaseUser: FirebaseUser) {
        // Parse gender
        val male = context.resources.getString(R.string.gender_male)
        val savedGender = if (gender == male) "male" else "female"

        // Parse date of birth
        val dobInTimestamp = UtilFunctions.convertDateStringToTimestamp(
            dateOfBirth,
            context.resources.getString(R.string.date_format)
        )

        // Create user instance
        val user = UserModel(
            firebaseUser.uid,
            Constants.SIGN_IN_WITH_EMAIL_PASSWORD_PROVIDER_ID,
            fullName,
            dobInTimestamp,
            savedGender,
            "",
        )

        firestore.collection("users")
            .document(user.userId)
            .set(user)
            .addOnCompleteListener { savedUserInfoTask ->
                if (savedUserInfoTask.isSuccessful) {
                    Log.i("SignUp", "saveUserInfo::success")
                } else {
                    Log.i("SignUp", "saveUserInfo::failed")
                }
            }
    }

    /*
    Username:
        - Length of 6 to 20 characters.
        - Must contain at least one letter.
        - Optionally contain digits.
    */
    fun validateUsername(username: String): Boolean {
        return username.matches(Regex("[a-zA-Z\\d]{6,20}"))
    }

    /*
    Password:
       - Length of 6 to 20 characters.
       - Must contain both letters and digits.
    */
    fun validatePassword(password: String): Boolean {
        val containEntireLettersOrDigits = password.matches(Regex("([a-zA-Z]{6,20})|(\\d{6,20})"))
        val allCases = password.matches(Regex("[a-zA-Z\\d]{6,20}"))

        return allCases && !containEntireLettersOrDigits
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
