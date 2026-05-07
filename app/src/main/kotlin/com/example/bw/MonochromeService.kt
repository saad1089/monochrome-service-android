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

    override fun onServiceConnected() {
        super.onServiceConnected()
        // When the user holds Vol Up + Vol Down for 3s, Android starts/connects the service.
        // We trigger our dialog immediately when that happens.
        showColorDialog()
    }

    private fun showColorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("How many hours of color?")
        builder.setItems(arrayOf("1 Hour", "2 Hours", "Keep Grayscale Off")) { _, which ->
            when (which) {
                0 -> toggleGrayscale(false, 1 * 60 * 60 * 1000L)
                1 -> toggleGrayscale(false, 2 * 60 * 60 * 1000L)
                2 -> toggleGrayscale(true, 0)
            }
            // After selection, we can disable the service to reset the shortcut state
            disableSelf() 
        }
        builder.setOnCancelListener { disableSelf() }
        
        val dialog = builder.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
        dialog.show()
    }

    private fun toggleGrayscale(enabled: Boolean, duration: Long) {
        try {
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", if (enabled) 0 else 1)
            Toast.makeText(this, "Grayscale ${if (enabled) "off" else "on"}", Toast.LENGTH_SHORT).show()
            
            if (!enabled && duration > 0) {
                // Use a system-level approach or a separate broadcast to handle the timer 
                // if the service is disabled, but for now we keep it simple.
                handler.postDelayed({ 
                    Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 1)
                }, duration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    override fun onKeyEvent(event: KeyEvent?): Boolean = false
}
