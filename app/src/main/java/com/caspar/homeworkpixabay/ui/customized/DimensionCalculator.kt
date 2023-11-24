package com.caspar.homeworkpixabay.ui.customized

import android.content.res.Resources

class DimensionCalculator {
    companion object {
        val Int.toDP: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
        val Int.toPX: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}