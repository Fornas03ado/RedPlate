package com.aasoftware.redplate.data

import androidx.fragment.app.Fragment
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.domain.User
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, googleSignInClient: GoogleSignInClient) {

    private val source = AuthService(auth, firestore, googleSignInClient)

    /** Launch a google login dialog. Send the result to the activity with request code 0 */
    fun requestGoogleLogin(fragment: Fragment){
        source.requestGoogleLogIn(fragment)
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

    /** Get the current Firebase Auth user (the one that is logged in) or null if no user is logged in */
    fun currentFirebaseUser(): FirebaseUser? = source.currentUser()

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

    /** Whether the user is logged in */
    fun loggedIn(): Boolean = source.currentUser() != null

    fun isEmailVerified(user: FirebaseUser): Boolean {
        return source.isEmailVerified(user)
    }

    /** Create the given [user] document in Firestore. Update its fields if it already exists */
    fun uploadUserToFirestore(user: User) {
        source.uploadUserToFirestore(user){}
    }

    /** Log out the current user if exists */
    fun signOut() {
        source.signOut()
    }
}