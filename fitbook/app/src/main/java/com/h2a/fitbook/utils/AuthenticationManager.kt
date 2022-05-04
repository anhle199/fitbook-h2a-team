package com.h2a.fitbook.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.h2a.fitbook.R

enum class SignInMethod { EMAIL_PASSWORD, GOOGLE }

class AuthenticationManager private constructor() {

    companion object {
        val instance = AuthenticationManager()
    }

    var isSignedIn = false
    var showAuthButtons = false
    var signInMethod: SignInMethod? = null

    private lateinit var _googleSignInClient: GoogleSignInClient
    val googleSignInClient get() = _googleSignInClient

    fun loadSignInMethod(context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            Constants.SIGN_IN_INFO_SHARE_PREFERENCES_NAME,
            Activity.MODE_PRIVATE
        )

        val signInMethodInString = sharedPreferences.getString(Constants.SIGN_IN_METHOD_KEY, "")
        if (!signInMethodInString.isNullOrEmpty()) {
            signInMethod = SignInMethod.valueOf(signInMethodInString)
        }
    }

    fun configureSignInWithGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.firebase_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        _googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun signOut(context: Context) {
        if (isSignedIn) {
            // Remove sign in method saved from share preferences
            val sharedPreferences = context.getSharedPreferences(
                Constants.SIGN_IN_INFO_SHARE_PREFERENCES_NAME,
                Activity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.remove(Constants.SIGN_IN_METHOD_KEY)
            editor.apply()

            // Sign out from google
            if (signInMethod == SignInMethod.GOOGLE) {
                googleSignInClient.signOut()
            }

            // Sign out from firebase
            Firebase.auth.signOut()
            isSignedIn = false
            signInMethod = null
        }
    }

}
