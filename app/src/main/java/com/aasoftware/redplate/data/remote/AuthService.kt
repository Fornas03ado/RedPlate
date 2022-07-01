package com.aasoftware.redplate.data.remote

import android.content.Intent
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.domain.User
import com.aasoftware.redplate.util.FirebaseConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /** Create the Firebase account with [email] and [password] */
    fun createFirebaseAccount(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    /** Log into an existing Firebase account with [email] and [password] */
    fun loginFirebaseAccount(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    /** Get the last user that signed in this device */
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


    /** Check if the user has previously logged in via Google services and return the result */
    fun getLastGoogleSignedInAccountOrNull(fragment: Fragment): GoogleSignInAccount? =
        /* Check for existing Google Sign In account, if the user is already signed in
        the GoogleSignInAccount will be non-null. Otherwise, the method will return null */
        GoogleSignIn.getLastSignedInAccount(fragment.requireContext())

    /** Launch the google sign in intent */
    fun requestGoogleLogIn(fragment: Fragment){
        /* Configure sign-in to request the user's ID, email address, and basic
        profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        /* Build a GoogleSignInClient with the options specified by gso. */
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso);

        /* Send the google sign in intent, which displays a menu with all the accounts */
        val signInIntent: Intent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, FirebaseConstants.GOOGLE_LOGIN_RC)
    }

}