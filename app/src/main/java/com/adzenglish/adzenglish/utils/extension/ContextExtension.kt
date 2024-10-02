package com.adzenglish.adzenglish.utils.extension

import android.content.Context
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.ui.notification.ReusableDialog

fun Context.showNotifyInternet(){
        ReusableDialog(this, this.getString(R.string.txt_check_internet), R.drawable.ic_no_internet).showDialog()
}