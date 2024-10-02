package com.adzenglish.adzenglish.ui.setting.level

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.adzenglish.adzenglish.databinding.FragmentBottomDialogLevelBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.DataStatus
import com.adzenglish.adzenglish.viewmodel.SettingVM
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetLevel(val viewModel: SettingVM, var preferences: Preferences) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentBottomDialogLevelBinding? = null
    private val binding get() = _binding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState != BottomSheetBehavior.STATE_EXPANDED) {
                            view?.height?.let { it1 -> behaviour.setPeekHeight(it1, true) }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomDialogLevelBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SettingLevelAdapter(viewModel) { id ->
            preferences.setInt(Constants.KEY_ID_LEVEL, id)
            viewModel.updateLevelIsSelectedById(id, Constants.INDEX_1)
        }
        binding?.rcvLevel?.adapter = adapter
        viewModel.levelList.observe(viewLifecycleOwner) { dataStatusResult ->
            when (dataStatusResult.status) {
                DataStatus.Status.SUCCESS -> adapter.submitList(dataStatusResult.data?.sortedBy { it.position })
                DataStatus.Status.ERROR -> Toast.makeText(
                    binding?.root?.context,
                    dataStatusResult.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.getAllLevelFromRoom()
        binding?.btnConfirm?.setOnClickListener { dismiss() }
        binding?.ivCloseBottom?.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}