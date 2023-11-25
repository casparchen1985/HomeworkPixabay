package com.caspar.homeworkpixabay.ui.enumClass

enum class ImageDisplayType(val value: Int) {
    GRID(1),
    LIST(2);

    companion object {
        infix fun from(value: Int?): ImageDisplayType? = ImageDisplayType.values().firstOrNull { it.value == value }
    }
}