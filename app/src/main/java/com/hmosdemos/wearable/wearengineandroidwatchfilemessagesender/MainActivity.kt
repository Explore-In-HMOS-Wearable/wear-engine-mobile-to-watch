package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.*
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.WearEngineApp
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.ui.theme.WearEngineAndroidLiteFileMessageSenderTheme
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels.MainViewModel
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var deviceManager: DeviceManager
    private lateinit var authManager: AuthManager
    private lateinit var p2pManager: P2pManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeManagers()

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    authManager = authManager,
                    deviceManager = deviceManager,
                    p2pManager = p2pManager
                )
            )

            WearEngineAndroidLiteFileMessageSenderTheme {
                WearEngineApp(viewModel = viewModel)
            }
        }
    }

    private fun initializeManagers() {
        deviceManager = DeviceManager(this)
        authManager = AuthManager(this)
        p2pManager = P2pManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        p2pManager.unregisterReceiver()
    }
}