package com.h2a.fitbook.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.favre.lib.crypto.bcrypt.BCrypt
import coil.load
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.FragmentProfileBinding
import com.h2a.fitbook.models.UserModel
import com.h2a.fitbook.utils.Constants
import com.h2a.fitbook.utils.UtilFunctions
import com.h2a.fitbook.utils.ValidationHandler
import com.h2a.fitbook.views.activities.AuthTextInputLayoutState
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel: ViewModel() {

    // Editing state observable variable
    private val _isEditing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    private val _isSelectingAvatar: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSelectingAvatar: LiveData<Boolean> = _isSelectingAvatar

    // Loading state observable variable
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var decodedNewPassword: String? = null
    private var originalUserModel: UserModel? = null
    val fullName get() = originalUserModel?.fullName ?: ""
    val birthday get() = convertBirthdayTimestampToString()
    val gender get() = originalUserModel?.gender ?: "male"
    val profileImageLink get() = originalUserModel?.profileImage ?: ""
    var profileUriFromGallery: Uri? = null

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    fun fetchUserData(completion: (Boolean) -> Unit) {
        originalUserModel = null

        UtilFunctions.fetchUserData(userId) { isSuccess, userData ->
            originalUserModel = userData
            completion(isSuccess)
        }
    }

    fun toggleEditingState() {
        _isEditing.value = !(_isEditing.value!!)
    }

    fun setSelectingAvatarState(state: Boolean) {
        _isSelectingAvatar.value = state
    }

    fun setLoadingState(state: Boolean) {
        _isLoading.value = state
    }

    fun validateBasicInfoFields(
        fullName: String,
        dateOfBirth: String,
        binding: FragmentProfileBinding,
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
        } else if (!ValidationHandler.validateDateOfBirth(dateOfBirth, Constants.BIRTHDAY_FORMAT)) {
            birthdayFieldState = AuthTextInputLayoutState.INVALID
            birthdayHelperTextResId = R.string.sign_up_birthday_field_is_invalid
        } else {
            birthdayFieldState = AuthTextInputLayoutState.VALID
            birthdayHelperTextResId = null
        }

        onUpdateField(binding.profileTilFullName, fullNameHelperTextResId, fullNameFieldState)
        onUpdateField(binding.profileTilBirthday, birthdayHelperTextResId, birthdayFieldState)

        return fullNameFieldState == AuthTextInputLayoutState.VALID && birthdayFieldState == AuthTextInputLayoutState.VALID
    }

    fun validateChangePasswordSection(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        binding: FragmentProfileBinding,
        onUpdateField: (TextInputLayout, Int?, AuthTextInputLayoutState) -> Unit
    ): Boolean {
        // Password
        val currentPasswordFieldState: AuthTextInputLayoutState
        val currentPasswordHelperTextResId: Int?

        if (currentPassword.isEmpty()) {
            currentPasswordFieldState = AuthTextInputLayoutState.MISSING_VALUE
            currentPasswordHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validatePassword(currentPassword)) {
            currentPasswordFieldState = AuthTextInputLayoutState.INVALID
            currentPasswordHelperTextResId = R.string.auth_password_field_is_invalid
        } else {
            val result = BCrypt.verifyer().verify(currentPassword.toCharArray(), originalUserModel?.hashedPassword!!.toCharArray())

            if (result.verified) {
                currentPasswordFieldState = AuthTextInputLayoutState.VALID
                currentPasswordHelperTextResId = null
            } else {
                currentPasswordFieldState = AuthTextInputLayoutState.INVALID
                currentPasswordHelperTextResId = R.string.profile_current_password_dont_match
            }
        }

        // New Password
        val newPasswordFieldState: AuthTextInputLayoutState
        val newPasswordHelperTextResId: Int?

        if (newPassword.isEmpty()) {
            newPasswordFieldState = AuthTextInputLayoutState.MISSING_VALUE
            newPasswordHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validatePassword(newPassword)) {
            newPasswordFieldState = AuthTextInputLayoutState.INVALID
            newPasswordHelperTextResId = R.string.auth_password_field_is_invalid
        } else if (newPassword == currentPassword) {
            newPasswordFieldState = AuthTextInputLayoutState.INVALID
            newPasswordHelperTextResId = R.string.profile_new_password_match_current_password
        } else {
            newPasswordFieldState = AuthTextInputLayoutState.VALID
            newPasswordHelperTextResId = null
        }

        // Confirm New Password
        val confirmNewPasswordFieldState: AuthTextInputLayoutState
        val confirmNewPasswordHelperTextResId: Int?

        if (confirmNewPassword.isEmpty()) {
            confirmNewPasswordFieldState = AuthTextInputLayoutState.MISSING_VALUE
            confirmNewPasswordHelperTextResId = R.string.auth_required_field_is_missing_value
        } else if (!ValidationHandler.validatePassword(confirmNewPassword)) {
            confirmNewPasswordFieldState = AuthTextInputLayoutState.INVALID
            confirmNewPasswordHelperTextResId = R.string.auth_password_field_is_invalid
        }  else if (confirmNewPassword != newPassword) {
            confirmNewPasswordFieldState = AuthTextInputLayoutState.INVALID
            confirmNewPasswordHelperTextResId = R.string.sign_up_confirm_password_dont_match_password
        } else {
            confirmNewPasswordFieldState = AuthTextInputLayoutState.VALID
            confirmNewPasswordHelperTextResId = null
        }

        onUpdateField(binding.profileTilCurrentPassword, currentPasswordHelperTextResId, currentPasswordFieldState)
        onUpdateField(binding.profileTilNewPassword, newPasswordHelperTextResId, newPasswordFieldState)
        onUpdateField(binding.profileTilConfirmNewPassword, confirmNewPasswordHelperTextResId, confirmNewPasswordFieldState)

        return currentPasswordFieldState == AuthTextInputLayoutState.VALID
                && newPasswordFieldState == AuthTextInputLayoutState.VALID
                && confirmNewPasswordFieldState == AuthTextInputLayoutState.VALID
    }

    fun rollbackAllChanges(context: Context, viewBinding: FragmentProfileBinding) {
        profileUriFromGallery = null

        if (profileImageLink.isEmpty()) {
            viewBinding.profileIvAvatar.setImageResource(R.drawable.default_avatar)
        } else {
            viewBinding.profileIvAvatar.load(profileImageLink)
        }

        viewBinding.profileEtFullName.setText(fullName)
        viewBinding.profileEtBirthday.setText(birthday)

        val gender = if (gender == "male") {
            context.resources.getString(R.string.gender_male)
        } else {
            context.resources.getString(R.string.gender_male)
        }
        viewBinding.profileActvGender.setText(gender, false)

        // Change password section
        if (viewBinding.profileLlEditPasswordSection.isVisible) {
            decodedNewPassword = null
            viewBinding.profileEtCurrentPassword.setText("")
            viewBinding.profileEtNewPassword.setText("")
            viewBinding.profileEtConfirmNewPassword.setText("")
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun saveAllChanges(context: Context, completion: (Boolean, Boolean) -> Unit) {
        if (profileUriFromGallery != null && originalUserModel != null) {
            val formatter = SimpleDateFormat(context.getString(R.string.date_time_format_upload_image_name))

            storage.getReference("/profile-images/${formatter.format(Date())}")
                .putFile(profileUriFromGallery!!)
                .addOnCompleteListener { uploadProfileImageTask ->
                    if (uploadProfileImageTask.isSuccessful) {
                        uploadProfileImageTask.result.metadata?.reference?.downloadUrl?.addOnCompleteListener { taskUrl ->
                            if (taskUrl.isSuccessful) {
                                originalUserModel!!.profileImage = taskUrl.result.toString()
                                profileUriFromGallery = null
                                Log.i("Profile", "saveAllChanges::failed - uploaded profile image")
                                saveUserInfoAndPassword(completion)
                            }
                        }
                    } else {
                        completion(false, false)
                        Log.i("Profile", "saveAllChanges::failed - failed to upload profile image")
                    }
                }
        } else if (originalUserModel != null) {
            saveUserInfoAndPassword(completion)
        }
    }

    private fun saveUserInfoAndPassword(completion: (Boolean, Boolean) -> Unit) {
        firestore.collection(Constants.USERS_COLLECTION_NAME)
            .document(userId)
            .set(originalUserModel!!)
            .addOnCompleteListener { updateUserInfoTask ->
                if (updateUserInfoTask.isSuccessful) {
                    Log.i("Profile", "saveUserInfoAndPassword::success - updated basic info")

                    if (decodedNewPassword != null) {
                        FirebaseAuth.getInstance()
                            .currentUser!!
                            .updatePassword(decodedNewPassword!!)
                            .addOnCompleteListener { updatePasswordTask ->
                                if (updatePasswordTask.isSuccessful) {
                                    completion(true, true)
                                    Log.i("Profile", "saveUserInfoAndPassword::success - updated password")
                                } else {
                                    completion(true, false)
                                    Log.i("Profile", "saveUserInfoAndPassword::failed - failed to update password")
                                }
                            }
                    } else {
                        completion(true, true)
                    }
                } else {
                    completion(false, false)
                    Log.i("Profile", "saveUserInfoAndPassword::failed - failed to update basic info")
                }
            }
    }

    private fun convertBirthdayTimestampToString(): String {
        if (originalUserModel == null || originalUserModel?.dateOfBirth == null)
            return ""

        val vnLocale = Locale("vi", "VN")
        val formatter = SimpleDateFormat(Constants.BIRTHDAY_FORMAT, vnLocale)
        return formatter.format(originalUserModel?.dateOfBirth!!.toDate()) ?: ""
    }

    fun setFullName(newFullName: String) {
        if (originalUserModel == null)
            originalUserModel = UserModel()

        originalUserModel!!.fullName = newFullName
    }

    fun setBirthday(context: Context, newBirthday: String) {
        if (originalUserModel == null)
            originalUserModel = UserModel()

        val dobInTimestamp = UtilFunctions.convertDateStringToTimestamp(
            newBirthday,
            context.resources.getString(R.string.date_format)
        )
        originalUserModel!!.dateOfBirth = dobInTimestamp
    }

    fun setGender(context: Context, newGender: String) {
        if (originalUserModel == null)
            originalUserModel = UserModel()

        // Parse gender
        val male = context.resources.getString(R.string.gender_male)
        val savedGender = if (newGender == male) "male" else "female"
        originalUserModel!!.gender = savedGender
    }

    fun setPassword(password: String) {
        if (originalUserModel == null)
            originalUserModel = UserModel()

        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        originalUserModel!!.hashedPassword = hashedPassword
        decodedNewPassword = password
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
