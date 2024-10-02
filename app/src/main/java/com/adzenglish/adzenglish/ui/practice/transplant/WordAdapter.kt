package com.adzenglish.adzenglish.ui.practice.transplant


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.base.media.MediaManager
import com.adzenglish.adzenglish.models.local.room.entity.DictEntity
import com.adzenglish.adzenglish.utils.extension.setInVisibility


class WordAdapter(val listDict: List<DictEntity>) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    private var dictTmp: DictEntity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_word_review, parent, false)
        return WordViewHolder(view)
    }

    override fun getItemCount() = listDict.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val dict = listDict[position]
        val boolean = dict.updatedAt.isEmpty()
        holder.ivSound.setInVisibility(!boolean)
        holder.tvWord.text = if (boolean) dict.wordRu else dict.wordEn
        holder.tvStudying.text =
            String.format(if (dict.exampleEn.isEmpty()) "Đã học" else "Đang học")
        holder.ivCheck.setImageResource(if (dict.exampleEn.isEmpty()) R.drawable.ic_check_false else R.drawable.ic_check_true)
        holder.ivSound.setOnClickListener {
            MediaManager.getInstance()?.playWithPath(dict.sound)
        }
        holder.itemView.setOnClickListener {
            updateUi(dict)
        }
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
        val tvWord = itemView.findViewById<TextView>(R.id.tvWord)
        val ivSound = itemView.findViewById<ImageView>(R.id.ivSound)
        val ivCheck = itemView.findViewById<ImageView>(R.id.ivCheck)
        val tvStudying = itemView.findViewById<TextView>(R.id.tvStudying)
        val layoutForeground = itemView.findViewById<ConstraintLayout>(R.id.layoutForeground)
    }
}