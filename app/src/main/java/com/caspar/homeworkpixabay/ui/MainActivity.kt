package com.caspar.homeworkpixabay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import com.caspar.homeworkpixabay.R
import com.caspar.homeworkpixabay.databinding.ActivityMainBinding
import com.caspar.homeworkpixabay.model.SharedPrefManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var touchedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        SharedPrefManager.setup(this)

        val setting = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(setting)

            fetchAndActivate().addOnCompleteListener { task ->
                Toast.makeText(
                    this@MainActivity,
                    if (task.isSuccessful) "RemoteConfig fetched and Activated" else "RemoteConfig fetch failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (configUpdate.updatedKeys.contains(resources.getString(R.string.display_type_name))) {
                        this@apply.activate().addOnCompleteListener {
                            Toast.makeText(this@MainActivity, "[Monitor] RemoteConfig updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    Toast.makeText(this@MainActivity, "[Monitor] RemoteConfig update failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

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