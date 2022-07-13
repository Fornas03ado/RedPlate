package com.aasoftware.redplate.ui.mainUI

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aasoftware.redplate.data.RemoteRepository
import com.aasoftware.redplate.ui.authenticationUI.AuthViewModel
import com.aasoftware.redplate.util.DEBUG_TAG
import com.google.firebase.auth.FirebaseUser

class MainViewModel(private val remote: RemoteRepository): ViewModel() {
    /** Private mutable liva data for [loggedIn] */
    private val _loggedIn = MutableLiveData(true)
    /** The current authentication status. */
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    /** Helper function for [checkLoggedIn]. Returns if any user is logged in */
    private fun loggedIn(): Boolean = remote.loggedIn()

    /** Checks if any user is currently logged in Firebase and if it is verified. Update [loggedIn] consequently */
    fun checkLoggedIn() {
        _loggedIn.value = loggedIn() && isEmailVerified(remote.currentFirebaseUser())
        Log.d(DEBUG_TAG, "User logged in: ${loggedIn()}")
    }

    /** @return true if [user] is not null and its email is verified, false otherwise */
    private fun isEmailVerified(user: FirebaseUser?): Boolean {
        if (user == null){
            return false
        }
        return remote.isEmailVerified(user)
    }

    /** Sign out the current user if it exists */
    fun signOut() {
        remote.signOut()
    }

    /** Factory that builds [AuthViewModel] given an [RemoteRepository].
     * @param remoteRepo: the [RemoteRepository] for the ViewModel to use. */
    class Factory(private val remoteRepo: RemoteRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(remoteRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}