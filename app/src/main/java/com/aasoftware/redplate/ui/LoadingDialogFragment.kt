package com.aasoftware.redplate.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aasoftware.redplate.R

class LoadingDialogFragment(private var message: String): DialogFragment(R.layout.loading_dialog) {
    init {
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view?.findViewById<TextView>(R.id.process_text)?.text = message
    }
    fun changeMessage(s: String){
        message = s
        view?.findViewById<TextView>(R.id.process_text)?.text = s
    }
}