package com.aasoftware.redplate.data.remote

import android.content.Intent
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.domain.User
import com.aasoftware.redplate.util.FirebaseConstants
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthService(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient) {

    /** Create the Firebase account with [email] and [password] */
    fun createFirebaseAccount(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    /** Log into an existing Firebase account with [email] and [password] */
    fun loginFirebaseAccount(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    /** Get the FirebaseAuth user that is signed in */
    fun currentUser() = auth.currentUser

    /** Removes last FirebaseAuth user that signed in this device if it exists */
    fun removeUserFromFirebase(user: FirebaseUser) {
        user.delete()
    }

    /** Upload [user] to the Firestore database */
    fun uploadUserToFirestore(user: User, onCompleteListener: OnCompleteListener<Void>){
        val map = HashMap<String, Any>().apply {
            put(FirebaseConstants.USERNAME_KEY, user.username)
            put(FirebaseConstants.EMAIL_KEY, user.email)
            put(FirebaseConstants.UID_KEY, user.id)
        }
        firestore.collection(FirebaseConstants.USERS_PATH).document(user.id)
            .set(map)
            .addOnCompleteListener(onCompleteListener)
    }

    /** Send a reset password email to the given [email] if it exists */
    fun sendPasswordResetEmail(email: String, onComplete: OnCompleteListener<Void>) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(onComplete)
    }

    /** Launch the google sign in intent */
    fun requestGoogleLogIn(fragment: Fragment){
        /* Send the google sign in intent, which displays a menu with all the accounts */
        val signInIntent: Intent = googleSignInClient.signInIntent
        fragment.requireActivity().startActivityForResult(signInIntent, FirebaseConstants.GOOGLE_LOGIN_RC)
    }

    /** Return true if the user has verified its email */
    fun isEmailVerified(user: FirebaseUser): Boolean {
        return user.isEmailVerified
    }

    /** Sign out the current user if exists */
    fun signOut() {
        auth.signOut()
    }

}