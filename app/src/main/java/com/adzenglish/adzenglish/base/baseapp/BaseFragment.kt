package com.adzenglish.adzenglish.base.baseapp


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.adzenglish.adzenglish.utils.extension.hideKeyboard
import com.google.android.material.appbar.MaterialToolbar


abstract class BaseFragment : Fragment() {
    private var activity : BaseActivity<*>? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnApplyWindowInsetsListener { v, insets ->
            (v as? ViewGroup)?.forEach { child ->
                child.dispatchApplyWindowInsets(insets)
            }
            insets
        }
        view.setOnTouchListener { v, _ ->
            v.hideKeyboard(requireActivity())
            v.clearFocus()
            true
        }
        activity = requireActivity() as BaseActivity<*>
        init()
        initData()
        initAction()
    }
    fun openFragment(fragmentClazz: Class<*>, args: Bundle?,
        addBackStack: Boolean
    ) {
        activity?.openFragment(fragmentClazz, args, addBackStack)
    }
    fun showFragment(fragmentClazz: Class<*>, args: Bundle?,
                     addBackStack: Boolean
    ) {
        activity?.showFragment(fragmentClazz, args, addBackStack)
    }

    open fun setToolbar(view: MaterialToolbar, onclick: (() -> Unit)? = null) {
        view.setNavigationOnClickListener {
            activity?.onBackPressed()
            onclick?.invoke()
        }
    }

    fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }
    fun onBackPressed() {
        activity?.onBackPressed()
    }
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    abstract fun init()
    abstract fun initData()
    abstract fun initAction()
    fun showLoadingDialog() = activity?.showLoadingDialog()
    fun hideLoadingDialog() = activity?.hideLoadingDialog()

}