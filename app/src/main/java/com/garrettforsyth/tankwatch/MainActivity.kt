package com.garrettforsyth.tankwatch

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.garrettforsyth.tankwatch.navigation.TankWatchNavGraph
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.garrettforsyth.tankwatch.data.repository.TankWatchRepository
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tankWatchRepository: TankWatchRepository
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: For now just ask for permissions like this.
        // In the future, we'll create more robust permission requesting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }


        enableEdgeToEdge()
        setContent {
            TankWatchTheme {
                TankWatchNavGraph(viewModel)
            }
        }

        // Handle after setContent so the navigation graph is created
        handleNotificationIntent(intent)

        // For now - register the token everytime we enter the main activity
        // In the future, this should be moved to after a successful sign in
        registerFcmToken()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val sensorId = intent.getIntExtra("sensorId", -1)
        if (sensorId != -1) viewModel.onNotificationTapped(sensorId)
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Failed to get token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Token: $token")
            lifecycleScope.launch {
                tankWatchRepository.registerFcmToken(token)
            }
        }
    }

    companion object {
        const val INTENT_EXTRA_SENSOR_ID = "sensorId" // Used to deeplink navigate to sensor details
    }
}