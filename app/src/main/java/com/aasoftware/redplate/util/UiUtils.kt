package com.aasoftware.redplate.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aasoftware.redplate.R
import com.google.android.material.snackbar.Snackbar

/** Hide the keyboard programmatically */
fun hideSoftKeyboard(fragment: Fragment) {
    (fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(fragment.requireActivity().currentFocus?.windowToken, 0)
    }
}

/** Set the default non-error drawable to the edit text */
fun EditText.defaultDrawables(@DrawableRes drawableId: Int){
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        ContextCompat.getDrawable(this.context, drawableId),
        null, null, null
    )
}
/** Set the error drawables to the edit text */
fun EditText.errorDrawables(@DrawableRes drawableId: Int){
    val tint = ContextCompat.getColor(this.context, R.color.error_color)
    val errorDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_error_24)
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        ContextCompat.getDrawable(this.context, drawableId)!!.mutate()
            .apply { setTint(tint) },
        null, errorDrawable, null
    )
}

/** Create a Snackbar with the given message */
fun Fragment.makeIndefiniteSnackbar(@StringRes msgId: Int) = makeIndefiniteSnackbar(getString(msgId))
/** Create a Snackbar with the given message */
fun Fragment.makeIndefiniteSnackbar(msg: String) = Snackbar.make(requireView(), msg,
    Snackbar.LENGTH_INDEFINITE)
    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.panel_color))
    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
    .setTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
    .apply {setAction(R.string.ok){dismiss()}; show()}

/** Create a Snackbar with the given message */
fun Fragment.makeLongSnackbar(@StringRes msgId: Int) = makeLongSnackbar(getString(msgId))
/** Create a Snackbar with the given message */
fun Fragment.makeLongSnackbar(msg: String) = Snackbar.make(requireView(), msg,
    Snackbar.LENGTH_LONG)
    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.panel_color))
    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
    .setTextColor(ContextCompat.getColor(requireContext(), R.color.input_text_color))
    .apply {show()}