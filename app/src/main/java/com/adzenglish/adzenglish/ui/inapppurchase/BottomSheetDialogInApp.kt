package com.adzenglish.adzenglish.ui.inapppurchase

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.adzenglish.adzenglish.databinding.FragmentBottomCloseBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogInApp(
    val tvTitle: String,
    private val tvContent: String,
    private val tvContinue: String,
    private val imgDesc: Int,
    val event: () -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomCloseBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                setupFullHeight(bottomSheet)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomCloseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = tvTitle
        binding.tvContent.text = tvContent
        binding.btnContinueLearning.text = tvContinue
        binding.imgDesc.setImageResource(imgDesc)
        binding.tvClose.setOnClickListener {
            dismiss()
        }
        binding.ivCloseBottom.setOnClickListener {
            dismiss()
        }
        binding.btnContinueLearning.setOnClickListener {
            event.invoke()
            dismiss()
        }
    }
}