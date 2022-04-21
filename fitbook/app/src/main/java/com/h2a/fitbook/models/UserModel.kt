package com.h2a.fitbook.models

import com.google.firebase.Timestamp

data class UserModel(
    val userId: String,
    val providerId: String,
    val fullName: String,
    val dateOfBirth: Timestamp?,
    val gender: String?,
    val profileImage: String,
)
