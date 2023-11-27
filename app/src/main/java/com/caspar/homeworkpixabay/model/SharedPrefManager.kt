package com.caspar.homeworkpixabay.model

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager {
    companion object {
        private const val DEFAULT_INT = Int.MIN_VALUE
        private lateinit var prefs: SharedPreferences
        private lateinit var prefEditor: SharedPreferences.Editor

        fun setup(context: Context) {
            prefs = context.getSharedPreferences("HomeworkPixabay", Context.MODE_PRIVATE)
            prefEditor = prefs.edit()
        }
    }

    fun saveInt(key: String, value: Int) {
        prefEditor.putInt(key, value)
        prefEditor.commit()
    }

    fun readInt(key: String): Int {
        return prefs.getInt(key, DEFAULT_INT)
    }
}
