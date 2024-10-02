package com.adzenglish.adzenglish.ui.setting

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentSettingBinding
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.notification.ReusableDialog
import com.adzenglish.adzenglish.ui.setting.account.EditProfileFragment
import com.adzenglish.adzenglish.ui.setting.level.BottomSheetLevel
import com.adzenglish.adzenglish.ui.setting.rate.RateFragment
import com.adzenglish.adzenglish.ui.setting.remind.BottomSheetTimer
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.ImageUtils
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.SettingVM
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragmentWithBinding<FragmentSettingBinding>() {
    private val viewModel: SettingVM by viewModels()
    private var dialogLevel: BottomSheetLevel? = null
    private var dialogTimer: BottomSheetTimer? = null
    private var openSound: Boolean? = null
    private var hour = Constants.INDEX_0
    private var minute = Constants.INDEX_0
    private val REQUEST_PERMISSION_NOTIFICATION = 103

    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constants.KEY_RATE) binding.tbEvaluate.gone()
        }

    override fun init() {
        dialogLevel = BottomSheetLevel(viewModel, preferences)
        dialogTimer = BottomSheetTimer(preferences) { newHour, newMinute ->
            hour = newHour
            minute = newMinute
            setupTextTime(newHour, newMinute)
            startAlarmManager(newHour, newMinute)
        }
        viewModel.level.observe(viewLifecycleOwner) { dataStatusResult ->
            setupTextViewLevel(dataStatusResult)
        }
        viewModel.account.observe(viewLifecycleOwner) { account ->
            setupAccount(account)
        }
        openSound = preferences.getBoolean(Constants.KEY_OPEN_SOUND)
        setupSound(openSound ?: true)
        hour = preferences.getInt(Constants.KEY_HOUR)
        minute = preferences.getInt(Constants.KEY_MINUTE)
        setupTextTime(hour, minute)
        if (preferences.getString(Constants.KEY_RATE) != null) binding.tbEvaluate.gone()
    }


    private fun setupAccount(account: Account?) {
        binding.tvName.text = if (account?.name?.isEmpty() == true || account?.name == null)
            requireContext().getString(R.string.txt_anonymous_user) else account.name
        binding.tvId.text = String.format("ID ${account?.id ?: viewModel.getIdRandom()}")
        Glide.with(requireContext()).load(ImageUtils.stringToBitmap(account?.imageView))
            .error(R.drawable.iv_avt).into(binding.ivAvatar)
    }

    private fun setupTextViewLevel(dataStatusResult: DataStatus<LevelEntity>) {
        when (dataStatusResult.status) {
            DataStatus.Status.SUCCESS -> dataStatusResult.data?.let { level ->
                binding.tvLevel.text = level.title
            }

            DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
        }
    }

    private fun setupSound(openSound: Boolean) {
        binding.ivCheckSound.setImageResource(if (openSound) R.drawable.ic_check_sound_true else R.drawable.ic_check_sound_false)
    }

    override fun initData() {
        viewModel.getLevelFromRoom()
        viewModel.getAccount(viewModel.repository.getDeviceId())
    }

    private fun setupTextTime(newHour: Int, newMinute: Int) {
        binding.tvTime.text = String.format(
            "${
                newHour.toString().padStart(Constants.INDEX_2, '0')
            } : ${newMinute.toString().padStart(Constants.INDEX_2, '0')}"
        )
    }

    override fun initAction() {
        binding.tbLevel.click {
            dialogLevel?.show(childFragmentManager, SettingFragment::class.java.name)
        }
        binding.tbSound.setOnClickListener {
            clickSound()
        }
        binding.tbEvaluate.setOnClickListener {
            openFragment(RateFragment::class.java, null, true)
        }
        binding.tbDelete.setOnClickListener {
            opDialogDelete()
        }
        binding.ivEdit.setOnClickListener {
            if (isNetworkAvailable()) openFragment(
                EditProfileFragment::class.java,
                null,
                true
            ) else showNotification()
        }
        binding.tbRemind.click {
            dialogTimer?.show(childFragmentManager, SettingFragment::class.java.name)
        }
        binding.tbContact.setOnClickListener {
            contact()
        }
    }

    private fun contact() {
        val packageInfo = try {
            requireContext().packageManager?.getPackageInfo(requireContext().packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        val appVersionCode = packageInfo?.let {
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.longVersionCode
            } else {
                it.versionCode.toLong()
            }
            versionCode
        } ?: -1L
        val phoneName = Build.MODEL
        val osVersion = Build.VERSION.RELEASE
        val osVersionCode = Build.VERSION.SDK_INT
        val address = getString(R.string.email)
        val subject = "${getString(R.string.app_name)} version: $appVersionCode"
        val body = "Vui lòng không xóa hoặc chỉnh sửa thông tin bên dưới\n" +
                "Device :$phoneName Android version: $osVersion int: $osVersionCode\n" +
                "\n" +
                "<Gõ tin nhắn của bạn ở đây> "
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
            .setData("mailto:$address".toUri())
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            selector = selectorIntent
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }

    private fun opDialogDelete() {
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_delete, null, false)
        val tvDelete = view.findViewById<TextView>(R.id.tvDelete)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        dialog.setContentView(view)
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
            resetData()
            dialog.dismiss()
        }
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes.gravity = Gravity.CENTER
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun resetData() {
        if (!isNetworkAvailable()) {
            showNotification()
            return
        }
        preferences.removeString(Constants.KEY_DAY)
        preferences.removeString(Constants.KEY_TOPIC)
        preferences.removeString(Constants.KEY_IMAGEVIEW)
        preferences.removeString(Constants.KEY_OPEN_SOUND)
        preferences.removeString(Constants.KEY_NUMBER_DAY)
        preferences.removeString(Constants.KEY_ATTENDANCE)
        preferences.setInt(Constants.KEY_HOUR, Constants.INDEX_19)
        preferences.setInt(Constants.KEY_MINUTE, Constants.INDEX_0)
        preferences.setBoolean(Constants.KEY_OPEN_SOUND, true)
        preferences.setInt(Constants.KEY_ID_LEVEL, Constants.INDEX_1)
        viewModel.startAlarmManager(Constants.INDEX_19, Constants.INDEX_0)
        showLoadingDialog()
        viewModel.resetData(viewModel.repository.getDeviceId()) {
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                setupTextTime(Constants.INDEX_19, Constants.INDEX_0)
                hideLoadingDialog()
                toast("Delete Account success")
            }
        }
        setupSound(true)
    }

    private fun showNotification() {
        requireContext().showNotifyInternet()
    }

    private fun clickSound() {
        openSound?.let {
            openSound = !it
            openSound?.let { sound ->
                setupSound(sound)
                preferences.setBoolean(Constants.KEY_OPEN_SOUND, sound)
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (dialogTimer != null && dialogLevel != null) {
            dialogTimer = null
            dialogLevel = null
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun startAlarmManager(newHour: Int, newMinute: Int) {
        if (Build.VERSION.SDK_INT >= 33)
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_PERMISSION_NOTIFICATION
                )
            } else {
                viewModel.startAlarmManager(newHour, newMinute)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_NOTIFICATION && grantResults[Constants.INDEX_0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.startAlarmManager(hour, minute)
        } else {
            toast("Vui lòng cấp quyền để sử dụng tính năng !")
        }
    }
}