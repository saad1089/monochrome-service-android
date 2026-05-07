package com.example.bw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import android.provider.Settings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Enforcing grayscale on boot.")
            try {
                Settings.Secure.putInt(context.contentResolver, "accessibility_display_daltonizer_enabled", 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
