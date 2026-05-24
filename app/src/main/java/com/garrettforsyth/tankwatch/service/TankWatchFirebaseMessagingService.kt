package com.garrettforsyth.tankwatch.service

import android.util.Log
import com.garrettforsyth.tankwatch.manager.TankWatchNotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class TankWatchFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationManager: TankWatchNotificationManager
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message received: ${message.data}")
        if (!notificationManager.handle(message.data)) {
            Log.w("FCM", "No handler found for message.")
        }
    }
}