package com.aasoftware.redplate.data

import androidx.fragment.app.Fragment
import com.aasoftware.redplate.data.remote.FirebaseAuthService
import com.aasoftware.redplate.data.remote.GoogleAuthService
import com.aasoftware.redplate.domain.User
import com.aasoftware.redplate.util.FirebaseConstants
import com.aasoftware.redplate.util.FirebaseConstants.EMAIL_KEY
import com.aasoftware.redplate.util.FirebaseConstants.UID_KEY
import com.aasoftware.redplate.util.FirebaseConstants.USERNAME_KEY
import com.aasoftware.redplate.util.FirebaseConstants.USERS_PATH
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val firebaseSource: FirebaseAuthService,
    private val googleSource: GoogleAuthService
    ){

    fun requestGoogleLogin(fragment: Fragment){
        val googleAccount = googleSource.getLastSignedInAccountOrNull(fragment)
        if (googleAccount == null){
            googleSource.requestLogIn(fragment)
        }
    }
    fun requestFirebaseLogin(){}

    fun requestFirebaseCreateAccount(
        email: String, password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ){
        firebaseSource.createAccount(email, password, onCompleteListener)
    }

    fun firebaseUser(): FirebaseUser? = firebaseSource.currentUser()

    fun uploadUser(user: User, onCompleteListener: OnCompleteListener<Void>) {
        firebaseSource.uploadUserToFirestore(user, onCompleteListener)
    }

    /** Remove the last */
    fun removeLastFirebaseUser(user: User) = firebaseSource.removeLastUserFromFirebase(user)
}