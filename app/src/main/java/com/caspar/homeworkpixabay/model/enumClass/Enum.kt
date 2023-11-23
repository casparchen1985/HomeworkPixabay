package com.caspar.homeworkpixabay.model.enumClass

enum class SearchType(val value: String) {
    ALL("all"),
    PHOTO("photo"),
    ILLUSTRATION("illustration"),
    VECTOR("vector");

    companion object {
        infix fun from(value: String?): SearchType? = SearchType.values().firstOrNull { it.value == value }
    }
}