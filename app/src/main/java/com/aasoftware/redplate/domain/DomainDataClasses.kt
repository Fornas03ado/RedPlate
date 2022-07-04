package com.aasoftware.redplate.domain

import android.location.Location

data class Gym(var id: Int, var name: String, var location: Location)
data class User(val id: String, val username: String, val email: String)

enum class AuthenticationProgress{NONE, ERROR, SUCCESS}
data class AuthenticationState(val progress: AuthenticationProgress, val error: Throwable?)
class GoogleSignInFailedException(msg: String) : Exception(msg)

enum class AuthErrorType{USERNAME_ERROR, EMAIL_ERROR, PASSWORD_ERROR}
data class AuthError(val errorMessage: String, val type: AuthErrorType)