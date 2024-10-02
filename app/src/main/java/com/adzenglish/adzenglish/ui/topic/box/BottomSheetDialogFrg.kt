package com.adzenglish.adzenglish.ui.topic.box

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.databinding.FragmentBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogFrg(val event: (Boolean) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomDialogBinding
    private var isSound = false
    private var isAnswer = false
    private var isTranslation = false
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.feedBackSound.setOnClickListener {
            isSound = !isSound
            updateUi(isSound, binding.ivFeedBackSound)
        }
        binding.feedBackAnswer.setOnClickListener {
            isAnswer = !isAnswer
            updateUi(isAnswer, binding.ivFeedBackAnswer)
        }
        binding.feedBackTranslation.setOnClickListener {
            isTranslation = !isTranslation
            updateUi(isTranslation, binding.ivFeedBackTranslation)
        }
        binding.tvClose.setOnClickListener {
            binding.edtContent.text.clear()
            resetData()
            dismiss()
        }
        binding.ivCloseBottom.setOnClickListener {
            binding.edtContent.text.clear()
            resetData()
            dismiss()
        }
        binding.btnFeedBack.setOnClickListener {
            val boolean = isSound || isAnswer || isTranslation
            event.invoke(boolean)
            binding.edtContent.text.clear()
            resetData()
            dismiss()
        }
    }

    private fun updateUi(isFeedback: Boolean, imageView: AppCompatImageView) {
        val boolean = (isSound || isAnswer || isTranslation)
        imageView.setImageResource(if (isFeedback) R.drawable.ic_check_true else R.drawable.ic_check_false)
        binding.btnFeedBack.setBackgroundResource(if (boolean) R.drawable.bg_text_view else R.drawable.bg_text_continue)
        binding.btnFeedBack.setTextColor(binding.root.context.getColor(if (boolean) R.color.white else R.color.gray_01))
    }

    private fun resetData() {
        isSound = false
        isAnswer = false
        isTranslation = false
    }
}