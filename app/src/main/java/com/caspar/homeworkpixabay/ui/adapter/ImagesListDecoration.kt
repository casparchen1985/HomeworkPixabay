package com.caspar.homeworkpixabay.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.caspar.homeworkpixabay.ui.customized.DimensionCalculator.Companion.toPX

class ImagesListDecoration() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(0, 0, 0, 5.toPX)
    }
}