package com.adzenglish.adzenglish.base.baseapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.adzenglish.adzenglish.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    private var loadingDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar()
        binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        setUpDialogLoading()
        init()
    }

    private fun setupStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            window.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS or APPEARANCE_LIGHT_NAVIGATION_BARS, APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB
    abstract fun init()

    @Throws
    open fun openFragment(
        fragmentClazz: Class<*>,
        args: Bundle?,
        addBackStack: Boolean = false
    ) {
        val tag = fragmentClazz.simpleName
        try {
            val fragment: Fragment
            try {
                fragment = (fragmentClazz.asSubclass(Fragment::class.java)).newInstance()
                    .apply { arguments = args }
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                if (addBackStack) {
                    transaction.addToBackStack(tag)
                }
                transaction.add(android.R.id.content, fragment, tag)
                transaction.commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws
    open fun showFragment(
        fragmentClazz: Class<*>,
        args: Bundle?,
        addBackStack: Boolean = false
    ) {
        val tag = fragmentClazz.simpleName
        try {
            val fragment: Fragment
            try {
                fragment = (fragmentClazz.asSubclass(Fragment::class.java)).newInstance()
                    .apply { arguments = args }

                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                if (addBackStack) {
                    transaction.addToBackStack(tag)
                }
                transaction.replace(R.id.fraIntro, fragment, tag)
                transaction.commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setUpDialogLoading() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null, false)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(view)
    }

    fun showLoadingDialog() {
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.cancel()
    }
}