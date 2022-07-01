package com.aasoftware.redplate.ui.createAccountLogin

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.*
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.domain.*
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.util.Credentials.isVaildEmail
import com.aasoftware.redplate.util.Credentials.isValidPassword
import com.aasoftware.redplate.util.Credentials.isValidUsername
import com.aasoftware.redplate.util.DEBUG_TAG
import com.aasoftware.redplate.util.asUser
import com.google.firebase.auth.FirebaseUser

/** Shared viewModel for the authentication process: [CreateAccountFragment],
 * [LoginFragment] and [ForgotPasswordFragment] */
class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {

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

    /** Check if the user has previously logged in via Google services.
     * If not, launch the Google sign in intent */
    fun requestGoogleLogin(fragment: LoginFragment) {
        authRepo.googleLogin(fragment)
    }

    /** Attempts a Firebase login with the given credentials */
    fun requestFirebaseLogin(email: String, password: String) {
        _loading.value = true
        authRepo.firebaseLogin(email, password){ task ->
            if (task.isSuccessful){
                _uiAuthState.value = AuthenticationState(SUCCESS, null)
            } else {
                _uiAuthState.value = AuthenticationState(ERROR, task.exception)
            }
            _loading.value = false
        }
    }

    /** Attempt to create account with the given parameters. The result will be sent to
     * [_uiAuthState] so that it can be observed via [uiAuthState] */
    fun createAccount(username: String, email: String, password: String){
        _loading.value = true
        authRepo.firebaseCreateAccount(email, password){ authTask ->
            if (authTask.isSuccessful){
                /* Account was created successfully */
                val firebaseUser = authRepo.firebaseUser()!!
                val user = firebaseUser.asUser(username)
                /* Upload the account to Firebase Firestore. This might be a WorkManager task
                 as it must be completed */
                authRepo.uploadUser(user){ upload ->
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

    /** Deletes the given user from Firebase Authentication */
    private fun deleteFirebaseUser(user: FirebaseUser) = authRepo.removeFirebaseUser(user)

    /** Restore the [_uiAuthState] value to show [NONE] after the result has been processed */
    fun onResultReceived(){
        _uiAuthState.value = AuthenticationState(NONE, null)
    }

    /** Returns an [AuthError] with the error message and [AuthErrorType] in case there's an error
     * with the input. Returns null if there's no error */
    fun validateInput(
        username: String, email: String, password: String, confirmPassword: String, resources: Resources
    ): AuthError?{
        if (!username.isValidUsername()){
            return AuthError(resources.getString(R.string.username_error_text),
                AuthErrorType.USERNAME_ERROR
            )
        } else if(!email.isVaildEmail()){
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
        authRepo.sendRecoverPasswordEmail(email){ task ->
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

    /** Helper function for [checkLoggedIn]. Returns if any user is logged in */
    private fun loggedIn(): Boolean = authRepo.loggedIn()

    /** Checks if any user is currently logged in Firebase. Update [authFinished] consequently */
    fun checkLoggedIn() {
        _authFinished.value = loggedIn()
        Log.d(DEBUG_TAG, "User logged in: ${loggedIn()}")
    }

    /** Factory that builds [AuthViewModel] given an [AuthRepository] */
    class Factory(private val authRepo: AuthRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}