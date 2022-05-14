package com.aasoftware.redplate.ui

import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aasoftware.redplate.R

class LoadingDialogFragment(private var message: String): DialogFragment(R.layout.loading_dialog) {
    init {
        isCancelable = false
        view?.findViewById<TextView>(R.id.process_text)?.text = message
    }
    fun changeMessage(s: String){
        message = s
        view?.findViewById<TextView>(R.id.process_text)?.text = s
    }
}