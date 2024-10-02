package com.adzenglish.adzenglish.ui.practice.skill


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.extension.setInVisibility

class ResultWordAdapter(val listDict: List<DictEntity>) :
    RecyclerView.Adapter<ResultWordAdapter.WordViewHolder>() {
    private var dictTmp: DictEntity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_word_result, parent, false)
        return WordViewHolder(view)
    }

    override fun getItemCount() = listDict.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val dict = listDict[position]
        val boolean = dict.updatedAt.isEmpty()
        holder.ivSound.setInVisibility(!boolean)
        holder.ivCheck.setImageResource(R.drawable.ic_check_true)
        holder.tvWord.text = if (boolean) dict.wordRu else dict.wordEn
        holder.tvWord.setTextColor(holder.itemView.context.getColor(if (dict.isAnswer) R.color.green_01 else R.color.red_01))
        holder.ivSound.setOnClickListener { MediaManager.getInstance()?.playWithPath(dict.sound) }
        holder.itemView.setOnClickListener { updateUi(dict) }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUi(dict: DictEntity) {
        dict.updatedAt = if (dict.updatedAt.isEmpty()) "true" else ""
        if (dictTmp?.id != dict.id) {
            dictTmp?.updatedAt = "true"
            dictTmp = dict
        }
        notifyDataSetChanged()
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val ivSound: ImageView = itemView.findViewById(R.id.ivSound)
        val ivCheck: ImageView = itemView.findViewById(R.id.ivCheck)
    }
}