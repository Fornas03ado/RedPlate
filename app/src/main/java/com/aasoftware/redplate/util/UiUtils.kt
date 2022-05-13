package com.aasoftware.redplate.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.R

/** Hide the keyboard programmatically */
fun hideSoftKeyboard(fragment: Fragment) {
    (fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(fragment.requireActivity().currentFocus?.windowToken, 0)
    }
}

/** Set the default non-error drawable to the edit text */
fun EditText.defaultDrawables(@DrawableRes drawableId: Int){
    val tint = ContextCompat.getColor(this.context, R.color.input_drawable_tint)
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        ContextCompat.getDrawable(this.context, drawableId)!!
            .apply { setTint(tint) },
        null, null, null
    )
}
/** Set the error drawables to the edit text */
fun EditText.errorDrawables(@DrawableRes drawableId: Int){
    val tint = ContextCompat.getColor(this.context, R.color.error_color)
    val errorDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_error_24)
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        ContextCompat.getDrawable(this.context, drawableId)!!
            .apply { setTint(tint) },
        null, errorDrawable, null
    )
}