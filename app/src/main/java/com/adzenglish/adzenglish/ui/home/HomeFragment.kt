package com.adzenglish.adzenglish.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.adapter.LessonAdapter
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentHomeBinding
import com.adzenglish.adzenglish.databinding.LearnAgainLayoutBinding
import com.adzenglish.adzenglish.databinding.NotificationLayoutBinding
import com.adzenglish.adzenglish.models.local.room.entity.Lessons
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.chooselevel.ChooseLevelFragment
import com.adzenglish.adzenglish.ui.inapppurchase.BottomSheetDialogInApp
import com.adzenglish.adzenglish.ui.inapppurchase.PurchaseActivity
import com.adzenglish.adzenglish.ui.learn.DoConversionFragment
import com.adzenglish.adzenglish.ui.learn.checkpoint.CheckPointFragment
import com.adzenglish.adzenglish.ui.learn.grammar.IntroGrammarFragment
import com.adzenglish.adzenglish.ui.learn.practice.IntroPracticeLessonFragment
import com.adzenglish.adzenglish.ui.learn.vocabulary.IntroVocabularyFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxOneFragment
import com.adzenglish.adzenglish.ui.topic.box.BoxThreeFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.utils.extension.formatToDays
import com.adzenglish.adzenglish.utils.extension.formatToStringFinishLesson
import com.adzenglish.adzenglish.utils.extension.formatToStringLevel
import com.adzenglish.adzenglish.utils.extension.showNotifyInternet
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.adzenglish.adzenglish.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class HomeFragment : BaseFragmentWithBinding<FragmentHomeBinding>() {
    private val viewModel: ViewModel by viewModels()
    private val mainActivityVM: MainActivityVM by activityViewModels()
    private lateinit var lessonAdapter: LessonAdapter

    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Constants.KEY_NUMBER_DAY) binding.itemLevelHome.tvTopic.text =
                sharedPreferences.getInt(key, Constants.INDEX_0).formatToDays()
        }

    override fun init() {
        setupAdapter()
        viewModel.account.observe(viewLifecycleOwner) { account ->
            binding.tvCoin.text = (account?.gold ?: Constants.INDEX_0).toString()
        }
    }


    override fun initData() {
        initDataWithLevel()
        initDataWithLesson()
        if (isNetworkAvailable()) viewModel.getAccount(viewModel.repository.getDeviceId()) else {
            requireContext().showNotifyInternet()
            binding.tvCoin.text = String.format("?")
        }
    }

    override fun initAction() {
        binding.itemLevelHome.cartViewLevel.click {
            openFragment(ChooseLevelFragment::class.java, null, true)
        }
        binding.ivChest.setOnClickListener {
            openFragment(BoxOneFragment::class.java, null, true)
        }
        binding.tvCoin.setOnClickListener {
            if (isNetworkAvailable()) startActivity(
                Intent(
                    activity,
                    PurchaseActivity::class.java
                )
            ) else requireContext().showNotifyInternet()
        }
    }

    private fun showCustomNotification() {
        val binding =
            NotificationLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.TOP)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()
        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    private fun showDialogLearnAgain() {
        val binding = LearnAgainLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext()).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window?.attributes)
        val dialogWindowWidth = (displayWidth * 0.7f).toInt()
        val dialogWindowHeight = (displayHeight * 0.3f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog?.window?.attributes = layoutParams

        binding.tvVocabulary.click {
            navigateToFragment(dialog, IntroVocabularyFragment::class.java, Constants.INDEX_4)
        }
        binding.tvGrammar.click {
            navigateToFragment(dialog, IntroGrammarFragment::class.java, 4)
        }
        binding.tvPractice.click {
            navigateToFragment(dialog, IntroPracticeLessonFragment::class.java, Constants.INDEX_4)
        }
    }

    private fun openPurchaseActivity() {
        BottomSheetDialogInApp(
            getString(R.string.txt_recharge),
            getString(R.string.txt_recharge_content),
            getString(R.string.txt_to_store),
            R.drawable.iv_recharge
        ) {
            startPurchaseActivity()
        }.show(childFragmentManager, BoxThreeFragment::class.java.name)

    }

    private fun startPurchaseActivity() {
        startActivity(
            Intent(
                activity,
                PurchaseActivity::class.java
            )
        )
    }

    private fun navigateToFragment(dialog: AlertDialog, fragment: Class<*>, toFragment: Int) {
        dialog.dismiss()
        openFragment(fragment, null, true)
        mainActivityVM.setNavigateFragment(toFragment)
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun setupAdapter() {
        lessonAdapter = LessonAdapter(::onLessonClick, ::showDialogLearnAgain)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rcvLesson)
        binding.rcvLesson.adapter = lessonAdapter
    }

    private fun onLessonClick(lessons: Lessons?) {
        if (lessons == null) {
            showCustomNotification()
            return
        }

        lessons.id?.let { preferences.setInt(Constants.KEY_LESSON_ID, it) }
        val fragmentInfo = checkInitAdapter(lessons)

        if (fragmentInfo.first == CheckPointFragment::class.java) {
            handleCheckpointFragment(fragmentInfo)
        } else {
            openFragment(fragmentInfo.first, fragmentInfo.second, true)
        }
    }

    private fun handleCheckpointFragment(fragmentInfo: Pair<Class<*>, Bundle?>) {
        if (!isNetworkAvailable()) {
            hideLoadingDialog()
            requireContext().showNotifyInternet()
            return
        }

        val account = viewModel.account.value
        if (account == null) {
            openPurchaseActivity()
            return
        }

        val coin = account.gold ?: Constants.INDEX_0
        if (coin >= Constants.INDEX_5) {
            showDeductionDialog(coin, fragmentInfo)
        } else {
            openPurchaseActivity()
        }
    }

    private fun showDeductionDialog(coin: Int, fragmentInfo: Pair<Class<*>, Bundle?>) {
        BottomSheetDialogInApp(
            getString(R.string.txt_deduction),
            getString(R.string.txt_deduction_content),
            getString(R.string.txt_agree),
            R.drawable.iv_deduction
        ) {
            showLoadingDialog()
            viewModel.updateCoin(
                coin - Constants.INDEX_5,
                viewModel.account.value?.androidId
            ) { update ->
                hideLoadingDialog()
                if (update) openFragment(fragmentInfo.first, fragmentInfo.second, true)
                else toast("Có lỗi sảy ra vui lòng thử lại !")
            }
        }.show(childFragmentManager, BoxThreeFragment::class.java.name)
    }

    private fun checkInitAdapter(lessons: Lessons): Pair<Class<*>, Bundle?> {
        return when (lessons.type) {
            Constants.INDEX_1 -> {
                if (lessons.isFinishPartVocabulary == true) {
                    if (lessons.isFinishPartGrammar == true) {
                        Pair(IntroPracticeLessonFragment::class.java, null)
                    } else {
                        Pair(IntroGrammarFragment::class.java, null)
                    }
                } else {
                    Pair(IntroVocabularyFragment::class.java, null)
                }
            }

            Constants.INDEX_2 -> Pair(DoConversionFragment::class.java, getBundle(lessons))
            Constants.INDEX_3 -> Pair(CheckPointFragment::class.java, getBundle(lessons))
            else -> Pair(IntroVocabularyFragment::class.java, null)
        }
    }

    private fun getBundle(lessons: Lessons): Bundle {
        val bundle = Bundle()
        bundle.putInt(Constants.KEY_LESSON_POSITION, lessons.levelPosition)
        bundle.putInt(Constants.KEY_LEVEL_ID, lessons.levelId)
        return bundle
    }

    private fun initDataWithLevel() {
        viewModel.levelById.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    binding.apply {
                        itemLevelHome.tvTopic.text =
                            preferences.getInt(Constants.KEY_NUMBER_DAY).formatToDays()
                        itemLevelHome.tvLevel.text =
                            dataStatusResult.data?.position?.formatToStringLevel()
                        dataStatusResult.data?.lessonsCount?.let { lessonsCount ->
                            itemLevelHome.progressBar.max = lessonsCount
                            viewModel.listLearned.observe(viewLifecycleOwner) { total ->
                                val totalList = total.size
                                itemLevelHome.progressBar.progress = totalList
                                val value = (totalList.toFloat() / lessonsCount * 100)
                                val roundedValue = (round(value * 100) / 100).toBigDecimal()
                                itemLevelHome.tvProgress.text =
                                    roundedValue.formatToStringFinishLesson()
                            }
                            viewModel.getLessonLearned(true)
                        }

                        dataStatusResult.data?.image?.let {
                            try {
                                val ims: InputStream = requireContext().assets.open("image/$it")
                                val d = Drawable.createFromStream(ims, null)
                                itemLevelHome.imgDescLevel.setImageDrawable(d)
                            } catch (ex: IOException) {
                                return@let
                            }
                        }
                        dataStatusResult.data?.id?.let { viewModel.getAllLessonFromRoom(it) }
                    }
                }

                DataStatus.Status.ERROR -> {
                    toast("Error: ${dataStatusResult.message.toString()}")
                }
            }
        }
        viewModel.getLevelById(1)
    }

    private fun initDataWithLesson() {
        viewModel.lessonList.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> {
                    val listLessonsByLessonId =
                        dataStatusResult.data?.sortedBy { it.levelPosition }
                    lessonAdapter.submitList(listLessonsByLessonId)
                }

                DataStatus.Status.ERROR -> toast("Error: ${dataStatusResult.message.toString()}")
            }
        }
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }
}