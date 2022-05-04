package com.h2a.fitbook.models

import com.google.firebase.Timestamp

data class UserModel(
    var userId: String = "",
    var providerId: String = "",
    var fullName: String = "",
    var dateOfBirth: Timestamp? = null,
    var gender: String? = null,
    var profileImage: String = "",
    var hashedPassword: String? = null,
)
