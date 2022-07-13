package com.aasoftware.redplate

import android.app.Application
import com.aasoftware.redplate.data.RemoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RedplateApplication: Application() {
    /** Client that provides and manages Firebase Authentication */
    private lateinit var auth: FirebaseAuth
    /** Client that provides access to Read-Write-Update operations in Firebase Firestore */
    private lateinit var firestore: FirebaseFirestore
    /** Client that provides Google Sign in method */
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        Injection.remoteRepository = RemoteRepository(auth, firestore, googleClient)
    }
}