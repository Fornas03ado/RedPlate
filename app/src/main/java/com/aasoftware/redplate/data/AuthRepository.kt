package com.aasoftware.redplate.data

import androidx.fragment.app.Fragment
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.domain.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val source: AuthService){

    /** Return the last signed in Google account. In case there is not any Google signed in account,
     * send an intent to [fragment] which asks the user which Google account to use. */
    fun googleLogin(fragment: Fragment): GoogleSignInAccount?{
        val googleAccount = source.getLastGoogleSignedInAccountOrNull(fragment)
        if (googleAccount == null){
            source.requestGoogleLogIn(fragment)
        }
        return googleAccount
    }

    fun firebaseLogin(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        source.loginFirebaseAccount(email, password, onCompleteListener)
    }

    fun firebaseCreateAccount(
        email: String, password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ){
        source.createFirebaseAccount(email, password, onCompleteListener)
    }

    /** Get the current user (the one that is logged in) or null if no user is logged in */
    fun firebaseUser(): FirebaseUser? = source.currentUser()

    /** Upload the given user to the firebase database */
    fun uploadUser(user: User, onCompleteListener: OnCompleteListener<Void>) {
        source.uploadUserToFirestore(user, onCompleteListener)
    }

    /** Remove the given firebase user */
    fun removeFirebaseUser(user: FirebaseUser) = source.removeUserFromFirebase(user)

    /** Send recover password email to the given email if it exists */
    fun sendRecoverPasswordEmail(email: String, onComplete: OnCompleteListener<Void>) {
        source.sendPasswordResetEmail(email, onComplete)
    }

    fun loggedIn(): Boolean = source.currentUser() != null
}