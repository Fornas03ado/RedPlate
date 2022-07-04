package com.aasoftware.redplate.data

import android.app.Activity
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.domain.User
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, oneTapClient: SignInClient) {

    private val source = AuthService(auth, firestore, oneTapClient)

    /** Launch a google login dialog. Send the result to [activity] with request code 0 */
    fun launchGoogleLogin(activity: Activity, onCompleteListener: OnCompleteListener<BeginSignInResult>){
        source.requestGoogleLogIn(activity, onCompleteListener)
    }

    /** Perform a login attempt in firebase auth. Result is observed by [onCompleteListener] */
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