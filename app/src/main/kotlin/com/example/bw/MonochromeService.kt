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

class MonochromeService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var isVolumeUp = false
    private var isVolumeDown = false

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false

        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> isVolumeUp = (event.action == KeyEvent.ACTION_DOWN)
            KeyEvent.KEYCODE_VOLUME_DOWN -> isVolumeDown = (event.action == KeyEvent.ACTION_DOWN)
        }

        if (isVolumeUp && isVolumeDown) {
            showColorDialog()
            return true
        }

        return super.onKeyEvent(event)
    }

    private fun showColorDialog() {
        // Basic dialog implementation
        val builder = AlertDialog.Builder(this)
        builder.setTitle("How many hours of color?")
        builder.setItems(arrayOf("1 Hour", "2 Hours", "Off")) { _, which ->
            when (which) {
                0 -> toggleGrayscale(false, 1 * 60 * 60 * 1000L)
                1 -> toggleGrayscale(false, 2 * 60 * 60 * 1000L)
                2 -> toggleGrayscale(true, 0)
            }
        }
        val dialog = builder.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
        dialog.show()
    }

    private fun toggleGrayscale(enabled: Boolean, duration: Long) {
        try {
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", if (enabled) 0 else 1)
            Toast.makeText(this, "Grayscale ${if (enabled) "off" else "on"}", Toast.LENGTH_SHORT).show()
            
            if (!enabled && duration > 0) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({ toggleGrayscale(true, 0) }, duration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
