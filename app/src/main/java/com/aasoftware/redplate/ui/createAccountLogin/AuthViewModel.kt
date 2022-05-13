package com.aasoftware.redplate.ui.createAccountLogin

import android.content.res.Resources
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aasoftware.redplate.R
import com.aasoftware.redplate.data.AuthRepository
import com.aasoftware.redplate.domain.*
import com.aasoftware.redplate.domain.AuthenticationProgress.*
import com.aasoftware.redplate.util.Credentials.isVaildEmail
import com.aasoftware.redplate.util.Credentials.isValidPassword
import com.aasoftware.redplate.util.Credentials.isValidUsername
import com.aasoftware.redplate.util.asUser
import com.google.firebase.auth.FirebaseUser

/** Shared viewModel for the authentication process: [CreateAccountFragment],
 * [LoginFragment] and [ForgotPasswordFragment] */
class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {

    /** The current status of the login or create account operations:
    * [AuthenticationProgress.NONE] -> When no process is in progress
     * [AuthenticationProgress.IN_PROGRESS] -> When auth has been requested and viewModel
     * is waiting for result
     * [AuthenticationProgress.ERROR] -> When auth finished with an error
     * [AuthenticationProgress.SUCCESS] -> When auth finished successfully
    *  */
    private val _uiAuthState = MutableLiveData(AuthenticationState(NONE, null))
    /* Public immutable live data of  */
    val uiAuthState: LiveData<AuthenticationState> get() = _uiAuthState

    /** Check if the user has previously logged in via Google services.
     * If not, launch the Google sign in intent */
    fun requestGoogleLogin(fragment: LoginFragment) {
        authRepo.requestGoogleLogin(fragment)
    }

    fun requestFirebaseLogin(email: String, password: String){
        authRepo.requestFirebaseLogin()
    }

    /** Attempt to create account with the given parameters. The result will be sent to
     * [_uiAuthState] so that it can be observed via [uiAuthState] */
    fun createAccount(username: String, email: String, password: String){
        authRepo.requestFirebaseCreateAccount(email, password){authTask ->
            if (authTask.isSuccessful){
                /* Account was created successfully */
                val user = authRepo.firebaseUser()!!.asUser(username)
                /* Upload the account to FirebaseFirestore. This might be a WorkManager task */
                authRepo.uploadUser(user){ upload ->
                    if (upload.isSuccessful){
                        _uiAuthState.value = AuthenticationState(SUCCESS, AuthResultDomain(user, null))
                    } else {
                        deleteFirebaseUser(user)
                        _uiAuthState.value = AuthenticationState(ERROR, AuthResultDomain(null, authTask.exception))
                        Log.d("AuthViewModel", "Upload task error: ${upload.exception?.javaClass?.canonicalName}")
                    }
                }
            } else {
                // Exception is FirebaseAuthUserCollisionException if an account with the given email already exists
                Log.d("AuthViewModel", "Create task error: ${authTask.exception?.javaClass?.canonicalName}")
                _uiAuthState.value = AuthenticationState(ERROR, AuthResultDomain(null, authTask.exception))
            }
        }
    }

    /** Deletes the given user from Firebase Authentication */
    private fun deleteFirebaseUser(user: User) = authRepo.removeLastFirebaseUser(user)

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

    /** Factory that builds [AuthViewModel] from an [AuthRepository] */
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