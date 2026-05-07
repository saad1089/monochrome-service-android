package com.example.bw

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.KeyEvent
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.app.AlertDialog
import android.view.WindowManager

import android.widget.EditText
import android.text.InputType
import android.widget.LinearLayout

class MonochromeService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        showColorDialog()
    }

    private fun showColorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("How many minutes of color?")
        
        // Create an input field
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Minutes (Max 120)"
        
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        container.setPadding(padding, padding, padding, 0)
        container.addView(input, lp)
        
        builder.setView(container)

        builder.setPositiveButton("Start") { _, _ ->
            val minutesStr = input.text.toString()
            var minutes = minutesStr.toIntOrNull() ?: 0
            
            // Cap at 120 minutes
            if (minutes > 120) {
                minutes = 120
            }

            if (minutes > 0) {
                toggleGrayscale(false, minutes * 60 * 1000L)
            } else {
                toggleGrayscale(true, 0)
            }
            disableSelf()
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            disableSelf()
        }

        builder.setOnCancelListener { disableSelf() }
        
        val dialog = builder.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
        dialog.show()
    }

    private fun toggleGrayscale(enabled: Boolean, durationMs: Long) {
        try {
            // daltonizer_enabled: 1 = Grayscale ON, 0 = Grayscale OFF (Color)
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", if (enabled) 1 else 0)
            
            val status = if (enabled) "Grayscale ON" else "Color for ${durationMs / 60000} min"
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            
            if (!enabled && durationMs > 0) {
                // Clear any existing timers before setting a new one
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({ 
                    Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 1)
                    // We can't easily toast here if the service is disabled, but the setting will change.
                }, durationMs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    override fun onKeyEvent(event: KeyEvent?): Boolean = false
}
