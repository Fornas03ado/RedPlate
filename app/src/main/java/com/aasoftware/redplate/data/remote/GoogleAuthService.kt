package com.aasoftware.redplate.data.remote

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthService {

    /** Check if the user has previously logged in via Google services and return the result */
    fun getLastSignedInAccountOrNull(fragment: Fragment): GoogleSignInAccount? =
        /* Check for existing Google Sign In account, if the user is already signed in
        the GoogleSignInAccount will be non-null. Otherwise, the method will return null */
        GoogleSignIn.getLastSignedInAccount(fragment.requireContext())


    fun requestLogIn(fragment: Fragment){
        /* Configure sign-in to request the user's ID, email address, and basic
        profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        /* Build a GoogleSignInClient with the options specified by gso. */
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso);

        /* Start the google sign in intent, which displays a menu with all the accounts */
        val signInIntent: Intent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, 0)
    }
}