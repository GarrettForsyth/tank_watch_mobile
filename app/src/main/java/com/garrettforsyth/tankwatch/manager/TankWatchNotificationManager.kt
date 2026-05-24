package com.garrettforsyth.tankwatch.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.garrettforsyth.tankwatch.MainActivity
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.BreachType
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.ui.util.ValueFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TankWatchNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "tankwatch_alerts"
    }

    init {
        createNotificationChannel()
    }

    /**
     * Returns true if handled; false otherwise
     */
    fun handle(data: Map<String, String>): Boolean {
        var wasHandled = true // Less safe default is simpler / more readable here
        when (NotificationType.fromString(data["type"] ?: "")) {
            NotificationType.ThresholdBreach -> {
                handleThresholdBreach(data)
            }
            else -> {
                Log.w("FCM", "Unknown notification type: ${data["type"]}")
                wasHandled = false
            }
        }
        return wasHandled
    }

    private fun handleThresholdBreach(data: Map<String, String>) {
        val sensorId = data["sensorId"]?.toIntOrNull() ?: return
        val tankName = data["tankName"] ?: context.getString(R.string.unknown_tank)
        val sensorDisplayName = data["sensorDisplayName"] ?: data["sensorModel"] ?: context.getString(R.string.sensor)
        val value = data["value"]?.toDoubleOrNull() ?: return
        val measurementType = data["measurementType"] ?: return
        val breach = data["breach"] ?: return
        showThresholdAlert(sensorId, tankName, sensorDisplayName, value, measurementType, breach)
    }

    private fun showThresholdAlert(
        sensorId: Int,
        tankName: String,
        sensorDisplayName: String,
        value: Double,
        measurementType: String,
        breach: String
    ) {
        // Create message accounting for locale (language, measurement units)
        val title = context.getString(R.string.notification_tank_alert_title, tankName)
        val breachMessage = when (BreachType.fromString(breach)) {
            BreachType.Max -> context.getString(R.string.above_max)
            BreachType.Min -> context.getString(R.string.below_min)
            else -> context.getString(R.string.outside)
        }
        val mt = MeasurementType.fromString(measurementType)
        val body = context.getString(
            R.string.notification_tank_alert_message,
            sensorDisplayName,
            breachMessage,
            ValueFormatter.format(value, mt)
        )

        // Flags to pop everything above MainActivity on stack. This gives a clean slate to navigate from
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.INTENT_EXTRA_SENSOR_ID, sensorId)
        }

        // Flags to update the extras with the most current notification
        val pendingIntent = PendingIntent.getActivity(
            context,
            sensorId,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(sensorId, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_tank_alerts_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_tank_alerts_description)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    /**
     * The different types of notifications.
     *
     * ThresholdBreach: the server detected readings outside the user's configured threshold for a sensor
     */
    enum class NotificationType(val type: String) {
        ThresholdBreach("notification_threshold_breach");

        companion object {
            fun fromString(value: String): NotificationType? {
                return NotificationType.entries.firstOrNull {
                    it.type.equals(value, ignoreCase = true)
                }
            }
        }
    }
}