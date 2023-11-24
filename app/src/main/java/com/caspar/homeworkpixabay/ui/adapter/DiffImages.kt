package com.caspar.homeworkpixabay.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.caspar.homeworkpixabay.model.dataClass.Hit

class DiffImages : DiffUtil.ItemCallback<Hit>() {
    override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
        return oldItem == newItem
    }
}