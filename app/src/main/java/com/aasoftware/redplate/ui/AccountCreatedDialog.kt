package com.aasoftware.redplate.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.aasoftware.redplate.databinding.DialogAccountCreatedBinding

class AccountCreatedDialog(private val onClickListener: View.OnClickListener): DialogFragment() {
    private lateinit var binding: DialogAccountCreatedBinding

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAccountCreatedBinding.inflate(layoutInflater, container, false)
        binding.okAction.setOnClickListener(onClickListener)
        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }
}