package com.aasoftware.redplate.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.aasoftware.redplate.databinding.DialogVerifyEmailBinding


class VerifyEmailDialog(private val onClickListener: View.OnClickListener): DialogFragment() {
    /** Object that contains the layout view bindings */
    private lateinit var binding: DialogVerifyEmailBinding

    init {
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogVerifyEmailBinding.inflate(inflater, container, false)
        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dismissAction.setOnClickListener {
            this.dismiss()
        }
        binding.verifyAction.setOnClickListener(onClickListener)
    }
}