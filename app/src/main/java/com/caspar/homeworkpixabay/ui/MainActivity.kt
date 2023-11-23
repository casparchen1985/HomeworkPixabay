package com.caspar.homeworkpixabay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.caspar.homeworkpixabay.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var touchedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStart() {
        super.onStart()
        binding.root.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN &&
                (view != touchedView || view !is TextInputEditText)
            ) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                touchedView = view
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                this.currentFocus?.clearFocus()
                return@setOnTouchListener false
            }
            false
        }
    }
}