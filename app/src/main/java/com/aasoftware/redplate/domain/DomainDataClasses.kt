package com.aasoftware.redplate.domain

import android.location.Location
import android.net.Uri

data class Gym(var id: Int, var name: String, var location: Location, var photoUrl: Uri? = null)
data class User(val id: String, val username: String, val email: String, var photoUrl: Uri? = null)

enum class AuthenticationProgress{NONE, ERROR, SUCCESS}
data class AuthenticationState(val progress: AuthenticationProgress, val error: Throwable?)
class GoogleSignInFailedException(msg: String): Throwable(msg)
class UserNotVerifiedException: Throwable()
class EmailAlreadyRegisteredException: Throwable()
class InvalidCredentialsException: Throwable()

enum class AuthErrorType{USERNAME_ERROR, EMAIL_ERROR, PASSWORD_ERROR}
data class AuthError(val errorMessage: String, val type: AuthErrorType)