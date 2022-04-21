package com.h2a.fitbook.utils

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

enum class SignInMethod { EMAIL_PASSWORD, GOOGLE }

object AuthenticationManager {
    var isSignedIn = false
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var signInMethod: SignInMethod

    fun signOut() {
        if (isSignedIn) {
            when (signInMethod) {
                SignInMethod.EMAIL_PASSWORD -> {
                    FirebaseAuth.getInstance().signOut()
                    isSignedIn = false
                }
                SignInMethod.GOOGLE -> {
                    FirebaseAuth.getInstance().signOut()
                    googleSignInClient.signOut()
                    isSignedIn = false
                }
            }
        }
    }
}
