package com.example.pocketmindai.manager

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.util.Log

class SmartActionManager(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun executeActionsForContext(predictedContext: String) {
        Log.d("SmartActionManager", "Executing actions for context: $predictedContext")
        when (predictedContext.uppercase()) {
            "STUDY", "WORK" -> enableFocusMode()
            "GYM" -> enableGymMode()
            "SLEEP" -> enableSleepMode()
            "HOME" -> enableHomeMode()
            else -> resetToNormalMode()
        }
    }

    private fun enableFocusMode() {
        // Enable Do Not Disturb if permission is granted
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
        Log.d("SmartActionManager", "Focus Mode Enabled: DND On, Ringtone Muted")
    }

    private fun enableGymMode() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.8).toInt(), 0)
        // Intent to launch Spotify could go here
        Log.d("SmartActionManager", "Gym Mode Enabled: Volume Increased")
    }

    private fun enableSleepMode() {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        }
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
        Log.d("SmartActionManager", "Sleep Mode Enabled: Total Silence")
    }

    private fun enableHomeMode() {
        resetToNormalMode()
        Log.d("SmartActionManager", "Home Mode Enabled: Normal Settings")
    }

    private fun resetToNormalMode() {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
        val normalVolume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.5).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_RING, normalVolume, 0)
        Log.d("SmartActionManager", "Normal Mode Restored")
    }
}
