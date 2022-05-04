package com.h2a.fitbook.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.FragmentProfileBinding
import com.h2a.fitbook.utils.AuthenticationManager
import com.h2a.fitbook.utils.SignInMethod
import com.h2a.fitbook.viewmodels.ProfileViewModel
import com.h2a.fitbook.views.activities.AuthTextInputLayoutState
import java.text.SimpleDateFormat

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private val openGalleryActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = result.data?.data!!
                viewModel.profileUriFromGallery = imageUri
                binding.profileIvAvatar.setImageURI(imageUri)
            } catch (e: NullPointerException) {
                Log.i("Profile", "openGalleryActivityResult - failed to choose image from gallery - ${e.message}")
            }
        } else {
            Log.i("Profile", "openGalleryActivityResult - failed to choose image from gallery - result_canceled")
        }

        viewModel.setSelectingAvatarState(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Only visibility when signing in with username and password
        _binding!!.profileLlEditPasswordSection.isVisible = AuthenticationManager.instance.signInMethod == SignInMethod.EMAIL_PASSWORD

        viewModel = ProfileViewModel()
        viewModel.isEditing.observe(this.viewLifecycleOwner) { isEditing ->
            if (this.context != null) {
                val safeContext = this.requireContext()

                binding.profileIvAvatar.isClickable = isEditing
                binding.profileTilFullName.isEnabled = isEditing
                binding.profileTilBirthday.isEnabled = isEditing
                binding.profileTilGender.isEnabled = isEditing
                binding.profileTilCurrentPassword.isEnabled = isEditing
                binding.profileTilNewPassword.isEnabled = isEditing
                binding.profileTilConfirmNewPassword.isEnabled = isEditing

                if (isEditing) {
                    binding.profileFabSave.isVisible = true
                    binding.profileFabEditOrCancel.backgroundTintList =
                        ContextCompat.getColorStateList(safeContext, R.color.fab_close_color)
                    binding.profileFabEditOrCancel.setImageResource(R.drawable.ic_round_close_24)
                } else {
                    binding.profileFabSave.visibility = View.INVISIBLE
                    binding.profileFabEditOrCancel.backgroundTintList =
                        ContextCompat.getColorStateList(safeContext, R.color.primary)
                    binding.profileFabEditOrCancel.setImageResource(R.drawable.ic_round_edit_24)
                }
            }
        }
        viewModel.isLoading.observe(this.viewLifecycleOwner) {
            binding.profileClLoadingBackground.isVisible = it
            binding.profileFabSave.isEnabled = !it
            binding.profileFabEditOrCancel.isEnabled = !it
        }

        setupGenderAdapter()
        addActionForFABs()
        Log.i("Profile", "onCreate")
        binding.profileTilBirthday.setEndIconOnClickListener { setupAndShowDatePicker() }

        binding.profileIvAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            viewModel.setSelectingAvatarState(true)
            openGalleryActivityResult.launch(intent)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (!viewModel.isSelectingAvatar.value!!) {
            fetchUserData()
        }
    }

    private fun fetchUserData() {
        viewModel.fetchUserData { isSuccess ->
            if (isSuccess) {
                updateNavHeader()

                if (viewModel.profileImageLink.isEmpty()) {
                    binding.profileIvAvatar.setImageResource(R.drawable.default_avatar)
                } else {
                    binding.profileIvAvatar.load(viewModel.profileImageLink)
                }

                binding.profileEtFullName.setText(viewModel.fullName)
                binding.profileEtBirthday.setText(viewModel.birthday)

                val gender = if (viewModel.gender == "male") {
                    resources.getString(R.string.gender_male)
                } else {
                    resources.getString(R.string.gender_male)
                }
                binding.profileActvGender.setText(gender, false)
            }
        }
    }

    private fun setupGenderAdapter() {
        // Set adapter for Gender AutoCompleteTextView
        val options = resources.getStringArray(R.array.genders)
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            options
        ).also {
            binding.profileActvGender.setAdapter(it)
        }

        // Set default option for this
        val defaultOption =
            if (options.isNotEmpty()) options[0] else resources.getString(R.string.gender_male)
        binding.profileActvGender.setText(defaultOption, false)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupAndShowDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.sign_up_text_field_birthday_placeholder))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat(resources.getString(R.string.date_format))
            binding.profileEtBirthday.setText(dateFormatter.format(it))
        }

        datePicker.show(parentFragmentManager, "birthdaySelection")
    }

    private fun addActionForFABs() {
        binding.profileFabEditOrCancel.setOnClickListener {
            viewModel.toggleEditingState()

            if (!viewModel.isEditing.value!!) { // cancel fab
                viewModel.rollbackAllChanges(requireContext(), binding)
            }
        }

        binding.profileFabSave.setOnClickListener {
            viewModel.setLoadingState(true)

            val fullName = binding.profileEtFullName.text.toString()
            val birthday = binding.profileEtBirthday.text.toString()
            val currentPassword = binding.profileEtCurrentPassword.text.toString()
            val newPassword = binding.profileEtNewPassword.text.toString()
            val confirmNewPassword = binding.profileEtConfirmNewPassword.text.toString()

            val basicInfoValidationResult = viewModel.validateBasicInfoFields(fullName, birthday, binding, this::updateAuthFieldByState)
            val isOmitChangePasswordSection = currentPassword.isEmpty() && newPassword.isEmpty() && confirmNewPassword.isEmpty()
            var changePasswordValidationResult = true

            if (!isOmitChangePasswordSection) {
                changePasswordValidationResult = viewModel.validateChangePasswordSection(currentPassword, newPassword, confirmNewPassword, binding, this::updateAuthFieldByState)
            }

            if (
                basicInfoValidationResult
                && (isOmitChangePasswordSection || (!isOmitChangePasswordSection && changePasswordValidationResult))
            ) {
                if (!isOmitChangePasswordSection) {
                    viewModel.setPassword(newPassword)
                }

                val requiredContext = requireContext()
                viewModel.setFullName(fullName)
                viewModel.setBirthday(requiredContext, birthday)
                viewModel.setGender(requiredContext,binding.profileActvGender.text.toString())

                viewModel.saveAllChanges(requiredContext) { isSuccessToUpdateUserInfo, isSuccessToUpdatePassword ->
                    viewModel.setLoadingState(false)
                    viewModel.toggleEditingState()
                    viewModel.rollbackAllChanges(requiredContext, binding)

                    if (isSuccessToUpdateUserInfo && isSuccessToUpdatePassword) {
                        updateNavHeader()
                        Toast.makeText(requiredContext, R.string.profile_update_user_info_successfully, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requiredContext, R.string.profile_failed_to_update_user_info, Toast.LENGTH_SHORT).show()
                    }

                }
            } else {
                viewModel.setLoadingState(false)
            }
        }
    }

    private fun updateNavHeader() {
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val avatarImageView = header.findViewById<ImageView>(R.id.nav_header_avatar)
        val fullNameTextView = header.findViewById<TextView>(R.id.nav_header_full_name)

        if (viewModel.profileImageLink.isEmpty()) {
            avatarImageView.setImageResource(R.drawable.default_avatar)
        } else {
            avatarImageView.load(viewModel.profileImageLink)
        }

        fullNameTextView.text = viewModel.fullName
    }

    private fun updateAuthFieldByState(
        textInputLayout: TextInputLayout,
        helperTextResId: Int?, state: AuthTextInputLayoutState
    ) {
        textInputLayout.boxStrokeColor = viewModel.getBoxStrokeColorOfAuthFieldByState(requireContext(), state)
        textInputLayout.hintTextColor = viewModel.getHintTextColorOfAuthFieldByState(requireContext(), state)
        textInputLayout.helperText = if (helperTextResId != null) {
            resources.getString(helperTextResId)
        } else {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
