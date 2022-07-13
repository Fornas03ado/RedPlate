package com.aasoftware.redplate.ui.authenticationUI

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.RemoteRepository
import com.aasoftware.redplate.domain.*
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.util.Credentials.isValidEmail
import com.aasoftware.redplate.util.Credentials.isValidPassword
import com.aasoftware.redplate.util.Credentials.isValidUsername
import com.aasoftware.redplate.util.DEBUG_TAG
import com.aasoftware.redplate.util.asUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/** Shared viewModel that will handle the authentication business logic. */
class AuthViewModel(private val remoteRepo: RemoteRepository) : ViewModel() {

    /** Private mutable liva data for [uiAuthState] */
    private val _uiAuthState = MutableLiveData(AuthenticationState(NONE, null))
    /** The current status of the login or create account operations:
     * [AuthenticationProgress.NONE] -> When no process is in progress or
     * is waiting for result
     * [AuthenticationProgress.ERROR] -> When auth finished with an error
     * [AuthenticationProgress.SUCCESS] -> When auth finished successfully
     *  */
    val uiAuthState: LiveData<AuthenticationState> get() = _uiAuthState

    /** Private mutable liva data for [loading] */
    private val _loading = MutableLiveData(false)
    /** Live data that represents whether the app is in loading state. It is,
     * then, the loading dialog visibility */
    val loading: LiveData<Boolean> get() = _loading

    /** Private mutable liva data for [authFinished] */
    private val _authFinished = MutableLiveData<Boolean?>(null)
    /** Whether authentication process has finished and activity is ready to navigate to
     *  PresentationActivity .
     *  null when logged state is being checked at the beginning*/
    val authFinished: LiveData<Boolean?> get() = _authFinished

    /** Last user that was successfully logged in. Even if it is currently not */
    var lastUser: FirebaseUser? = null

    /* Regular arguments and SafeArgs are not willing to work for some reason, so I am in
    * in need of using ViewModel variables instead to communicate between fragments */
    var navEmail: String? = null
    var navPassword: String? = null

    init {
        Log.d(DEBUG_TAG, "AuthViewModel init")
    }

    /** Check if the user has previously logged in via Google services.
     * If not, launch the Google sign in intent */
    fun requestGoogleLogin(fragment: Fragment) {
        remoteRepo.requestGoogleLogin(fragment)
    }

    /** Attempts a Firebase login with the given credentials */
    fun requestFirebaseLogin(email: String, password: String) {
        _loading.value = true
        remoteRepo.firebaseLogin(email, password){ task ->
            if (task.isSuccessful){
                val user = remoteRepo.currentFirebaseUser()
                lastUser = user
                if (user != null && remoteRepo.isEmailVerified(user)){
                    _uiAuthState.value = AuthenticationState(SUCCESS, null)
                } else {
                    _uiAuthState.value = AuthenticationState(ERROR, UserNotVerifiedException())
                    signOut()
                }
            } else {
                _uiAuthState.value = AuthenticationState(ERROR, InvalidCredentialsException())
            }
            _loading.value = false
        }
    }

    /** Sign out the current user if it exists */
    private fun signOut() {
        remoteRepo.signOut()
    }

    /** Attempt to create account with the given parameters. The result will be sent to
     * [_uiAuthState] so that it can be observed via [uiAuthState] */
    fun createAccount(username: String, email: String, password: String){
        _loading.value = true
        remoteRepo.firebaseCreateAccount(email, password){ authTask ->
            if (authTask.isSuccessful){
                /* Account was created successfully */
                val firebaseUser = remoteRepo.currentFirebaseUser()!!
                val user = firebaseUser.asUser(username)
                /* Upload the account to Firebase Firestore. This might be a WorkManager task
                 as it must be completed */
                remoteRepo.uploadUser(user){ upload ->
                    if (upload.isSuccessful){
                        _uiAuthState.value = AuthenticationState(SUCCESS, null)
                    } else {
                        deleteFirebaseUser(firebaseUser)
                        _uiAuthState.value = AuthenticationState(ERROR, upload.exception)
                        Log.d("AuthViewModel", "Upload task error: ${upload.exception?.javaClass?.canonicalName}")
                    }
                    _loading.value = false
                }

                // Send a verification email
                firebaseUser.sendEmailVerification()
            } else {
                // Exception is FirebaseAuthUserCollisionException if an account with the given email already exists
                Log.d("AuthViewModel", "Create task error: ${authTask.exception?.javaClass?.canonicalName}")
                _uiAuthState.value = AuthenticationState(ERROR, authTask.exception)
                _loading.value = false
            }
        }
    }

