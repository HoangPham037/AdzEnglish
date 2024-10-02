package com.adzenglish.adzenglish.ui.notification

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.databinding.NotificationLayoutBinding

class ReusableDialog(
    val context: Context,
    val title: String,
    iconResId: Int
) {

    private var dialog: AlertDialog

    init {
        val binding = NotificationLayoutBinding.inflate(LayoutInflater.from(context))
        binding.dialogTitle.text = title
        binding.appCompatImageView3.setImageResource(iconResId)
        val builder = AlertDialog.Builder(context)
            .setView(binding.root)
        dialog = builder.create().apply {
            window?.apply {
                setGravity(Gravity.TOP)
                setBackgroundDrawableResource(android.R.color.transparent)
                attributes?.windowAnimations = R.style.DialogAnimation
            }
        }
    }

    fun showDialog() {
        dialog.apply {
            show()
            val dismissHandler = Handler(context.mainLooper)
            val dismissRunnable = Runnable {
                if (isShowing) {
                    dismiss()
                }
            }
            dismissHandler.postDelayed(dismissRunnable, 2000)
        }
    }
}