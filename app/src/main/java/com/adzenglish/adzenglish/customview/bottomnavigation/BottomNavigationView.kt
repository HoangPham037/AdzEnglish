package com.adzenglish.adzenglish.customview.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.databinding.BottomNavigationViewBinding
import com.adzenglish.adzenglish.utils.Constants.INDEX_0
import com.adzenglish.adzenglish.utils.Constants.INDEX_1
import com.adzenglish.adzenglish.utils.Constants.INDEX_2
import com.adzenglish.adzenglish.utils.Constants.INDEX_3

class BottomNavigationView : FrameLayout {
    private lateinit var binding: BottomNavigationViewBinding
    private var onSelectItemChanged: ((Int) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    @SuppressLint("ResourceAsColor")
    private fun init() {
        binding = BottomNavigationViewBinding.inflate(LayoutInflater.from(context), null, false)
        addView(binding.root)
        changeSelectMenu(INDEX_0)
        onClickItemListener()
    }

    fun changeSelectMenu(position: Int) {
        when (position) {
            INDEX_0 -> {
                setViewSelect(binding.imageHome)
                unSelectView(binding.imagePractice)
                unSelectView(binding.imageRank)
                unSelectView(binding.imageSetting)
            }

            INDEX_1 -> {
                unSelectView(binding.imageHome)
                setViewSelect(binding.imagePractice)
                unSelectView(binding.imageRank)
                unSelectView(binding.imageSetting)
            }

            INDEX_2 -> {
                unSelectView(binding.imageHome)
                unSelectView(binding.imagePractice)
                setViewSelect(binding.imageRank)
                unSelectView(binding.imageSetting)
            }

            INDEX_3 -> {
                unSelectView(binding.imageHome)
                unSelectView(binding.imagePractice)
                unSelectView(binding.imageRank)
                setViewSelect(binding.imageSetting)
            }
        }
    }

    private fun setViewSelect(imageView: ImageView) {
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.green_01))
    }

    private fun unSelectView(imageView: ImageView) {
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.gray_01))
    }

    private fun onClickItemListener() {
        binding.imageHome.setOnClickListener { onSelectItemChanged?.invoke(INDEX_0) }
        binding.imagePractice.setOnClickListener { onSelectItemChanged?.invoke(INDEX_1) }
        binding.imageRank.setOnClickListener { onSelectItemChanged?.invoke(INDEX_2) }
        binding.imageSetting.setOnClickListener { onSelectItemChanged?.invoke(INDEX_3) }
    }

    fun setOnClickItemClickListener(onClickItemClickListener: (Int) -> Unit) {
        this.onSelectItemChanged = onClickItemClickListener
    }

}