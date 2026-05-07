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
    private var isVolumeUpPressed = false
    private var isVolumeDownPressed = false
    private var isDialogShowing = false

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false

        val keyCode = event.keyCode
        val action = event.action

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            isVolumeUpPressed = (action == KeyEvent.ACTION_DOWN)
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            isVolumeDownPressed = (action == KeyEvent.ACTION_DOWN)
        }

        if (isVolumeUpPressed && isVolumeDownPressed && !isDialogShowing) {
            showColorDialog()
            return true // Consume the keys so volume doesn't change
        }

        // Return false to allow normal volume behavior when not triggered
        return false
    }

    private fun showColorDialog() {
        isDialogShowing = true
        val builder = AlertDialog.Builder(this)
        builder.setTitle("How many hours of color?")
        builder.setItems(arrayOf("1 Hour", "2 Hours", "Off")) { _, which ->
            isDialogShowing = false
            when (which) {
                0 -> toggleGrayscale(false, 1 * 60 * 60 * 1000L)
                1 -> toggleGrayscale(false, 2 * 60 * 60 * 1000L)
                2 -> toggleGrayscale(true, 0)
            }
        }
        builder.setOnCancelListener { isDialogShowing = false }
        
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
