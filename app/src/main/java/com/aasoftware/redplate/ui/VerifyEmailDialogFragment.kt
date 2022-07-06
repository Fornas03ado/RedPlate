package com.aasoftware.redplate.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aasoftware.redplate.R


class VerifyEmailDialogFragment(private val onClickListener: View.OnClickListener): DialogFragment(R.layout.verify_email_dialog) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.verify_email_dialog, container, false)
        // Set transparent background and no title
        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        view.findViewById<TextView>(R.id.dismiss_action)?.setOnClickListener {
            this.dismiss()
        }
        view.findViewById<TextView>(R.id.verify_action)?.setOnClickListener(onClickListener)
    }
}