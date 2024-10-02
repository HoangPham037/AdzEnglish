package com.adzenglish.adzenglish.ui.setting.remind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.adzenglish.adzenglish.databinding.FragmentBottomDialogTimerBinding
import com.adzenglish.adzenglish.models.local.sharepreference.Preferences
import com.adzenglish.adzenglish.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetTimer(var preferences: Preferences, val event: (Int, Int) -> Unit) :
    BottomSheetDialogFragment(), NumberPicker.OnValueChangeListener {
    var newHour = Constants.INDEX_0
    var newMinute = Constants.INDEX_0
    private lateinit var binding: FragmentBottomDialogTimerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomDialogTimerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newHour = preferences.getInt(Constants.KEY_HOUR)
        newMinute = preferences.getInt(Constants.KEY_MINUTE)
        binding.hrsPicker.setMinValue(Constants.INDEX_0)
        binding.minPicker.setMinValue(Constants.INDEX_0)
        binding.hrsPicker.setMaxValue(Constants.INDEX_23)
        binding.minPicker.setMaxValue(Constants.INDEX_59)
        binding.hrsPicker.value = newHour
        binding.minPicker.value = newMinute
        binding.hrsPicker.setOnValueChangedListener(this)
        binding.minPicker.setOnValueChangedListener(this)
        binding.hrsPicker.displayedValues = (Constants.INDEX_0..Constants.INDEX_23).map {
            it.toString().padStart(Constants.INDEX_2, '0')
        }.toTypedArray()
        binding.minPicker.displayedValues = (Constants.INDEX_0..Constants.INDEX_59).map {
            it.toString().padStart(Constants.INDEX_2, '0')
        }.toTypedArray()
        binding.tvDisable.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener {
            preferences.setInt(Constants.KEY_HOUR, newHour)
            preferences.setInt(Constants.KEY_MINUTE, newMinute)
            event.invoke(newHour, newMinute)
            dismiss()
        }
        binding.ivCloseBottom.setOnClickListener { dismiss() }
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        picker?.let {
            when (it) {
                binding.hrsPicker -> newHour = newVal
                binding.minPicker -> newMinute = newVal
            }
        }
    }
}