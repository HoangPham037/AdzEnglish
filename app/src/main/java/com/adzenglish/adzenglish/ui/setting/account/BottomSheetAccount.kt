package com.adzenglish.adzenglish.ui.setting.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adzenglish.adzenglish.databinding.FragmentBottomDialogAccountBinding
import com.adzenglish.adzenglish.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetAccount(val event: (String) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomDialogAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomDialogAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.ivCloseBottom.setOnClickListener { dismiss() }
        binding.btnTakePhoto.setOnClickListener {
            event.invoke(Constants.TYPE_TAKE)
            dismiss()
        }
        binding.btnChooseLibrary.setOnClickListener {
            event.invoke(Constants.TYPE_CHOOSE)
            dismiss()
        }
    }
}