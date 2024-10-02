package com.adzenglish.adzenglish.ui.practice.word

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.baseapp.BaseFragmentWithBinding
import com.adzenglish.adzenglish.databinding.FragmentPracticeWordBinding
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.ui.learn.vocabulary.IntroVocabularyFragment
import com.adzenglish.adzenglish.ui.practice.transplant.TransplantFromFrg
import com.adzenglish.adzenglish.ui.practice.transplant.WordAdapter
import com.adzenglish.adzenglish.ui.topic.box.BoxOneFragment
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.visible
import com.adzenglish.adzenglish.viewmodel.PracticeVM
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PracticeWordFragment : BaseFragmentWithBinding<FragmentPracticeWordBinding>(),
    ItemTouchHelpListener, MyTabSelectedListener {
    @Inject
    lateinit var preferences: Preferences
    private var adapter: WordAdapter? = null
    private var tabIndex = Constants.INDEX_0
    private val viewModel: PracticeVM by viewModels()
    private var listDictLearned: ArrayList<DictEntity> = arrayListOf()
    private var listDictStudying: ArrayList<DictEntity> = arrayListOf()
    override fun init() {
        viewModel.listWord.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> dataStatusResult.data?.let { getData(it) }
                DataStatus.Status.ERROR -> toast(dataStatusResult.message.toString())
            }
        }
        viewModel.lesson.observe(viewLifecycleOwner){
            binding.tvTitleLesson.text = it.title
        }
        switchDeleteItem()
    }

    private fun getData(listDict: List<DictEntity>) {
        listDictLearned.clear()
        listDictStudying.clear()
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            listDict.forEach { dict ->
                dict.form2 = ""
                if (dict.exampleEn.isEmpty()) listDictStudying.add(dict) else listDictLearned.add(
                    dict
                )
            }
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                val boolean = tabIndex == Constants.INDEX_0
                updateUi(if (boolean) listDictStudying else listDictLearned)
                binding.tvTotalWord.text = listDict.size.toString()
                setupTabLayout()
            }
        }
    }

    private fun setupTabLayout() {
        val tabStudying = binding.tabLayout.getTabAt(Constants.INDEX_0)
        val tabLearned = binding.tabLayout.getTabAt(Constants.INDEX_1)
        tabStudying?.text = String.format("Đang học ${listDictStudying.size}")
        tabLearned?.text = String.format("Đã học ${listDictLearned.size}")
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.tabLayout.setSelectedTabIndicatorColor(requireContext().getColor(R.color.orange))
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val boolean = tab?.position == Constants.INDEX_0
        tab?.position?.let { tabIndex = it }
        updateUi(if (boolean) listDictStudying else listDictLearned)
    }

    override fun initData() {
        viewModel.getListWord()
        viewModel.getLesson(preferences.getInt(Constants.KEY_LESSON_ID))
    }

    private fun updateUi(list: ArrayList<DictEntity>) {
        binding.tvReview.goneIf(list.size == Constants.INDEX_0 || list.isEmpty())
        adapter = WordAdapter(list)
        binding.rcvWord.adapter = adapter
        if (tabIndex == Constants.INDEX_0) {
            binding.btnCheck.gone()
        } else {
            binding.btnCheck.visible()
            val boolean = listDictLearned.size > Constants.INDEX_0
            binding.btnCheck.setTextColor(requireContext().getColor(if (boolean) R.color.white else R.color.gray_01))
            binding.btnCheck.setBackgroundResource(if (boolean) R.drawable.bg_text_view else R.drawable.bg_text_continue)
        }
    }

    override fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.openChest.setOnClickListener {
            openFragment(BoxOneFragment::class.java, null, true)
        }
        binding.openLessons.setOnClickListener {
            openFragment(IntroVocabularyFragment::class.java, null, true)
        }
        binding.btnCheck.setOnClickListener {
            showFragment()
        }
    }

    private fun showFragment() {
        if (listDictLearned.size > Constants.INDEX_0) {
            Bundle().apply {
                val jsonString = Gson().toJson(listDictLearned)
                putString(Constants.KEY_TRANSPLANT_FROM, jsonString)
                openFragment(TransplantFromFrg::class.java, this, true)
            }
        }
    }

    private fun switchDeleteItem() {
        val simpleItemTouchCallback =
            RecyclerViewItemTouchHelper(Constants.INDEX_0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.rcvWord)
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentPracticeWordBinding {
        return FragmentPracticeWordBinding.inflate(layoutInflater)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is WordAdapter.WordViewHolder) {
            adapter?.listDict?.get(viewHolder.adapterPosition)?.let { dictEntity ->
                dictEntity.exampleEn = if (dictEntity.exampleEn.isEmpty()) "true" else ""
                viewModel.updateDict(dictEntity)
            }
        }
    }
}