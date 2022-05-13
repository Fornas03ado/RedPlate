package com.aasoftware.redplate.data.remote

import com.aasoftware.redplate.domain.User
import com.aasoftware.redplate.util.FirebaseConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /** Create the Firebase account with [email] and [password] */
    fun createAccount(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    /** Log into an existing Firebase account with [email] and [password] */
    fun loginAccount(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
    }

    /** Get the last user that signed in this device */
    fun currentUser() = auth.currentUser

    /** Removes last FirebaseAuth user that signed in this device if it exists */
    fun removeLastUserFromFirebase(user: User) {
        if (currentUser()?.uid == user.id){
            currentUser()?.delete()
        }
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

}