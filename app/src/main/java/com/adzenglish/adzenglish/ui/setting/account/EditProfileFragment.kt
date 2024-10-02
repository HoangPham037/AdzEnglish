package com.adzenglish.adzenglish.ui.setting.account

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentEditProfileBinding
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.ImageUtils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.SettingVM
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditProfileFragment : BaseFragmentWithBinding<FragmentEditProfileBinding>() {
    @Inject
    lateinit var preferences: Preferences
    private var imageView: Bitmap? = null
    private val viewModel: SettingVM by viewModels()
    private var bottomSheet: BottomSheetAccount? = null

    override fun init() {
        bottomSheet = BottomSheetAccount { type ->
            when (type) {
                Constants.TYPE_TAKE -> takePhoto()
                Constants.TYPE_CHOOSE -> chooseLibrary()
            }
        }
        viewModel.account.observe(viewLifecycleOwner) { account ->
            setupAccount(account)
        }
    }

    private fun setupAccount(account: Account?) {
        binding.edtName.text = Editable.Factory.getInstance()
            .newEditable(account?.name ?: requireContext().getString(R.string.anonymous_users))
        account?.imageView?.let { imageView ->
            Glide.with(requireContext()).load(ImageUtils.stringToBitmap(imageView))
                .fallback(R.drawable.ic_img_error)
                .placeholder(R.drawable.ic_image_default)
                .error(R.drawable.ic_img_error).into(binding.ivAvatar)
        }
    }

    override fun initData() {
        viewModel.getAccount(viewModel.repository.getDeviceId())
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.tvEditAccount.click { openBottomDialog() }
        binding.btnSave.setOnClickListener { saveProfile() }
    }

    private fun chooseLibrary() {
        ImageUtils.loadImageView(this)
    }

    private fun takePhoto() {
        ImageUtils.takePhotoFromCamera(this)
    }


    private fun saveProfile() {
        var tvName = binding.edtName.text.toString()
        if (tvName.isEmpty()) tvName = requireContext().getString(R.string.anonymous_users)
        if (viewModel.account.value == null) {
            val account = Account(
                viewModel.repository.getIdRandom(),
                viewModel.repository.getDeviceId(),
                tvName,
                Constants.INDEX_0,
                imageView?.let { ImageUtils.bitmapToString(it) },
                Constants.INDEX_0
            )
            setAccount(account)
        } else {
            viewModel.account.value?.name = tvName
            imageView?.let { viewModel.account.value?.imageView = ImageUtils.bitmapToString(it) }
            viewModel.account.value?.let { updateAccount(it) }
        }
    }

    private fun setAccount(account: Account) {
        if (isNetworkAvailable()) {
            showLoadingDialog()
            viewModel.setAccount(account) { boolean ->
                result(boolean)
            }
        } else showNotification()
    }

    private fun result(boolean: Boolean) {
        hideLoadingDialog()
        if (boolean) {
            toast("Update Success!")
            onBackPressed()
        } else toast(requireContext().getString(R.string.txt_error))
    }

    private fun showNotification() {
        requireContext().showNotifyInternet()
    }

    private fun updateAccount(account: Account) {
        if (isNetworkAvailable()) {
            showLoadingDialog()
            viewModel.updateAccount(account) { boolean ->
                result(boolean)
            }
        } else showNotification()
    }

    private fun openBottomDialog() {
        bottomSheet?.show(childFragmentManager, EditProfileFragment::class.java.name)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentEditProfileBinding {
        return FragmentEditProfileBinding.inflate(layoutInflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CODE_TAKE_PHOTO_FROM_CAMERA -> {
                    data?.extras?.get("data")?.let {
                        imageView = it as Bitmap
                        Glide.with(requireContext()).load(imageView).into(binding.ivAvatar)
                    }
                }

                Constants.REQUEST_CODE_GET_IMAGE_FROM_GALLERY -> {
                    data?.data?.let { uri ->
                        try {
                            imageView = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                uri
                            )
                            Glide.with(requireContext()).load(imageView).into(binding.ivAvatar)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}