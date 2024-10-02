package com.adzenglish.adzenglish.ui.rank.profile

import android.os.Bundle
import android.view.LayoutInflater
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentProfileNameBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileNameFragment : BaseFragmentWithBinding<FragmentProfileNameBinding>(), MyTextWatcher {
    @Inject
    lateinit var preferences: Preferences
    private var tvName: String? = null

    override fun init() {
        binding.edtName.addTextChangedListener(this)
    }

    override fun initData() {
       // do nothing
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSave.setOnClickListener {
            if (binding.edtName.text.toString().isNotEmpty()) {
                Bundle().apply {
                    putString(Constants.KEY_NAME_USER, tvName)
                    openFragment(ProfilePhotoFragment::class.java, this, true)
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        tvName = s.toString()
        binding.btnSave.setTextColor(requireContext().getColor(if (s?.length == Constants.INDEX_0) R.color.gray_01 else R.color.white))
        binding.btnSave.setBackgroundResource(if (s?.length == Constants.INDEX_0) R.drawable.bg_text_continue else R.drawable.bg_text_view)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentProfileNameBinding {
        return FragmentProfileNameBinding.inflate(layoutInflater)
    }
}