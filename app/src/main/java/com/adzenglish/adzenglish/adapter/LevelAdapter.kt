package com.adzenglish.adzenglish.adapter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemChooseLevelBinding
import com.adzenglish.adzenglish.models.local.room.entity.LevelEntity
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.click
import com.adzenglish.adzenglish.viewmodel.ViewModel
import java.io.IOException
import java.io.InputStream


class LevelAdapter(var viewModel: ViewModel,private val screenWith: Int, private val event: (Int) -> Unit) :
    BaseRecyclerAdapter<LevelEntity, LevelAdapter.LevelViewHolder>() {
    private var level : LevelEntity? = null
    inner class LevelViewHolder(private val binding: ViewDataBinding) :
        BaseViewHolder<LevelEntity>(binding) {
        override fun bind(itemData: LevelEntity?) {
            super.bind(itemData)
            if (binding is ItemChooseLevelBinding) {
                if (itemData?.isSelected == 1) level = itemData
                binding.apply {
                    tvLevel.text = String.format("%s", "Cấp độ ${itemData?.position}")
                    tvTitle.text = itemData?.description
                    tvProgress.text = String.format("%s", "Tổng ${itemData?.lessonsCount} bài")
                    itemData?.let { levels->
                        binding.tvAction.text = if (levels.isSelected == 1) itemView.context.resources.getText(R.string.txt_continue) else itemView.context.resources.getText(R.string.txt_come_here)
                        binding.tvAction.click {
                            if (level?.id == itemData.id) {
                                event.invoke(-1)
                                return@click
                            }
                            level?.let {
                                viewModel.updateLevelIsSelectedById(it.id, Constants.INDEX_0)
                            }
                            level = itemData
                            event.invoke(itemData.id)
                        }
                    }

                    try {
                        val ims: InputStream =
                            itemView.context.assets.open("image/${itemData?.image}")
                        val d = Drawable.createFromStream(ims, null)
                        imgLevel.setImageDrawable(d)
                    } catch (ex: IOException) {
                        return
                    }
                    val layoutParams = root.layoutParams
                    layoutParams.width = screenWith - 400
                    root.layoutParams = layoutParams
                }
            }
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_choose_level
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        return LevelViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}