    /** Deletes the given [user] from Firebase Authentication */
    private fun deleteFirebaseUser(user: FirebaseUser) = remoteRepo.removeFirebaseUser(user)

    /** Restore the [_uiAuthState] value to show [NONE] after the result has been processed */
    fun onResultReceived(){
        _uiAuthState.value = AuthenticationState(NONE, null)
    }

    /** Returns an [AuthError] with the error message and [AuthErrorType] in case there's an error
     * with the input or null if there's no error */
    fun validateInput(
        username: String, email: String, password: String, confirmPassword: String, resources: Resources
    ): AuthError?{
        if (!username.isValidUsername()){
            return AuthError(resources.getString(R.string.username_error_text),
                AuthErrorType.USERNAME_ERROR
            )
        } else if(!email.isValidEmail()){
            return AuthError(resources.getString(R.string.email_error_text),
                AuthErrorType.EMAIL_ERROR
            )
        } else if(!password.isValidPassword()){
            return AuthError(resources.getString(R.string.password_error_text),
                AuthErrorType.PASSWORD_ERROR
            )
        } else if(password != confirmPassword){
            return AuthError(resources.getString(R.string.password_match_error_text),
                AuthErrorType.PASSWORD_ERROR
            )
        }
        // All the fields are correct
        return null
    }

    /** Sends a password reset email to the given email via firebase. Updates [uiAuthState]
     * with the result*/
    fun recoverPassword(email: String) {
        _loading.value = true
        remoteRepo.sendRecoverPasswordEmail(email){ task ->
            if (task.isSuccessful){
                _uiAuthState.value = AuthenticationState(SUCCESS, null)
            } else {
                _uiAuthState.value = AuthenticationState(ERROR, task.exception)
            }
            _loading.value = false
        }
    }

    /** Notify that authentication process has finished via [authFinished] */
    fun onAuthenticationFinished() {
        _loading.value = false
        _uiAuthState.value = AuthenticationState(NONE, null)
        _authFinished.value = true
    }

    /** Helper function for [checkAuthState]. Returns if any user is logged in */
    private fun loggedIn(): Boolean = remoteRepo.loggedIn()

    /** Checks if any user is currently logged in Firebase and if it is verified. Update [authFinished] consequently */
    fun checkAuthState() {
        _authFinished.value = loggedIn() && isEmailVerified(remoteRepo.currentFirebaseUser())
        Log.d(DEBUG_TAG, "User logged in: ${loggedIn()}")
    }

    /** Handle Google sign in result from the receiver activity */
    fun onGoogleSignInResult(activity: Activity, remote: RemoteRepository, data: Intent) {
        val signInTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google sign in was successful
            _loading.value = true
            val account = signInTask.getResult(ApiException::class.java)
            Log.d(DEBUG_TAG, "firebaseAuthWithGoogle: id = ${account.idToken}")

            // Authenticate in Firebase auth with the Google account
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            remote.signInWithCredential(credential){ authTask ->
                if (authTask.isSuccessful) {
                    // Firebase sign in was successful. Upload the user to Firestore.
                    val user = User(remote.currentUser()!!.uid, account.displayName!!, remote.currentUser()!!.email!!, account.photoUrl)
                    remoteRepo.uploadUserToFirestore(user)
                    _uiAuthState.value = AuthenticationState(SUCCESS, null)
                } else {
                    // Google sign in failed, update UI appropriately
                    Log.w(DEBUG_TAG, "Error authenticating in Firebase: ${authTask.exception?.localizedMessage}")
                    _uiAuthState.value = AuthenticationState(ERROR,
                        GoogleSignInFailedException(activity.getString(R.string.error_while_signing_in)))
                    _loading.value = false
                }
            }
        } catch (e: ApiException){
            // Google sign in failed, update UI appropriately
            Log.w(DEBUG_TAG, "Error authenticating with Google")
            _uiAuthState.value = AuthenticationState(ERROR,
                GoogleSignInFailedException(activity.getString(R.string.error_while_signing_in)))
            _loading.value = false
        }
    }

    /** @return true if [user] is not null and its email is verified, false otherwise */
    private fun isEmailVerified(user: FirebaseUser?): Boolean {
        if (user == null){
            return false
        }
        return remoteRepo.isEmailVerified(user)
    }

    /** Factory that builds [AuthViewModel] given an [RemoteRepository].
     * @param remoteRepo: the [RemoteRepository] for the ViewModel to use. */
    class Factory(private val remoteRepo: RemoteRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(remoteRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}