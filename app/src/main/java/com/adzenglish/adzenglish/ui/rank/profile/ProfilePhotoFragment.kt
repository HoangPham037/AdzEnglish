package com.adzenglish.adzenglish.ui.rank.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentProfilePhotoBinding
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.setting.account.BottomSheetAccount
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.ImageUtils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.SettingVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePhotoFragment : BaseFragmentWithBinding<FragmentProfilePhotoBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private var tvName: String? = null
    private var imageView: Bitmap? = null
    private val viewModel: SettingVM by viewModels()
    private var bottomSheet: BottomSheetAccount? = null
    override fun init() {
        bottomSheet = BottomSheetAccount { type ->
            when (type) {
                Constants.TYPE_TAKE -> ImageUtils.takePhotoFromCamera(this)
                Constants.TYPE_CHOOSE -> ImageUtils.loadImageView(this)
            }
        }
        imageView = ImageUtils.stringToBitmap(preferences.getString(Constants.KEY_IMAGEVIEW))
        updateUi(imageView)
    }

    override fun initData() {
        arguments?.let { bundle ->
            tvName = bundle.getString(Constants.KEY_NAME_USER)
            tvName?.let {
                if (it.isNotEmpty()) binding.tvName.text =
                    it.toCharArray()[Constants.INDEX_0].toString()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA -> {
                    data?.extras?.get("data")?.let {
                        imageView = it as Bitmap
                        updateUi(imageView)
                    }
                }

                Constants.REQUEST_CODE_GET_IMAGE_FROM_GALLERY -> {
                    data?.data?.let { uri ->
                        imageView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    requireContext().contentResolver, uri
                                )
                            )
                        } else {
                            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        }
                        updateUi(imageView)
                    }
                }
            }
        }
    }

    private fun updateUi(imageView: Bitmap?) {
        imageView?.let {
            binding.tvName.gone()
            binding.ivAvatar.setImageBitmap(imageView)
        }
        binding.btnAddPhoto.text =
            requireContext().getString(if (imageView == null) R.string.txt_add_photo else R.string.txt_continue)
        binding.tvNoReward.text =
            requireContext().getString(if (imageView == null) R.string.txt_reward_no else R.string.txt_edit_account)
    }

    private fun saveProfile(point: Int) {
        if (isNetworkAvailable()) {
            showLoadingDialog()
            val account = Account(
                viewModel.getIdRandom(),
                viewModel.repository.getDeviceId(),
                tvName,
                Constants.INDEX_0,
                imageView?.let { ImageUtils.bitmapToString(it) },
                point)
            viewModel.saveAccount(account) { boolean ->
                hideLoadingDialog()
                if (boolean) onBackPressed() else toast(requireContext().getString(R.string.txt_error))
            }
        } else requireContext().showNotifyInternet()
    }

    override fun initAction() {
        binding.btnAddPhoto.click {
            if (imageView == null) openBottomDialog() else saveProfile(Constants.INDEX_10)
        }
        binding.ivAvatar.click {
            openBottomDialog()
        }
        binding.tvNoReward.setOnClickListener {
            if (imageView == null)
                saveProfile(Constants.INDEX_0)
            else openBottomDialog()
        }
    }

    private fun openBottomDialog() {
        bottomSheet?.show(childFragmentManager, ProfilePhotoFragment::class.java.name)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentProfilePhotoBinding {
        return FragmentProfilePhotoBinding.inflate(layoutInflater)
    }
}