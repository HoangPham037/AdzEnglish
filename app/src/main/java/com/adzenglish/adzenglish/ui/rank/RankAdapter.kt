package com.adzenglish.adzenglish.ui.rank

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.recyclerview.BaseRecyclerAdapter
import com.adzenglish.adzenglish.base.recyclerview.BaseViewHolder
import com.adzenglish.adzenglish.databinding.ItemRankBinding
import com.adzenglish.adzenglish.models.local.Account
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.ImageUtils
import com.adzenglish.adzenglish.utils.Utils
import com.adzenglish.adzenglish.utils.extension.gone
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.setInVisibility
import com.adzenglish.adzenglish.utils.extension.visible
import com.bumptech.glide.Glide

class RankAdapter(val androidId: String) : BaseRecyclerAdapter<Account, RankAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewDataBinding) : BaseViewHolder<Account>(binding) {
        override fun bind(itemData: Account?) {
            super.bind(itemData)
            if (binding is ItemRankBinding) {
                binding.space.goneIf(adapterPosition != listItem.size - Constants.INDEX_1)
                binding.tvName.text = String.format(
                    "${
                        if (itemData?.name?.isNotEmpty() == true) itemData.name else itemView.context.getString(
                            R.string.anonymous_users
                        )
                    }"
                )
                binding.viewItemRank.setBackgroundColor(itemView.context.getColor(if (itemData?.androidId == androidId) R.color.green_06 else R.color.white))
                val imageView = itemData?.imageView == null || itemData.imageView?.isEmpty() == true
                binding.tvAvatar.setInVisibility(imageView)
                binding.ivAvatar.setInVisibility(!imageView)
                if (imageView) itemData?.name?.let { name ->
                    if (name.isNotEmpty()) binding.tvAvatar.text =
                        itemData.name?.toCharArray()?.get(Constants.INDEX_0).toString()
                } else
                    Glide.with(itemView.context)
                        .load(ImageUtils.stringToBitmap(itemData?.imageView))
                        .fallback(R.drawable.ic_img_error)
                        .placeholder(R.drawable.ic_image_default)
                        .error(R.drawable.ic_img_error).into(binding.ivAvatar)
                if (adapterPosition < Utils.listNickname.size) {
                    binding.nickname.text = Utils.listNickname[adapterPosition]
                    binding.nickname.visible()
                } else {
                    binding.nickname.gone()
                }
                binding.uncoin.text = itemData?.point.toString()
                when (adapterPosition) {
                    Constants.INDEX_0 -> updateUi(binding, R.drawable.ic_top_1)
                    Constants.INDEX_1 -> updateUi(binding, R.drawable.ic_top_2)
                    Constants.INDEX_2 -> updateUi(binding, R.drawable.ic_top_3)
                    else -> {
                        binding.title.visible()
                        binding.imageView5.gone()
                        binding.title.text = (adapterPosition + Constants.INDEX_1).toString()
                    }
                }
            }
        }

        private fun updateUi(binding: ItemRankBinding, icTop: Int) {
            binding.title.gone()
            binding.imageView5.visible()
            binding.imageView5.setImageDrawable(itemView.context.getDrawable(icTop))
        }
    }

    override fun getItemLayoutResource(viewType: Int): Int {
        return R.layout.item_rank
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewHolderDataBinding(parent, viewType))
    }
}