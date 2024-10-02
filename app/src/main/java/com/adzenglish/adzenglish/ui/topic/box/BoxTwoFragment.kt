package com.adzenglish.adzenglish.ui.topic.box

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentBoxTwoBinding
import com.adzenglish.adzenglish.models.local.room.entity.WordThemeEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.TopicViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BoxTwoFragment : BaseFragmentWithBinding<FragmentBoxTwoBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private val viewModel: TopicViewModel by viewModels()
    override fun getViewBinding(inflater: LayoutInflater): FragmentBoxTwoBinding {
        return FragmentBoxTwoBinding.inflate(layoutInflater)
    }

    override fun init() {
        if (!isNetworkAvailable()) requireContext().showNotifyInternet()
        viewModel.wordTheme.observe(viewLifecycleOwner) { updateUi(it) }
    }

    private fun updateUi(wordTheme: WordThemeEntity) {
        viewModel.upDateWordTheme(wordTheme)
        binding.tvTitle.text = wordTheme.title
        binding.description.text = wordTheme.description
        Glide.with(requireContext()).load(wordTheme.headerImage).error(R.drawable.ic_img_error)
            .placeholder(R.drawable.ic_image_default).fallback(R.drawable.ic_img_error).into(binding.ivDetail)
    }

    override fun initData() {
        arguments?.getInt(Constants.KEY_ID_WORD_THEME)?.let {
            viewModel.getWordThemeById(it)
        }
    }

    override fun initAction() {
        binding.btnCheckYourself.setOnClickListener {
            viewModel.wordTheme.value?.let { wordTheme ->
                Bundle().apply {
                    putInt(Constants.KEY_WORD_THEME, wordTheme.id)
                    openFragment(BoxThreeFragment::class.java, this, true)
                }
            }
        }
        binding.tvNo.click {
            onBackPressed()
        }
    }
}