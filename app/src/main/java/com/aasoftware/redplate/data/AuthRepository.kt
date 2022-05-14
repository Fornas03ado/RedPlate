package com.aasoftware.redplate.data

import androidx.fragment.app.Fragment
import com.aasoftware.redplate.data.remote.AuthService
import com.aasoftware.redplate.domain.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val source: AuthService){

    fun googleLogin(fragment: Fragment){
        val googleAccount = source.getLastGoogleSignedInAccountOrNull(fragment)
        if (googleAccount == null){
            source.requestGoogleLogIn(fragment)
        }
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

    /** Remove the recently created firebase user if it couldn't be uploaded to Firestore */
    fun removeLastFirebaseUser(user: User) = source.removeLastUserFromFirebase(user)

    /** Send recover password email to the given email if it exists */
    fun sendRecoverPasswordEmail(email: String, onComplete: OnCompleteListener<Void>) {
        source.sendRecoverPasswordEmail(email, onComplete)
    }
}