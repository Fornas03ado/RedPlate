package com.aasoftware.redplate.util

import com.aasoftware.redplate.domain.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

/** CREDENTIALS VALIDATORS */
object Credentials{
    /** Returns true if email fits [android.util.Patterns.EMAIL_ADDRESS] */
    fun String.isValidEmail() : Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

    /** Returns true if username is between 4 and 20 characters long */
    fun String.isValidUsername(): Boolean = length in 4..20

    /** Returns true if password:
     *  Has at least 8 characters
     *  Has at least one number
     *  Has at least one lowercase
     *  Has at least one uppercase
     *  Has at least one special character*/
    fun String.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!/|'()_<>{}¨*¿?;:.,€¡])(?=\\S+$).{3,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }
}

fun FirebaseUser.asUser(username: String) = User(this.uid, username, this.email!!)

/** Id still to be changed by Firebase one */
fun GoogleSignInAccount.asUser(): User = User(this.id!!, this.displayName!!, this.email!!)