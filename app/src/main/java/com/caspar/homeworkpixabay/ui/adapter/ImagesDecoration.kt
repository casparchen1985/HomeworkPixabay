package com.caspar.homeworkpixabay.ui.adapter

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.caspar.homeworkpixabay.ui.customized.DimensionCalculator.Companion.toPX

class ImagesDecoration(private val borderWidthDP: Int) : RecyclerView.ItemDecoration() {
    private val halfBorderPX = borderWidthDP.toPX / 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val allItemCount = parent.adapter?.itemCount ?: 0
        val spanIndex = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex

        val lm = parent.layoutManager as StaggeredGridLayoutManager
        val orientation = lm.orientation
        val spanCount = lm.spanCount

        val restItemCount = if (allItemCount % spanCount == 0) spanCount else allItemCount % spanCount

        when (spanIndex) {
            0 -> {
                if (orientation == LinearLayout.VERTICAL) {
                    outRect.left = 2 * halfBorderPX
                    outRect.top = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.right = halfBorderPX
                    outRect.bottom =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                } else {
                    outRect.left = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.top = 2 * halfBorderPX
                    outRect.right =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                    outRect.bottom = halfBorderPX
                }
            }

            spanCount - 1 -> {
                if (orientation == LinearLayout.VERTICAL) {
                    outRect.left = halfBorderPX
                    outRect.top = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.right = 2 * halfBorderPX
                    outRect.bottom =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                } else {
                    outRect.left = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.top = halfBorderPX
                    outRect.right =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                    outRect.bottom = 2 * halfBorderPX
                }
            }

            else -> {
                if (orientation == LinearLayout.VERTICAL) {
                    outRect.left = halfBorderPX
                    outRect.top = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.right = halfBorderPX
                    outRect.bottom =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                } else {
                    outRect.left = if (position / spanCount == 0) 2 * halfBorderPX else halfBorderPX
                    outRect.top = halfBorderPX
                    outRect.right =
                        if (position in allItemCount - restItemCount..<allItemCount) 2 * halfBorderPX else halfBorderPX
                    outRect.bottom = halfBorderPX
                }
            }
        }
    }
}